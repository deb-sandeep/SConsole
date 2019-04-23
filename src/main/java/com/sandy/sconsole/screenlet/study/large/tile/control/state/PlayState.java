package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import java.sql.Timestamp ;
import java.util.Calendar ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.analysis.PAARecordUpdater ;
import com.sandy.sconsole.api.remote.RemoteController ;
import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.remote.KeyType ;
import com.sandy.sconsole.core.screenlet.Screenlet ;
import com.sandy.sconsole.core.screenlet.Screenlet.RunState ;
import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.core.util.SecondTickListener ;
import com.sandy.sconsole.dao.entity.ProblemAttempt ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.dao.entity.Session.SessionType ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.Btn1Type ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.Btn2Type ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.OutcomeButtonsState ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionInformation ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.PauseDialog ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.TopicSelectionDialog ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.TopicSelectionDialog.TopicSelectionListener ;

/**
 * Transition In:
 * --------------
 *  HomeState    -> Key.PLAY
 *  ChangeState  -> Key.PLAY
 *  
 * 
 * Transition Out:
 * ---------------
 *  Key.STOP     -> HomeState
 *  
 * Self Transitions:
 * -----------------
 *  Key.FN_A      -> Mark problem as Solved
 *  Key.FN_B      -> Mark problem as Redo
 *  Key.FN_C      -> Mark problem as Pigeon
 *  Key.FN_D      -> Mark problem as Skip
 *  Key.PLAYPAUSE -> Show pause dialog / Resume
 *  
 * Programmatic Transitions:
 * -------------------------
 *  Key.PLAYPAUSE -> Resume session
 *  Key.STOP      -> Transition to HomeState
 */
public class PlayState extends BaseControlTileState 
    implements SecondTickListener, TopicSelectionListener {

    private static final Logger log = Logger.getLogger( PlayState.class ) ;
    
    public static final String NAME = "Play" ;
    
    private PauseDialog pauseDialog = null ;
    private TopicSelectionDialog topicSelectionDialog = null ;
    
    private ProblemAttempt problemAttempt = null ;
    
    private int runTime = 0 ;
    private int pauseTime = 0 ;
    private int lapTime = 0 ;
    private int projectedDuration = 0 ;
    
    private int numProblemsLeftInBook = 0 ;
    
    private long lastPersistentUpdateTime = 0 ;
    
    private PAARecordUpdater analysisUpdater = null ;
    
    public PlayState( SessionControlTile tile, StudyScreenletLargePanel screenletPanel ) {
        super( NAME, tile, screenletPanel ) ;
        
        topicSelectionDialog = new TopicSelectionDialog( this ) ;
        analysisUpdater = new PAARecordUpdater() ;
        
        addTransition( Key.FN_A, "Solved", this ) ;
        addTransition( Key.FN_B, "Redo",   this ) ;
        addTransition( Key.FN_C, "Pigeon", this ) ;
        addTransition( Key.FN_D, "Skip",   this ) ;
        addTransition( Key.FN_E, "*",      this ) ;
        addTransition( Key.FN_F, "Ignore", this ) ;
        addTransition( Key.FN_G, "Move",   this ) ;
        
        addTransition( Key.PLAYPAUSE, this ) ;
        
        pauseDialog = new PauseDialog( this ) ;
    }

    @Override public String getCenterPanelCardName() {
        return RemoteController.CENTER_PROJ_PANEL ;
    }
    
    @Override
    public void resetState() {
        si = null ;
        runTime = 0 ;
        pauseTime = 0 ;
        lapTime = 0 ;
        projectedDuration = 0 ;
        problemAttempt = null ;
        lastPersistentUpdateTime = 0 ;
    }

    @Override
    public boolean preActivate( Object payload, State fromState, Key key ) {
        
        // If this activation is not due to a self transition, then only do this
        if( fromState != this ) {
            if( (payload == null) || !(payload instanceof SessionInformation) ) {
                throw new IllegalArgumentException( 
                        "PlayState activation payload is null or is not of "
                        + "type SessionInformation" ) ;
            }
            
            super.disableAllTransitions() ;
            
            // Note that the session information has been sanitized for starting
            // the session immediately.
            this.si = ( SessionInformation )payload ;
            
            // Save the session in the database. Post this point, the blank
            // session will contain a valid session id and any further saves
            // will end up updating the session instead of creating a new one
            log.debug( "Creating a new session in database" ) ;
            this.si.session.setStartTime( new Timestamp( System.currentTimeMillis() ) ) ;
            updateSession() ;
            
            log.debug( "Saving as the latest session for " + getSubjectName() ) ;
            lastSessionRepo.update( getSubjectName(), si.session.getId() ) ;
            
            // If the session is an exercise, we pick the first unsolved 
            // problem and create a problem attempt to track the outcome of
            // the problem
            if( si.session.getSessionType() == SessionType.EXERCISE ) {
                numProblemsLeftInBook = si.unsolvedProblems.size() ;
                problemAttempt = createNewProblemAttempt() ;
            }
            
            // Start the timers and active self transitions
            SConsole.addSecTimerTask( this ) ;
            
            // Initialize the UI. 
            log.debug( "Initializing play state UI and necessary transitions." ) ;
            initializeUIAndTransitions( fromState ) ;
        }
        
        // Let all the triggers for activation be handled. In case this is
        // a play transition  from outside, the playpause method will detect
        // it and set the runstate to true.
        return true ;
    }
    
    private void initializeUIAndTransitions( State fromState ) {
        // Note that any invalidations will have been removed before the 
        // transition happened. 
        
        // Doesn't matter where the transition has come from, we have to
        // activate the pause and stop buttons
        log.debug( "Enabling play/pause and stop buttons and transitions" ) ;
        tile.setBtn1UI( Btn1Type.PAUSE ) ;
        tile.setBtn2UI( Btn2Type.STOP ) ;
        enableTransition( Key.PLAYPAUSE, Key.STOP ) ;
        
        // If the session is an Exercise, we need to activate the problem
        // outcome controls and transitions
        if( si.session.getSessionType() == SessionType.EXERCISE ) {
            log.debug( "Session is an exercise. Activating problem outcome UI" ) ;
            tile.updateOutcomeCounts( si.session ) ;
            tile.setOutcomeButtonsState( OutcomeButtonsState.ACTIVE ) ;
            enableTransition( Key.FN_A, Key.FN_B, Key.FN_C, Key.FN_D, 
                              Key.FN_E, Key.FN_F, Key.FN_G );
        }
        
        tile.updateSessionTimeLabel( runTime ) ;
        
        // If the transition is happening from Change, replace the buttons 
        // pause and stop and clear any change trigger highlights
        if( fromState.getName().equals( ChangeState.NAME ) ) {
            log.debug( "Transition has happened from Change. Clearing change UI highlights" ) ;
            tile.clearChangeUIHighlights() ;
        }
    }
    
    private void updateSession() {
        
        boolean publishCreationEvent = si.session.getId() == null ? true : false ;
        
        si.session.setDuration( this.runTime ) ;
        si.session.setAbsoluteDuration( this.runTime + this.pauseTime ) ;
        si.session.setEndTime( new Timestamp( System.currentTimeMillis() ) ) ;
        sessionRepo.save( si.session ) ;
        
        lastPersistentUpdateTime = System.currentTimeMillis() ;
        
        if( publishCreationEvent ) {
            SConsole.GLOBAL_EVENT_BUS
                    .publishEvent( EventCatalog.SESSION_STARTED, si.session ) ;
        }
    }
    
    private ProblemAttempt createNewProblemAttempt() {
        
        ProblemAttempt attempt = null ;
        Problem problem = null ;
        
        if( si.unsolvedProblems.isEmpty() ) {
            log.debug( "No more unsolved problems left." ) ;
            return null ;
        }
        
        lapTime = 0 ;
        projectedDuration = 0 ;
        
        tile.updateLapTimeLabel( 0 ) ;
        tile.updateLapTimeProjection( 0 ) ;
                
        problem = si.unsolvedProblems.remove( 0 ) ;
        attempt = new ProblemAttempt() ;
        attempt.setDuration( lapTime ) ;
        attempt.setProjectedDuration( projectedDuration ) ;
        attempt.setProblem( problem ) ;
        attempt.setStartTime( new Timestamp( System.currentTimeMillis() ) ) ;
        attempt.setSession( si.session ) ;
        
        tile.setProblemLabel( problem ) ;
        
        SConsole.GLOBAL_EVENT_BUS
                .publishEvent( EventCatalog.PROBLEM_ATTEMPT_STARTED, attempt ) ;
        
        return attempt ;
    }
    
    private void saveProblemAttemptAndLoadNextProblem( String outcome ) {
        
        Session session = si.session ;
        
        // Store the current problem attempt outcome in the database.
        problemAttempt.setOutcome( outcome ) ;
        problemAttempt.setEndTime( new Timestamp( System.currentTimeMillis() ) ) ;
        problemAttempt.setProjectedDuration( projectedDuration ) ;
        problemAttempt.setDuration( lapTime ) ;
        problemAttemptRepo.save( problemAttempt ) ;
        
        Problem problemForAnalysis = problemAttempt.getProblem() ;
        
        if( outcome.equals( ProblemAttempt.OUTCOME_SOLVED ) || 
            outcome.equals( ProblemAttempt.OUTCOME_REDO )   || 
            outcome.equals( ProblemAttempt.OUTCOME_PIGEON ) ) {
            new Thread(() -> {
                try {
                    log.debug( "Spawning async analysis for problem " +  
                               problemForAnalysis.getId() ) ;
                    analysisUpdater.updateAnalysis( problemForAnalysis ) ;
                }
                catch( Exception e ) {
                    log.error( "Error analyzing problem attempts.", e ) ;
                }
            }).start() ;
        }
                
        // Update the problem master with the problem outcome details
        Problem problem = problemAttempt.getProblem() ;
        if( outcome.equals( ProblemAttempt.OUTCOME_SOLVED ) ) {
            problem.setSolved( true ) ;
            session.incrementNumSolved() ;
            tile.updateNumProblemsLeftInBookLabel( --numProblemsLeftInBook ) ;
        }
        else if( outcome.equals( ProblemAttempt.OUTCOME_REDO   ) ) {
            problem.setRedo( true ) ;
            session.incrementNumRedo() ;
        }
        else if( outcome.equals( ProblemAttempt.OUTCOME_PIGEON ) ) {
            problem.setPigeoned( true ) ;
            session.incrementNumPigeon() ;
            tile.updateNumProblemsLeftInBookLabel( --numProblemsLeftInBook ) ;
        }
        else if( outcome.equals( ProblemAttempt.OUTCOME_SKIP   ) ) {
            problem.setSkipped( true ) ;
            session.incrementNumSkipped() ;
        }
        else if( outcome.equals( ProblemAttempt.OUTCOME_IGNORE ) ) {
            problem.setSolved( true ) ;
            problem.setIgnored( true ) ;
            session.incrementNumIgnored() ;
            tile.updateNumProblemsLeftInBookLabel( --numProblemsLeftInBook ) ;
        }
        else if( outcome.equals( ProblemAttempt.OUTCOME_MOVE ) ) {
            tile.updateNumProblemsLeftInBookLabel( --numProblemsLeftInBook ) ;
        }
        
        problemRepo.save( problem ) ;
        tile.updateOutcomeCounts( session ) ;
        
        // Update the session.
        updateSession() ;
        
        ProblemAttempt attempt = problemAttempt ;
        SConsole.GLOBAL_EVENT_BUS
                .publishEvent( EventCatalog.PROBLEM_ATTEMPT_ENDED, attempt ) ;
        
        if( outcome.equals( ProblemAttempt.OUTCOME_SOLVED ) || 
            outcome.equals( ProblemAttempt.OUTCOME_IGNORE ) || 
            outcome.equals( ProblemAttempt.OUTCOME_MOVE ) ) {
            publishRefreshBurnInfo() ;
        }        
        
        // Load the next problem. If there are no more problems, the session 
        // has to end.
        problemAttempt = createNewProblemAttempt() ;
        if( problemAttempt == null ) {
            showMessage( "No more problems left in this book" ) ;
            SConsole.getApp().postSoftwareRemoteKeyEvent( Key.STOP ) ;
        }
    }

    /* This method is called when the state machine is about to execute a 
     * transition from this state. We take this opportunity to do some
     * cleanup work - for example, remove ourselves from the list of second
     * timer task listeners.
     * 
     * NOTE that this method is also called during self transitions. Hence
     * before removing ourselves from the second listener list, we must check
     * if the next state is not a self transition.
     */
    @Override
    public void deactivate( State nextState, Key key ) {
        super.deactivate( nextState, key ) ;
        
        if( nextState != this ) {
            tile.getScreenlet().setCurrentRunState( RunState.STOPPED ) ;
            SConsole.removeSecTimerTask( this ) ;
            
            updateSession() ;
            
            SConsole.GLOBAL_EVENT_BUS
                    .publishEvent( EventCatalog.SESSION_ENDED, si.session ) ;
            
            if( si.session.getSessionType() == SessionType.EXERCISE ) {
                tile.setOutcomeButtonsState( OutcomeButtonsState.INACTIVE ) ;
                tile.updateLapTimeLabel( -1 ) ;
                // If there are any problem attempts currently underway,
                // ignore them. This problem will be picked when a new 
                // session is started.
            }
            resetState() ;
        }
    }

    /* This method is called whenever Key.RUN causes a transition. This may
     * be with one of the two intentions - Play or Resume. We decide which,
     * based on the current run state.
     */
    @Override
    public void handlePlayPauseResumeKey() {
        
        Screenlet screenlet = tile.getScreenlet() ;
        if( screenlet.getRunState() != RunState.RUNNING ) {
            // Set the state to running, so that the next Key.PLAY transition
            // can be treated as intention to pause.
            tile.getScreenlet().setCurrentRunState( RunState.RUNNING ) ;
        }
        else {
            tile.getScreenlet().setCurrentRunState( RunState.PAUSED ) ;
            showDialog( pauseDialog ) ;
        }
    }

    @Override
    public void handleFnAKey() {
        log.debug( "Current problem marked as Solved." ) ;
        saveProblemAttemptAndLoadNextProblem( ProblemAttempt.OUTCOME_SOLVED ) ;
    }

    @Override
    public void handleFnBKey() {
        log.debug( "Current problem marked as Redo." ) ;
        saveProblemAttemptAndLoadNextProblem( ProblemAttempt.OUTCOME_REDO ) ;
    }

    @Override
    public void handleFnCKey() {
        log.debug( "Current problem marked as Pigeon." ) ;
        saveProblemAttemptAndLoadNextProblem( ProblemAttempt.OUTCOME_PIGEON ) ;
    }
    
    @Override
    public void handleFnDKey() {
        log.debug( "Current problem marked as Skip." ) ;
        saveProblemAttemptAndLoadNextProblem( ProblemAttempt.OUTCOME_SKIP ) ;
    }
    
    @Override
    public void handleFnEKey() {
        log.debug( "Current problem marked as Star." ) ;
        Problem problem = problemAttempt.getProblem() ;
        
        problem.setStarred( !problem.getStarred() ) ;
        tile.setProblemLabel( problem ) ;
        problemRepo.save( problem ) ;
    }

    @Override
    public void handleFnFKey() {
        log.debug( "Current problem marked as Ignore." ) ;
        saveProblemAttemptAndLoadNextProblem( ProblemAttempt.OUTCOME_IGNORE ) ;
    }
    
    @Override
    public void handleFnGKey() {
        log.debug( "Attempting to move the current problem to another topic." ) ;
        // Note that the dialog will call the handleNewTopicSelection when
        // the user selects a new topic.
        showDialog( topicSelectionDialog ) ;
    }

    @Override
    public void secondTicked( Calendar calendar ) {
        if( tile.getScreenlet().getRunState() == RunState.RUNNING ) {
            runTime++ ;
            tile.updateSessionTimeLabel( runTime ) ;
            
            if( problemAttempt != null ) {
                lapTime++ ;
                tile.updateLapTimeLabel( lapTime ) ;
            }
            
            si.session.setDuration( this.runTime ) ;
            si.session.setAbsoluteDuration( this.runTime + this.pauseTime ) ;
            si.session.setEndTime( new Timestamp( System.currentTimeMillis() ) ) ;
            
            if( (System.currentTimeMillis() - lastPersistentUpdateTime) > 5000 ) {
                updateSession() ;
            }
            
            SConsole.GLOBAL_EVENT_BUS
                    .publishEvent( EventCatalog.SESSION_PLAY_HEARTBEAT, 
                                   this.si.session ) ;
        }
        else {
            pauseTime++ ;
        }
    }

    @Override
    public Topic getDefaultTopic() {
        return this.si.session.getTopic() ;
    }

    /**
     * NOTE: Don't get confused with the method name. This method is 
     * being called for moving the current problem to a new topic.
     */
    @Override
    public void handleNewTopicSelection( Topic newTopic ) {
        log.debug( "Topic to move the current problem to - " + newTopic ) ;
        
        Problem problem = problemAttempt.getProblem() ;
        problem.setTopic( newTopic ) ;
        problemRepo.save( problem ) ;
        
        // NOTE that this would necessitate a burn refresh since the 
        // number of problems in this topic has reduced. We would need to
        // reflect the same in other tiles such as burn status and 
        // the burn graph
        
        saveProblemAttemptAndLoadNextProblem( ProblemAttempt.OUTCOME_MOVE ) ;
    }

    public void handleNonTransitionMappedKey( Key key ) {
        if( key.getKeyType() == KeyType.TIME_PROJ ) {
            updateTimeProjection( Integer.parseInt( key.getKeyCode() ) ) ;
        }
    }
    
    private void updateTimeProjection( int projectedDuration ) {
        this.projectedDuration = projectedDuration ;
        tile.updateLapTimeProjection( projectedDuration ) ;
    }
}
