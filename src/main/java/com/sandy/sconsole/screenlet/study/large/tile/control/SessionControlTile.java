package com.sandy.sconsole.screenlet.study.large.tile.control;

import static com.sandy.sconsole.core.frame.UIConstant.* ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;
import static com.sandy.sconsole.core.screenlet.Screenlet.RunState.PAUSED ;
import static com.sandy.sconsole.core.screenlet.Screenlet.RunState.RUNNING ;
import static com.sandy.sconsole.core.screenlet.Screenlet.RunState.STOPPED ;

import java.awt.Color ;
import java.sql.Timestamp ;
import java.util.* ;

import javax.swing.JPanel ;
import javax.swing.SwingUtilities ;

import org.apache.log4j.Logger ;
import org.springframework.context.ApplicationContext ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.remote.RemoteController ;
import com.sandy.sconsole.core.frame.SConsoleFrame ;
import com.sandy.sconsole.core.remote.Handler ;
import com.sandy.sconsole.core.remote.RemoteKeyEventProcessor ;
import com.sandy.sconsole.core.remote.RemoteKeyListener ;
import com.sandy.sconsole.core.screenlet.AbstractScreenlet ;
import com.sandy.sconsole.core.screenlet.Screenlet ;
import com.sandy.sconsole.core.screenlet.Screenlet.RunState ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.core.util.SecondTickListener ;
import com.sandy.sconsole.dao.entity.ProblemAttempt ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.LastSessionRepository ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.dao.repository.SessionRepository ;
import com.sandy.sconsole.dao.repository.master.BookRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.* ;

@SuppressWarnings( "serial" )
public class SessionControlTile extends SessionControlTileUI 
    implements SecondTickListener, RemoteKeyListener {
    
    static final Logger log = Logger.getLogger( SessionControlTile.class ) ;
    
    private RunState runState = RunState.STOPPED ;
    
    private int runTime = 0 ;
    private int totalPauseTime = 0 ;
    
    private Timestamp lapStartTime = null ;
    private int lapTime = 0 ;
    private int numProblemsLeftInChapter = 0 ;
    
    private StudyScreenlet screenlet = null ;
    
    private ApplicationContext ctx = null ;
    private Session currentSession = null ;
    private Session lastSession = null ;
    
    private BookRepository           bookRepo = null ;
    private ProblemRepository        problemRepo = null ;
    private SessionRepository        sessionRepo = null ;
    private LastSessionRepository    lastSessionRepo = null ;
    private ProblemAttemptRepository problemAttemptRepo = null ;
    
    private RemoteController remoteController = null ;
    
    private Queue<Problem> unsolvedProblems = new LinkedList<>() ;
    
    private ChangeSelection changeSelection = null ;
    
    private PauseDialog             pauseDialog         = null ;
    private BookChangeDialog        bookChangeDialog    = null ;
    private TopicChangeDialog       topicChangeDialog   = null ;
    private ProblemChangeDialog     problemChangeDialog = null ;
    private SessionTypeChangeDialog typeChangeDialog    = null ;

    class ChangeSelection {
        
        private String sessionType = null ;
        private Topic topic = null ;
        private Book book = null ;
        private Problem problem = null ;
        
        public ChangeSelection( Session session ) {
            if( session != null ) {
                setSessionType( session.getSessionType() ) ;
                setTopic( session.getTopic() ) ;
                setBook( session.getBook() ) ;
                setProblem( session.getLastProblem() ) ;
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
        
        public String getSessionType() { return this.sessionType ; }
        public void setSessionType( String type ) {
            this.sessionType = type ;
            setSessionTypeIcon( this.sessionType ) ;
        }
        
        public Topic getTopic() { return this.topic ; }
        public void setTopic( Topic topic ) {
            this.topic = topic ;
            setTopicLabel( this.topic ) ;
        }
        
        public Book getBook() { return this.book ; }
        public void setBook( Book book ) {
            this.book = book ;
            setBookLabel( this.book ) ;
        }
        
        public Problem getProblem() { return this.problem ; }
        public void setProblem( Problem problem ) {
            this.problem = problem ;
            setProblemLabel( this.problem ) ;
        }
    }
    
    public SessionControlTile( ScreenletPanel parent ) {
        super( parent ) ;
        screenlet = ( StudyScreenlet )parent.getScreenlet() ;
        keyProcessor = new RemoteKeyEventProcessor( "Study Control", this ) ;
        
        ctx = SConsole.getAppContext() ;
        
        bookRepo           = ctx.getBean( BookRepository.class ) ;
        problemRepo        = ctx.getBean( ProblemRepository.class ) ;
        sessionRepo        = ctx.getBean( SessionRepository.class ) ;
        lastSessionRepo    = ctx.getBean( LastSessionRepository.class ) ;
        problemAttemptRepo = ctx.getBean( ProblemAttemptRepository.class ) ;
        
        remoteController = ctx.getBean( RemoteController.class ) ;

        pauseDialog         = new PauseDialog() ;
        typeChangeDialog    = new SessionTypeChangeDialog() ;
        bookChangeDialog    = new BookChangeDialog( this ) ;
        topicChangeDialog   = new TopicChangeDialog( this ) ;
        problemChangeDialog = new ProblemChangeDialog( this ) ;

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
    
    public void populateLastSessionDetails( Session ls ) {
        
        keyProcessor.disableAllKeys() ;
        
        activateProblemOutcomeButtons( false ) ;
        
        setBtn2( Btn2Type.CHANGE ) ;
        
        this.runTime = 0 ;
        this.lapTime = 0 ;
        this.totalPauseTime = 0 ;
        this.lastSession = ls ;
        
        // Note that current session does not have an identifier till it
        // is first played.
        currentSession = new Session() ;
        
        if( ls == null ) {
            // There has been no last session. Keep everything blank and
            // enable only the change button.
            setBtn1( Btn1Type.CLEAR ) ;
            
            setSessionType( null ) ;
            setTopic( null ) ;
            setBook( null ) ;
            setProblem( null ) ;
            
            updateNumProblemsLeftInChapterLabel( -1 ) ;
        }
        else {
            // Use the last session as a template for this session.
            String sessionType = ls.getSessionType() ;
            setSessionType( ls.getSessionType() ) ;
            setTopic( ls.getTopic() ) ;
            updateSessionTimeLabel( ls.getDuration() ) ;
            
            if( sessionType.equals( Session.TYPE_EXERCISE ) ) {
                setBook( ls.getBook() ) ;
                setProblem( ls.getLastProblem() ) ;
                
                updateNumSkippedLabel( ls.getNumSkipped() ) ;
                updateNumSolvedLabel( ls.getNumSolved() ) ;
                updateNumRedoLabel( ls.getNumRedo() ) ;
                updateNumPigeonLabel( ls.getNumPigeon() ) ;
                
                loadUnsolvedProblems() ;
                setNextProblem() ;
            }
            else {
                setBook( ls.getBook() ) ;
                currentSession.setLastProblem( ls.getLastProblem() ) ;
            }
        }
        
        setCurrentUseCase( UseCase.STOP_SESSION ) ;
        validateSessionDetailsAndActivatePlay() ;
    }
    
    private void setSessionType( String sessionType ) {
        currentSession.setSessionType( sessionType ) ;
        setSessionTypeIcon( sessionType ) ;
    }
    
    private void setTopic( Topic topic ) {
        currentSession.setTopic( topic ) ;
        setTopicLabel( currentSession.getTopic() ) ;
    }
    
    private void setBook( Book book ) {
        currentSession.setBook( book ) ;
        setBookLabel( book ) ;
    }
    
    private void setProblem( Problem problem ) {
        currentSession.setLastProblem( problem ) ;
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
        
        if( currentSession == null ) return ;
        
        Topic topic = currentSession.getTopic() ;
        Book book = currentSession.getBook() ;
        
        if( topic == null || book == null ) return ;
        
        ArrayList<Problem> problems = new ArrayList<>() ;
        problems.addAll( problemRepo.findUnsolvedProblems( topic.getId(), book.getId() ) ) ;
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
            Problem currentProblem = currentSession.getLastProblem() ;
            if( currentProblem != null ) {
                refProblemId = currentProblem.getId() ;
            }
            for( Problem p : problems ) {
                if( p.getId() >= refProblemId ) {
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
            if( currentSession.getSessionType().equals( Session.TYPE_EXERCISE ) ) {
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
        populateLastSessionDetails( currentSession.clone() ) ;
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
            currentSession = changeSelection.createSession() ;
            changeSelection = null ;
            loadUnsolvedProblems() ;
            setNextProblem() ;
            deactivateControlPanelForChange() ;
        }
        
        resume() ;
        
        Date now = new Date() ;
        currentSession.setStartTime( new Timestamp( now.getTime() ) ) ;
        saveSession() ;
        this.lastSession = currentSession ;

        if( currentSession.getSessionType().equals( Session.TYPE_EXERCISE ) ) {
            updateNumSkippedLabel( 0 ) ;
            updateNumSolvedLabel( 0 ) ;
            updateNumRedoLabel( 0 ) ;
            updateNumPigeonLabel( 0 ) ;
            updateSessionTimeLabel( 0 ) ;
        }
        
        lapStartTime = new Timestamp( now.getTime() ) ;
        lastSessionRepo.update( getScreenlet().getDisplayName(), currentSession.getId() ) ;
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
        if( currentSession.getSessionType().equals( Session.TYPE_EXERCISE ) ) {
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
        
        Problem p = currentSession.getLastProblem() ;
        p.setSolved( solved ) ;
        p.setRedo( redo ) ;
        p.setSkipped( skipped ) ;
        p.setPigeoned( pigeoned ) ;
        
        problemRepo.save( p ) ;
        
        ProblemAttempt attempt = new ProblemAttempt() ;
        attempt.setSession( currentSession ) ;
        attempt.setProblem( currentSession.getLastProblem() ) ;
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
        currentSession.setEndTime( new Timestamp( now.getTime() ) ) ;
        currentSession.setDuration( runTime ) ;
        currentSession.setAbsoluteDuration( runTime + totalPauseTime ) ;
        currentSession = sessionRepo.save( currentSession ) ;
    }
    
    private void skipProblem() {
        log.debug( "Skipping the problem" ) ;
        saveProblem( false, false, true, false ) ;
        lapTime = 0 ;
        updateNumSkippedLabel( currentSession.incrementNumSkipped() ) ;
        setNextProblem() ;
    }
    
    private void problemSolved() {
        log.debug( "Solved the problem" ) ;
        saveProblem( true, false, false, false ) ;
        lapTime = 0 ;
        updateNumSolvedLabel( currentSession.incrementNumSolved() ) ;
        setNextProblem() ;
        
        numProblemsLeftInChapter-- ;
        updateNumProblemsLeftInChapterLabel( numProblemsLeftInChapter ) ;
    }
    
    private void redoProblem() {
        log.debug( "Redo the problem" ) ;
        saveProblem( false, true, false, false ) ;
        lapTime = 0 ;
        updateNumRedoLabel( currentSession.incrementNumRedo() ) ;
        setNextProblem() ;
    }
    
    private void setPigeon() {
        log.debug( "Set a pigeon" ) ;
        saveProblem( false, false, false, true ) ;
        lapTime = 0 ;
        updateNumPigeonLabel( currentSession.incrementNumPigeon() ) ;
        setNextProblem() ;
    }
    
    private void setStarred() {
        log.debug( "Un/starring the problem" ) ;
        Problem p = currentSession.getLastProblem() ;
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
        
        Integer lsId = null ;
        Session sessionBefore = lastSession ;
        
        if( lastSession != null ) {
            lsId = lastSessionRepo.findSessionBefore( lastSession.getId(), 
                                                      screenlet.getDisplayName() ) ;
            if( lsId != null ) {
                sessionBefore = sessionRepo.findById( lsId ).get() ;
            }
        }
        
        changeSelection = new ChangeSelection( sessionBefore ) ;
        activateControlPanelForChange( changeSelection.getSessionType() ) ;
        
        validateSessionDetailsAndActivatePlay() ;
        
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
            changeSelection.setSessionType( type ) ;
            
            // If the new session is of type Theory or Lecture,
            // we need to blank out the following :
            //  - problem label
            //  - num labels
            //  - lap time
            
            activateControlPanelForChange( type ) ;
            
            if( !type.equals( Session.TYPE_EXERCISE ) ) {
                changeSelection.setProblem( null ) ;
                changeSelection.setBook( null ) ;
            }
        }
        
        validateSessionDetailsAndActivatePlay() ;
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
            changeSelection.setTopic( selectedTopic ) ;
            if( changeSelection.getSessionType().equals( Session.TYPE_EXERCISE ) ) {
                populatePredictionsForBookAndProblem( selectedTopic.getId() ) ;
            }
        }
        
        validateSessionDetailsAndActivatePlay() ;
    }
    
    private void populatePredictionsForBookAndProblem( Integer topicId ) {
        
        Integer lastSessionId = lastSessionRepo.findLastSessionForTopic( topicId ) ;
        
        if( lastSessionId != null ) {
            Session session = sessionRepo.findById( lastSessionId ).get() ;
            
            changeSelection.setBook( session.getBook() ) ;
            changeSelection.setProblem( session.getLastProblem() ) ;
        }
        else {
            List<Integer> bookIds = bookRepo.findProblemBooksForTopic( topicId ) ;
            
            if( bookIds.isEmpty() ) {
                changeSelection.setBook( null ) ;
                changeSelection.setProblem( null ) ;
                updateNumPigeonLabel( -1 ) ;
            }
            else {
                Book book = bookRepo.findById( bookIds.get( 0 ) ).get() ;
                changeSelection.setBook( book ) ;
                
                Integer problemId = problemRepo.findNextUnsolvedProblem( 
                                                       topicId, book.getId() ) ;
                if( problemId != null ) {
                    changeSelection.setProblem( problemRepo.findById( problemId )
                                                           .get() ) ;
                }
                else {
                    changeSelection.setProblem( null ) ;
                }
            }
        }
        refreshUnsolvedProblemCount() ;
    }
    
    private void refreshUnsolvedProblemCount() {
        
        int numProblemsLeft = -1 ;
        if( changeSelection.getTopic() != null &&
                changeSelection.getBook() != null ) {
            log.debug( "Finding num unsolved problems for " ) ;
            log.debug( "\tTopic = " + changeSelection.getTopic().getId() ) ;
            log.debug( "\tBook = " + changeSelection.getBook().getId() ) ;
            
            numProblemsLeft = problemRepo.findUnsolvedProblemCount( 
                    changeSelection.getTopic().getId(), 
                    changeSelection.getBook().getId() ) ;
            
            log.debug( "Num unsolved problems found = " + numProblemsLeft );
        }
        updateNumProblemsLeftInChapterLabel( numProblemsLeft ) ;
    }
    
    private void changeBook() {
        log.debug( "Executing changeBook" ) ;
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                invokeChangeBookAsync() ;
            }
        });
    }
    
    private void invokeChangeBookAsync() {
        
        SConsoleFrame frame = SConsole.getApp().getFrame() ;
        Book selectedBook = ( Book )frame.showDialog( bookChangeDialog ) ;
        
        log.debug( "New Book chosen = " + selectedBook ) ;
        if( selectedBook != null ) {
            changeSelection.setBook( selectedBook ) ;
            
            Topic topic = changeSelection.getTopic() ;
            if( topic != null ) {
                Integer problemId = problemRepo.findNextUnsolvedProblem( 
                                        topic.getId(), selectedBook.getId() ) ;
                
                Problem problem = null ;
                if( problemId != null ) {
                    problem = problemRepo.findById( problemId ).get() ;
                }
                changeSelection.setProblem( problem ) ;
            }
        }
        
        refreshUnsolvedProblemCount() ;
        validateSessionDetailsAndActivatePlay() ;
    }
    
    private void changeProblem() {
        log.debug( "Executing changeProblem" ) ;
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                invokeChangeProblemAsync() ;
            }
        });
    }
    
    private void invokeChangeProblemAsync() {
        
        SConsoleFrame frame = SConsole.getApp().getFrame() ;
        Problem selectedProblem = ( Problem )frame.showDialog( problemChangeDialog ) ;
        
        log.debug( "New Problem chosen = " + selectedProblem ) ;
        if( selectedProblem != null ) {
            changeSelection.setProblem( selectedProblem ) ;
        }
        validateSessionDetailsAndActivatePlay() ;
    }
    
    private void cancelChange() {
        log.debug( "Executing cancelChange" ) ;
        
        changeSelection = null ;
        setBtn2( Btn2Type.CHANGE ) ;
        setCurrentUseCase( UseCase.STOP_SESSION ) ;
        deactivateControlPanelForChange() ;
        
        if( lastSession != null ) {
            populateLastSessionDetails( lastSession.clone() ) ;
        }
        else {
            populateLastSessionDetails( null ) ;
        }
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
        
        hideAndShowRelevantControlElements() ;
    }
    
    private List<JPanel> validateSessionData() {
        
        List<JPanel> invalidAttributePanels = new ArrayList<JPanel>() ;
        
        String sessionType = null ;
        Topic topic = null ;
        Book book = null ;
        Problem problem = null ;
        
        if( changeSelection != null ) {
            sessionType = changeSelection.getSessionType() ;
            topic = changeSelection.getTopic() ;
            book = changeSelection.getBook() ;
            problem = changeSelection.getProblem() ;
        }
        else if( currentSession != null ){
            sessionType = currentSession.getSessionType() ;
            topic = currentSession.getTopic() ;
            book = currentSession.getBook() ;
            problem = currentSession.getLastProblem() ;
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
    
    private void hideAndShowRelevantControlElements() {
        
        String sessionType = null ;
        
        if( changeSelection != null ) {
            sessionType = changeSelection.getSessionType() ;
        }
        else if( currentSession != null ){
            sessionType = currentSession.getSessionType() ;
        }

        if( sessionType == null ) {
            sTimeLbl.setText( "00:00:00" ) ;
            
            btnSkipLbl.setText( "" ) ;
            btnSolvedLbl.setText( "" ) ;
            btnRedoLbl.setText( "" ) ;
            btnPigeonLbl.setText( "" ) ;
            
            numSkipLbl.setText( "" ) ;
            numSolvedLbl.setText( "" ) ;
            numRedoLbl.setText( "" ) ;
            numPigeonLbl.setText( "" ) ;
            
            lTimeLbl.setText( "" ) ;
        }
        else {
            if( sessionType.equals( Session.TYPE_EXERCISE ) ) {
                btnSkipLbl.setText( "Skip" ) ;
                btnSolvedLbl.setText( "Solved" ) ;
                btnRedoLbl.setText( "Redo" ) ;
                btnPigeonLbl.setText( "Pigeon" ) ;
            }
            else if( sessionType.equals( Session.TYPE_LECTURE ) || 
                     sessionType.equals( Session.TYPE_THEORY ) )  {
                
                btnSkipLbl.setText( "" ) ;
                btnSolvedLbl.setText( "" ) ;
                btnRedoLbl.setText( "" ) ;
                btnPigeonLbl.setText( "" ) ;
                
                numSkipLbl.setText( "" ) ;
                numSolvedLbl.setText( "" ) ;
                numRedoLbl.setText( "" ) ;
                numPigeonLbl.setText( "" ) ;
                
                lTimeLbl.setText( "" ) ;
            }
        }
    }
    
    public Topic getChangeSelectionTopic() {
        if( this.changeSelection != null ) {
            return this.changeSelection.getTopic() ;
        }
        return null ;
    }

    public Book getChangeSelectionBook() {
        if( this.changeSelection != null ) {
            return this.changeSelection.getBook() ;
        }
        return null ;
    }

    public Problem getChangeSelectionProblem() {
        if( this.changeSelection != null ) {
            return this.changeSelection.getProblem() ;
        }
        return null ;
    }
}
