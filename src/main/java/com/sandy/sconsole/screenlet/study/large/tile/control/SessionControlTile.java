package com.sandy.sconsole.screenlet.study.large.tile.control;

import static com.sandy.sconsole.core.frame.UIConstant.* ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;
import static com.sandy.sconsole.core.screenlet.Screenlet.RunState.* ;

import java.awt.* ;
import java.sql.* ;
import java.util.* ;
import java.util.Date ;
import java.util.List ;
import java.util.Queue ;

import javax.swing.* ;

import org.apache.log4j.* ;
import org.springframework.context.* ;

import com.sandy.sconsole.* ;
import com.sandy.sconsole.api.remote.* ;
import com.sandy.sconsole.core.frame.* ;
import com.sandy.sconsole.core.remote.* ;
import com.sandy.sconsole.core.screenlet.* ;
import com.sandy.sconsole.core.screenlet.Screenlet.* ;
import com.sandy.sconsole.core.util.* ;
import com.sandy.sconsole.dao.entity.* ;
import com.sandy.sconsole.dao.entity.master.* ;
import com.sandy.sconsole.dao.repository.* ;
import com.sandy.sconsole.dao.repository.master.* ;
import com.sandy.sconsole.screenlet.study.* ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.* ;

@SuppressWarnings( "serial" )
public class SessionControlTile extends SessionControlTileUI 
    implements SecondTickListener, RemoteKeyListener {
    
    static final Logger log = Logger.getLogger( SessionControlTile.class ) ;
    
    private class ChangeSelection {
        
        String sessionType = null ;
        Topic topic = null ;
        Book book = null ;
        Problem problem = null ;
        
        public ChangeSelection() {
            if( session != null ) {
                sessionType = session.getSessionType() ;
                topic = session.getTopic() ;
                book = session.getBook() ;
                problem = session.getLastProblem() ;
            }
        }
        
        public Session createSession() {
            Session session = new Session() ;
            session.setSessionType( sessionType ) ;
            session.setTopic( topic ) ;
            session.setBook( book ) ;
            session.setLastProblem( problem ) ;
            return session ;
        }
    }
    
    private RunState runState = RunState.STOPPED ;
    
    private int runTime = 0 ;
    private int totalPauseTime = 0 ;
    
    private Timestamp lapStartTime = null ;
    private int lapTime = 0 ;
    private int numProblemsLeftInChapter = 0 ;
    
    private StudyScreenlet screenlet = null ;
    
    private ApplicationContext ctx = null ;
    private Session session = null ;
    
    private ProblemRepository        problemRepo = null ;
    private SessionRepository        sessionRepo = null ;
    private LastSessionRepository    lastSessionRepo = null ;
    private ProblemAttemptRepository problemAttemptRepo = null ;
    
    private RemoteController remoteController = null ;
    
    private Queue<Problem> unsolvedProblems = new LinkedList<>() ;
    
    private ChangeSelection changeSelection = null ;
    
    private PauseDialog pauseDialog = null ;
    private SessionTypeChangeDialog typeChangeDialog = null ;
    private TopicChangeDialog topicChangeDialog = null ;

    public SessionControlTile( ScreenletPanel parent ) {
        super( parent ) ;
        screenlet = ( StudyScreenlet )parent.getScreenlet() ;
        keyProcessor = new RemoteKeyEventProcessor( "Study Control", this ) ;
        
        ctx = SConsole.getAppContext() ;
        
        problemRepo        = ctx.getBean( ProblemRepository.class ) ;
        sessionRepo        = ctx.getBean( SessionRepository.class ) ;
        lastSessionRepo    = ctx.getBean( LastSessionRepository.class ) ;
        problemAttemptRepo = ctx.getBean( ProblemAttemptRepository.class ) ;
        
        remoteController = ctx.getBean( RemoteController.class ) ;

        pauseDialog = new PauseDialog() ;
        typeChangeDialog = new SessionTypeChangeDialog() ;
        topicChangeDialog = new TopicChangeDialog( this ) ;

        SConsole.addSecTimerTask( this ) ;
    }

    protected void screenletMaximized() {
        super.screenletMaximized() ;
        remoteController.pushKeyProcessor( keyProcessor ) ; 
    }
    
    protected void screenletMinimized() {
        super.screenletMinimized() ;
        remoteController.popKeyProcessor() ;
    }
    
    protected void screenletRunStateChanged( Screenlet screenlet ) {
        super.screenletRunStateChanged( screenlet ) ;
        runState = screenlet.getRunState() ;
    }

    public void populateLastSessionDetails( Session lastSession ) {
        
        keyProcessor.disableAllKeys() ;
        
        activateProblemOutcomeButtons( false ) ;
        updateLapTimeLabel( 0 ) ;
        
        setBtn2( Btn2Type.CHANGE ) ;
        
        this.runTime = 0 ;
        this.lapTime = 0 ;
        this.totalPauseTime = 0 ;
        
        clearOutcomeStatusAndControls( true ) ;
        
        session = new Session() ;
        
        if( lastSession == null ) {
            // There has been no last session. Keep everything blank and
            // enable only the change button.
            setBtn1( Btn1Type.CLEAR ) ;
        }
        else {
            // Use the last session as a template for this session.
            String sessionType = lastSession.getSessionType() ;
            setSessionType( lastSession.getSessionType() ) ;
            setTopic( lastSession.getTopic() ) ;
            updateSessionTimeLabel( lastSession.getDuration() ) ;
            
            if( sessionType.equals( Session.TYPE_EXERCISE ) ) {
                setBook( lastSession.getBook() ) ;
                setProblem( lastSession.getLastProblem() ) ;
                
                updateNumSkippedLabel( lastSession.getNumSkipped() ) ;
                updateNumSolvedLabel( lastSession.getNumSolved() ) ;
                updateNumRedoLabel( lastSession.getNumRedo() ) ;
                updateNumPigeonLabel( lastSession.getNumPigeon() ) ;
                
                loadUnsolvedProblems() ;
                setNextProblem() ;
            }
            else {
                setBook( lastSession.getBook() ) ;
                session.setLastProblem( lastSession.getLastProblem() ) ;
            }
        }
        
        setCurrentUseCase( UseCase.STOP_SESSION ) ;
        validateSessionDetailsAndActivatePlay() ;
    }
    
    private void setSessionType( String sessionType ) {
        session.setSessionType( sessionType ) ;
        setSessionTypeIcon( sessionType ) ;
    }
    
    private void setTopic( Topic topic ) {
        session.setTopic( topic ) ;
        setTopicLabel( session.getTopic().getTopicName() ) ;
    }
    
    private void setBook( Book book ) {
        session.setBook( book ) ;
        setBookLabel( book ) ;
    }
    
    private void setProblem( Problem problem ) {
        session.setLastProblem( problem ) ;
        setProblemLabel( problem ) ;
    }
    
    private void setNextProblem() {
        
        Problem nextProblem = unsolvedProblems.poll() ;
        setProblem( nextProblem ) ;
        
        if( nextProblem == null ) {
            if( runState == RUNNING ) {
                handleStopKey() ;
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
        updateNumProblemsLeftInChapterLabel( unsolvedProblems.size() ) ;
    }

    protected void activateProblemOutcomeButtons( boolean activate ) {
        super.activateProblemOutcomeButtons( activate ) ;
        if( !activate ) {
            keyProcessor.setKeyEnabled( false, FN_A, FN_B, FN_C, FN_D, FN_E ) ;
            keyProcessor.clearFnHandler( FN_A, FN_B, FN_C, FN_D, FN_E );
        }
        else {
            keyProcessor.setFnHandler( FN_A, new Handler() { public void handle() { skipProblem() ; } } ) ;
            keyProcessor.setFnHandler( FN_B, new Handler() { public void handle() { problemSolved() ; } }  ) ;
            keyProcessor.setFnHandler( FN_C, new Handler() { public void handle() { redoProblem() ; } }  ) ;
            keyProcessor.setFnHandler( FN_D, new Handler() { public void handle() { setPigeon() ; } }  ) ;
            keyProcessor.setFnHandler( FN_E, new Handler() { public void handle() { setStarred() ; } }  ) ;
            keyProcessor.setKeyEnabled( true, FN_A, FN_B, FN_C, FN_D, FN_E ) ;
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
            totalPauseTime++ ;
        }
    }
    
    // --------------- Remote key processing [Start] ---------------------------

    @Override
    public void handlePlayPauseResumeKey() {
        
        AbstractScreenlet screenlet = getScreenlet() ;
        runState = screenlet.getRunState() ;
        log.debug( "Play/Pause/Resume command received" ) ;
        log.debug( "  Current run state = " + runState ) ;
        
        switch( runState ) {
            case RUNNING:
                log.debug( "\tPausing" ) ;
                pause() ;
                setCurrentUseCase( UseCase.PAUSE_SESSION ) ;
                screenlet.setCurrentRunState( PAUSED ) ;
                break ;
                
            case STOPPED:
                log.debug( "\tPlaying" ) ;
                play() ;
                setCurrentUseCase( UseCase.PLAY_SESSION ) ;
                screenlet.setCurrentRunState( RUNNING ) ;
                break ;
                
            case PAUSED:
                log.debug( "\tResuming" ) ;
                resume() ;
                setCurrentUseCase( UseCase.PLAY_SESSION ) ;
                screenlet.setCurrentRunState( RUNNING ) ;
                break ;
        }
    }
    
    @Override
    public void handleStopKey() {
        log.debug( "Ending the session" ) ;
        saveSession() ;
        setCurrentUseCase( UseCase.STOP_SESSION ) ;
        screenlet.setCurrentRunState( STOPPED ) ;
        populateLastSessionDetails( session.clone() ) ;
    }
    
    @Override public void handleLeftNavKey() {}
    @Override public void handleRightNavKey() {}
    @Override public void handleUpNavKey() {}
    @Override public void handleDownNavKey() {}
    @Override public void handleSelectNavKey() {}
    
    // --------------- Remote key processing [End] -----------------------------

    private void play() {
        log.debug( "Starting the session" ) ;
        
        if( getCurrentUseCase() == UseCase.CHANGE_SESSION ) {
            session = changeSelection.createSession() ;
            changeSelection = null ;
            deactivateControlPanelForChange() ;
        }
        
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
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                SConsoleFrame frame = SConsole.getApp().getFrame() ;
                Integer userAction = ( Integer )frame.showDialog( pauseDialog ) ;
                
                if( userAction == PauseDialog.PLAY_ACTION ) {
                    handlePlayPauseResumeKey() ;
                }
                else {
                    handleStopKey() ;
                }
            }
        });
    }
    
    private void resume() {
        log.debug( "Resuming the session" ) ;
        keyProcessor.disableAllKeys() ;
        
        // If session type = Exercise, enable the lap buttons
        if( session.getSessionType().equals( Session.TYPE_EXERCISE ) ) {
            activateProblemOutcomeButtons( true ) ;
        }
        
        setBtn1( Btn1Type.PAUSE ) ;
        setBtn2( Btn2Type.STOP ) ;
        
        saveSession() ;
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
        updateNumProblemsLeftInChapterLabel( numProblemsLeftInChapter ) ;
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
    
    protected void deactivateControlPanelForChange() {
        log.debug( "De-activating control panel for change." ) ;
        
        typePnl.setBackground( BG_COLOR ) ;
        topicPnl.setBackground( BG_COLOR ) ;
        bookPnl.setBackground( BG_COLOR ) ;
        problemPnl.setBackground( BG_COLOR ) ;
        
        typeLbl.setForeground( TYPE_LBL_FG ) ;
        topicLbl.setForeground( TOPIC_LBL_FG ) ;
        bookLbl.setForeground( BOOK_LBL_FG ) ;
        problemLbl.setForeground( PROBLEM_LBL_FG ) ;
        
        keyProcessor.disableAllKeys() ;
        keyProcessor.clearFnHandler( FN_A, FN_B, FN_C, FN_D, FN_CANCEL ) ;
        
        highlightPanelValidity( typePnl, true ) ;
        highlightPanelValidity( topicPnl, true ) ;
        highlightPanelValidity( bookPnl, true ) ;
        highlightPanelValidity( problemPnl, true ) ;
    }

    protected void activateControlPanelForChange( String sessionType ) {
        
        log.debug( "Activating control panel for change." ) ;
        
        // First set everything to deactivated state
        deactivateControlPanelForChange() ;
        
        typePnl.setBackground( FN_A_COLOR ) ;
        typeLbl.setForeground( Color.WHITE ) ;
        
        topicPnl.setBackground( FN_B_COLOR ) ;
        topicLbl.setForeground( Color.WHITE ) ;

        keyProcessor.setFnHandler( FN_A,      new Handler() { public void handle(){ changeSessionType() ; } } ) ;
        keyProcessor.setFnHandler( FN_B,      new Handler() { public void handle(){ changeTopic() ; } }  ) ;
        keyProcessor.setFnHandler( FN_CANCEL, new Handler() { public void handle(){ cancelChange() ; } }  ) ;
        keyProcessor.setKeyEnabled( true, FN_A, FN_B ) ;
        
        if( sessionType != null && 
            sessionType.equals( Session.TYPE_EXERCISE ) ) {
            
            bookPnl.setBackground( FN_C_COLOR ) ;
            bookLbl.setForeground( Color.WHITE ) ;
            
            problemPnl.setBackground( FN_D_COLOR ) ;
            problemLbl.setForeground( Color.WHITE ) ;
            
            keyProcessor.setFnHandler( FN_C, new Handler() { public void handle(){ changeBook() ; } }  ) ;
            keyProcessor.setFnHandler( FN_D, new Handler() { public void handle(){ changeProblem() ; } }  ) ;
            keyProcessor.setKeyEnabled( true, FN_C, FN_D ) ;
        }
        
        setBtn2( Btn2Type.CANCEL ) ;
        setBtn1( Btn1Type.PLAY ) ;
    }
    
    @Override
    protected void executeChangeSessionDetailsUseCase() {
        log.debug( "Executing change session details." ) ;
        
        changeSelection = new ChangeSelection() ;
        activateControlPanelForChange( changeSelection.sessionType ) ;
        setCurrentUseCase( UseCase.CHANGE_SESSION ) ;
    }
    
    private void changeSessionType() {
        log.debug( "Executing changeSessionType" ) ;
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                invokeChangeSessionTypeAsync() ;
            }
        });
    }
    
    private void invokeChangeSessionTypeAsync() {
        
        SConsoleFrame frame = SConsole.getApp().getFrame() ;
        String type = ( String )frame.showDialog( typeChangeDialog ) ;
        
        log.debug( "New session type chosen = " + type ) ;
        if( type != null ) {
            changeSelection.sessionType = type ;
            setSessionTypeIcon( type ) ;
            
            // If the new session is of type Theory or Lecture,
            // we need to blank out the following :
            //  - problem label
            //  - num labels
            //  - lap time
            
            activateControlPanelForChange( type ) ;
            
            if( type.equals( Session.TYPE_EXERCISE ) ) {
                clearOutcomeStatusAndControls( false ) ;
            }
            else {
                changeSelection.problem = null ;
                changeSelection.book = null ;
                
                setProblemLabel( null ) ;
                setBookLabel( null ) ;
                
                clearOutcomeStatusAndControls( true ) ;
            }
        }
        
        validateSessionDetailsAndActivatePlay() ;
    }
    
    private void validateSessionDetailsAndActivatePlay() {
        
        List<JPanel> invalidPanels = validateSessionData() ;
        
        if( !invalidPanels.isEmpty() ) {
            for( JPanel invalidPanel : invalidPanels ) {
                highlightPanelValidity( invalidPanel, false ) ;
                setBtn1( Btn1Type.CLEAR ) ;
            }
        }
        else {
            highlightPanelValidity( typePnl, true ) ;
            highlightPanelValidity( topicPnl, true ) ;
            highlightPanelValidity( bookPnl, true ) ;
            highlightPanelValidity( problemPnl, true ) ;
            setBtn1( Btn1Type.PLAY ) ;
        }
    }
    
    private List<JPanel> validateSessionData() {
        
        List<JPanel> invalidAttributePanels = new ArrayList<JPanel>() ;
        
        String sessionType = null ;
        Topic topic = null ;
        Book book = null ;
        Problem problem = null ;
        
        if( changeSelection != null ) {
            sessionType = changeSelection.sessionType ;
            topic = changeSelection.topic ;
            book = changeSelection.book ;
            problem = changeSelection.problem ;
        }
        else if( session != null ){
            sessionType = session.getSessionType() ;
            topic = session.getTopic() ;
            book = session.getBook() ;
            problem = session.getLastProblem() ;
        }
        
        if( sessionType == null ) {
            invalidAttributePanels.add( typePnl ) ;
        }
        
        if( topic == null ) {
            invalidAttributePanels.add( topicPnl ) ;
        }
        else {
            if( sessionType.equals( Session.TYPE_EXERCISE ) ) {
                if( book == null ) {
                    invalidAttributePanels.add( bookPnl ) ;
                }
                if( problem == null ) {
                    invalidAttributePanels.add( problemPnl ) ;
                }
            }
            else {
                if( problem != null ) {
                    invalidAttributePanels.add( problemPnl ) ;
                }
                
                if( book != null ) {
                    invalidAttributePanels.add( bookPnl ) ;
                }
            }
        }
        return invalidAttributePanels ;
    }
    
    private void changeTopic() {
        log.debug( "Executing changeTopic" ) ;
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                invokeChangeTopicAsync() ;
            }
        });
    }
    
    private void invokeChangeTopicAsync() {
        
        SConsoleFrame frame = SConsole.getApp().getFrame() ;
        Topic selectedTopic = ( Topic )frame.showDialog( topicChangeDialog ) ;
        
        log.debug( "New topic chosen = " + selectedTopic ) ;
        if( selectedTopic != null ) {
            changeSelection.topic = selectedTopic ;
            setTopicLabel( selectedTopic.getTopicName() ) ;
        }
        // TODO: Change the book and the problems intelligently
    }
    
    private void changeBook() {
        log.debug( "Executing changeBook" ) ;

    }
    
    private void changeProblem() {
        log.debug( "Executing changeProblem" ) ;

    }
    
    private void cancelChange() {
        
        log.debug( "Executing cancelChange" ) ;
        changeSelection = null ;
        setBtn2( Btn2Type.CHANGE ) ;
        setCurrentUseCase( UseCase.STOP_SESSION ) ;
        deactivateControlPanelForChange() ;
        populateLastSessionDetails( session.clone() ) ;
    }
    
    public Topic getChangeSelectionTopic() {
        return this.changeSelection.topic ;
    }
}
