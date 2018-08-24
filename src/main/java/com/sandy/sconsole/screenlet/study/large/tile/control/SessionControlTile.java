package com.sandy.sconsole.screenlet.study.large.tile.control;

import static com.sandy.sconsole.core.CoreEventID.* ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;
import static com.sandy.sconsole.core.screenlet.Screenlet.RunState.* ;

import java.sql.* ;
import java.util.* ;
import java.util.Date ;

import org.apache.log4j.* ;
import org.springframework.context.* ;

import com.sandy.common.bus.* ;
import com.sandy.sconsole.* ;
import com.sandy.sconsole.core.remote.* ;
import com.sandy.sconsole.core.screenlet.* ;
import com.sandy.sconsole.core.screenlet.Screenlet.* ;
import com.sandy.sconsole.core.util.* ;
import com.sandy.sconsole.dao.entity.* ;
import com.sandy.sconsole.dao.entity.master.* ;
import com.sandy.sconsole.dao.repository.* ;
import com.sandy.sconsole.dao.repository.master.* ;
import com.sandy.sconsole.screenlet.study.* ;

@SuppressWarnings( "serial" )
public class SessionControlTile extends SessionControlTileUI 
    implements SecondTickListener {
    
    static final Logger log = Logger.getLogger( SessionControlTile.class ) ;
    
    private RunState runState = RunState.STOPPED ;
    
    private int runTime = 0 ;
    @SuppressWarnings( "unused" )
    private int pauseTime = 0 ;
    private int totalPauseTime = 0 ;
    
    private Timestamp lapStartTime = null ;
    private int lapTime = 0 ;
    private int numProblemsLeftInChapter = 0 ;
    
    private StudyScreenlet screenlet = null ;
    
    private Session session = null ;
    
    private ProblemRepository        problemRepo = null ;
    private SessionRepository        sessionRepo = null ;
    private LastSessionRepository    lastSessionRepo = null ;
    private ProblemAttemptRepository problemAttemptRepo = null ;
    
    private Queue<Problem> unsolvedProblems = new LinkedList<>() ;

    public SessionControlTile( ScreenletPanel parent ) {
        super( parent ) ;
        screenlet = ( StudyScreenlet )parent.getScreenlet() ;
        kaMgr = screenlet.getKeyActivationManager() ;
        
        ApplicationContext ctx = SConsole.getAppContext() ;
        
        problemRepo        = ctx.getBean( ProblemRepository.class ) ;
        sessionRepo        = ctx.getBean( SessionRepository.class ) ;
        lastSessionRepo    = ctx.getBean( LastSessionRepository.class ) ;
        problemAttemptRepo = ctx.getBean( ProblemAttemptRepository.class ) ;
        
        SConsole.addSecTimerTask( this ) ;
        getEventBus().addSubscriberForEventTypes( this, false, 
                                                    SCREENLET_PLAY, 
                                                    SCREENLET_PAUSE, 
                                                    SCREENLET_RESUME, 
                                                    SCREENLET_STOP ) ;
    }

    public void populateLastSessionDetails( Session lastSession ) {
        
        kaMgr.disableAllKeys() ;
        
        activateProblemOutcomeButtons( false ) ;
        updateLapTimeLabel( 0 ) ;
        
        setBtn2( Btn2Type.CHANGE ) ;
        
        this.runTime = 0 ;
        this.lapTime = 0 ;
        this.pauseTime = 0 ;
        this.totalPauseTime = 0 ;
        
        session = new Session() ;
        if( lastSession == null ) {
            // There has been no last session. Keep everything blank and
            // enable only the change button.
            setBtn1( Btn1Type.CLEAR ) ;
            
            updateNumSkippedLabel( 0 ) ;
            updateNumSolvedLabel( 0 ) ;
            updateNumRedoLabel( 0 ) ;
            updateNumPigeonLabel( 0 ) ;
            updateSessionTimeLabel( 0 ) ;
        }
        else {
            // Use the last session as a template for this session.
            setSessionType( lastSession.getSessionType() ) ;
            setTopic( lastSession.getTopic() ) ;
            setBook( lastSession.getBook() ) ;
            
            updateNumSkippedLabel( lastSession.getNumSkipped() ) ;
            updateNumSolvedLabel( lastSession.getNumSolved() ) ;
            updateNumRedoLabel( lastSession.getNumRedo() ) ;
            updateNumPigeonLabel( lastSession.getNumPigeon() ) ;
            updateSessionTimeLabel( lastSession.getDuration() ) ;
        }
    }
    
    public void setSessionType( String sessionType ) {
        session.setSessionType( sessionType ) ;
        setSessionTypeIcon( sessionType ) ;
    }
    
    public void setTopic( Topic topic ) {
        session.setTopic( topic ) ;
        setTopicLabel( session.getTopic().getTopicName() ) ;
    }
    
    public void setBook( Book book ) {
        session.setBook( book ) ;
        if( book == null ) {
            setBookLabel( null ) ;
        }
        else {
            setBookLabel( session.getBook().getBookShortName() ) ;
            loadUnsolvedProblems() ;
            setNextProblem() ;
        }
    }
    
    private void setNextProblem() {
        
        Problem nextProblem = unsolvedProblems.poll() ;
        if( nextProblem != null ) {
            session.setLastProblem( nextProblem ) ;
            setProblemLabel( nextProblem ) ;
            setBtn1( Btn1Type.PLAY ) ;
        }
        else {
            if( runState == RUNNING ) {
                getScreenlet().processStopKey() ;
            }
        }
    }
    
    private void loadUnsolvedProblems() {
        
        ArrayList<Problem> problems = new ArrayList<>() ;
        Integer topicId = session.getTopic().getId() ;
        Integer bookId  = session.getBook().getId() ;
        
        problems.addAll( problemRepo.findUnsolvedProblems( topicId, bookId ) ) ;
        unsolvedProblems.clear() ;
        
        if( !problems.isEmpty() ) {
            
            List<Problem> found = new ArrayList<>() ;

            // First pass - go through the problems and collect all redo problems
            for( Problem p : problems ) {
                if( p.getRedo() ) {
                    found.add( p ) ;
                }
            }
            unsolvedProblems.addAll( found ) ;
            problems.removeAll( found ) ;
            found.clear() ;
            
            // Second pass - find all the questions whose id is greater than
            // or equal to the the sessions problem and which are not skipped
            int refProblemId = -1 ;
            Problem currentProblem = session.getLastProblem() ;
            if( currentProblem != null ) {
                refProblemId = currentProblem.getId() ;
            }
            for( Problem p : problems ) {
                if( p.getId() >= refProblemId && !p.getSkipped() ) {
                    found.add( p ) ;
                }
            }
            unsolvedProblems.addAll( found ) ;
            problems.removeAll( found ) ;
            found.clear() ;
            
            // Third pass - Find all the problems which are not skipped and add
            for( Problem p : problems ) {
                if( !p.getSkipped() ) {
                    found.add( p ) ;
                }
            }
            unsolvedProblems.addAll( found ) ;
            problems.removeAll( found ) ;
            found.clear() ;
            
            
            // Fourth pass - add all the remaining problems to the unsolved
            // problem list
            unsolvedProblems.addAll( problems ) ;
        }
        
        log.debug( "Session has " + unsolvedProblems.size() + " unsolved problems." ) ;
        numProblemsLeftInChapter = unsolvedProblems.size() ;
        updateNumProblemsLeftInChapter( unsolvedProblems.size() ) ;
    }

    protected void activateProblemOutcomeButtons( boolean activate ) {
        super.activateProblemOutcomeButtons( activate ) ;
        if( !activate ) {
            kaMgr.enableKey( false, FN_A, FN_B, FN_C, FN_D, FN_E ) ;
            kaMgr.clearFnKeyFeature( FN_A, FN_B, FN_C, FN_D, FN_E );
        }
        else {
            kaMgr.enableFnKey( FN_A, new FnKeyHandler() { public void process() { skipProblem() ; } } ) ;
            kaMgr.enableFnKey( FN_B, new FnKeyHandler() { public void process() { problemSolved() ; } }  ) ;
            kaMgr.enableFnKey( FN_C, new FnKeyHandler() { public void process() { redoProblem() ; } }  ) ;
            kaMgr.enableFnKey( FN_D, new FnKeyHandler() { public void process() { setPigeon() ; } }  ) ;
            kaMgr.enableFnKey( FN_E, new FnKeyHandler() { public void process() { setStarred() ; } }  ) ;
        }
    }
    
    @Override
    public void secondTicked( Calendar calendar ) {
        if( runState == RUNNING ) {
            runTime++ ;
            lapTime++ ;
            updateSessionTimeLabel( runTime ) ;
            if( session.getSessionType().equals( Session.TYPE_EXERCISE ) ) {
                updateLapTimeLabel( lapTime ) ;
            }
        }
        else if( runState == PAUSED ) {
            pauseTime++ ;
            totalPauseTime++ ;
        }
    }

    @Override
    public void handleEvent( Event event ) {
        
        AbstractScreenlet screenlet = getScreenlet() ;
        runState = screenlet.getCurrentRunState() ;
        
        switch( event.getEventType() ) {
            case SCREENLET_PLAY:
                play() ;
                break ;
                
            case SCREENLET_PAUSE:
                pause() ;
                break ;
                
            case SCREENLET_RESUME:
                resume() ;
                pauseTime = 0 ;
                break ;
                
            case SCREENLET_STOP:
                stop() ;
                pauseTime = 0 ;
                break ;
        }
    }
    
    private void play() {
        log.debug( "Starting the session" ) ;
        resume() ;
        
        Date now = new Date() ;
        session.setStartTime( new Timestamp( now.getTime() ) ) ;
        session.setNumSkipped( 0 ) ;
        session.setNumSolved( 0 ) ;
        session.setNumRedo( 0 ) ;
        session.setNumPigeon( 0 ) ;
        saveSession() ;
        
        updateNumSkippedLabel( 0 ) ;
        updateNumSolvedLabel( 0 ) ;
        updateNumRedoLabel( 0 ) ;
        updateNumPigeonLabel( 0 ) ;
        updateSessionTimeLabel( 0 ) ;
        
        lapStartTime = new Timestamp( now.getTime() ) ;
        lastSessionRepo.update( getScreenlet().getDisplayName(), session.getId() ) ;
    }
    
    private void pause() {
        log.debug( "Pausing the session" ) ;
        kaMgr.disableAllKeys() ;

        setBtn1( Btn1Type.PLAY ) ;
        setBtn2( Btn2Type.STOP ) ;

        // If session type = Exercise, disable the lap buttons
        if( session.getSessionType().equals( Session.TYPE_EXERCISE ) ) {
            activateProblemOutcomeButtons( false ) ;
        }
        saveSession() ;
    }
    
    private void resume() {
        log.debug( "Resuming the session" ) ;
        kaMgr.disableAllKeys() ;
        
        // If session type = Exercise, enable the lap buttons
        if( session.getSessionType().equals( Session.TYPE_EXERCISE ) ) {
            activateProblemOutcomeButtons( true ) ;
        }
        
        setBtn1( Btn1Type.PAUSE ) ;
        setBtn2( Btn2Type.STOP ) ;
        
        pauseTime = 0 ;
        saveSession() ;
    }
    
    private void stop() {
        log.debug( "Ending the session" ) ;
        saveSession() ;
        populateLastSessionDetails( session.clone() ) ;
    }
    
    private void saveProblem( boolean solved, boolean redo, 
                              boolean skipped, boolean pigeoned ) {
        
        String outcome = null ;
        if( solved ) outcome = ProblemAttempt.OUTCOME_SOLVED ;
        else if( redo ) outcome = ProblemAttempt.OUTCOME_REDO ;
        else if( skipped ) outcome = ProblemAttempt.OUTCOME_SKIP ;
        else if( pigeoned ) outcome = ProblemAttempt.OUTCOME_PIGEON ;
        
        
        Problem p = session.getLastProblem() ;
        p.setSolved( solved ) ;
        p.setRedo( redo ) ;
        p.setSkipped( skipped ) ;
        p.setPigeoned( pigeoned ) ;
        
        problemRepo.save( p ) ;
        
        ProblemAttempt attempt = new ProblemAttempt() ;
        attempt.setSession( session ) ;
        attempt.setProblem( session.getLastProblem() ) ;
        attempt.setStartTime( lapStartTime ) ;
        attempt.setEndTime( new Timestamp( new Date().getTime() ) ) ;
        attempt.setDuration( lapTime ) ;
        attempt.setOutcome( outcome ) ;
        
        problemAttemptRepo.save( attempt ) ;
        saveSession() ;
        
        lapStartTime = new Timestamp( new Date().getTime() ) ;
    }
    
    private void saveSession() {
        
        Date now = new Date() ;
        session.setEndTime( new Timestamp( now.getTime() ) ) ;
        session.setDuration( runTime ) ;
        session.setAbsoluteDuration( runTime + totalPauseTime ) ;
        session = sessionRepo.save( session ) ;
    }
    
    private void skipProblem() {
        log.debug( "Skipping the problem" ) ;
        saveProblem( false, false, true, false ) ;
        lapTime = 0 ;
        updateNumSkippedLabel( session.incrementNumSkipped() ) ;
        setNextProblem() ;
    }
    
    private void problemSolved() {
        log.debug( "Solved the problem" ) ;
        saveProblem( true, false, false, false ) ;
        lapTime = 0 ;
        updateNumSolvedLabel( session.incrementNumSolved() ) ;
        setNextProblem() ;
        
        numProblemsLeftInChapter-- ;
        updateNumProblemsLeftInChapter( numProblemsLeftInChapter ) ;
    }
    
    private void redoProblem() {
        log.debug( "Redo the problem" ) ;
        saveProblem( false, true, false, false ) ;
        lapTime = 0 ;
        updateNumRedoLabel( session.incrementNumRedo() ) ;
        setNextProblem() ;
    }
    
    private void setPigeon() {
        log.debug( "Set a pigeon" ) ;
        saveProblem( false, false, false, true ) ;
        lapTime = 0 ;
        updateNumPigeonLabel( session.incrementNumPigeon() ) ;
        setNextProblem() ;
    }
    
    private void setStarred() {
        log.debug( "Un/starring the problem" ) ;
        Problem p = session.getLastProblem() ;
        p.setStarred( !p.getStarred() ) ;
        problemRepo.save( p ) ;
        super.setProblemLabel( p ) ;
    }

    @Override
    protected void changeSessionDetails() {
        log.debug( "Change session details called." ) ;
    }
}
