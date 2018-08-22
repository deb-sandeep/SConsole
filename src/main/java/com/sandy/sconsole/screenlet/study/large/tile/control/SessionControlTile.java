package com.sandy.sconsole.screenlet.study.large.tile.control;

import static com.sandy.sconsole.core.CoreEventID.* ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;
import static com.sandy.sconsole.core.screenlet.Screenlet.RunState.* ;

import java.util.* ;

import com.sandy.common.bus.* ;
import com.sandy.sconsole.* ;
import com.sandy.sconsole.core.remote.* ;
import com.sandy.sconsole.core.screenlet.* ;
import com.sandy.sconsole.core.screenlet.Screenlet.* ;
import com.sandy.sconsole.core.util.* ;
import com.sandy.sconsole.dao.entity.* ;
import com.sandy.sconsole.dao.entity.master.* ;
import com.sandy.sconsole.dao.repository.master.* ;
import com.sandy.sconsole.screenlet.study.* ;

@SuppressWarnings( "serial" )
public class SessionControlTile extends SessionControlTileUI 
    implements SecondTickListener {
    
    private static final int FN_CHANGE = 1 ;
    
    private RunState runState = RunState.STOPPED ;
    
    private Date startTime = null ;
    private Date endTime = null ;
    
    private long runTime = 0 ;
    private long pauseTime = 0 ;
    private long totalPauseTime = 0 ;
    
    private StudyScreenlet screenlet = null ;
    private KeyActivationManager kaMgr = null ;
    
    private Session session = null ;
    
    private ProblemRepository problemRepo = null ;
    private Queue<Problem> unsolvedProblems = new LinkedList<>();

    public SessionControlTile( ScreenletPanel parent ) {
        super( parent ) ;
        screenlet = ( StudyScreenlet )parent.getScreenlet() ;
        kaMgr = screenlet.getKeyActivationManager() ;
        
        problemRepo = SConsole.getAppContext().getBean( ProblemRepository.class ) ;
        
        SConsole.addSecTimerTask( this ) ;
        getEventBus().addSubscriberForEventTypes( this, false, 
                                                    SCREENLET_PLAY, 
                                                    SCREENLET_PAUSE, 
                                                    SCREENLET_RESUME, 
                                                    SCREENLET_STOP ) ;
    }

    public void populateLastSessionDetails( Session lastSession ) {
        
        setBtn2( Btn2Type.CHANGE ) ;
        setProblemButtonsVisible( false ) ;
        
        kaMgr.disableAllKeys() ;
        kaMgr.setKeyEnable( FN_A, true ) ;
        kaMgr.setFnKeyFeature( FN_A, FN_CHANGE ) ;
        
        if( lastSession == null ) {
            // There has been no last session. Keep everything blank and
            // enable only the change button.
            session = new Session() ;
            setBtn1( Btn1Type.CLEAR ) ;
        }
        else {
            // Use the last session as a template for this session.
            session = lastSession ;
            session.setId( null ) ; 
            
            setSessionType( session.getSessionType() ) ;
            setTopic( session.getTopic() ) ;
            setBook( session.getBook() ) ;
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
        if( book == null ) {
            setBookLabel( null ) ;
        }
        else {
            session.setBook( book ) ;
            setBookLabel( session.getBook().getBookShortName() ) ;
            
            loadUnsolvedProblems() ;
            setProblem( unsolvedProblems.poll() ) ;
        }
    }
    
    public void setProblem( Problem problem ) {
        session.setLastProblem( problem ) ;
        setProblemLabel( problem ) ;
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
                if( p.getMarkedForRedo() ) {
                    found.add( p ) ;
                }
            }
            unsolvedProblems.addAll( found ) ;
            problems.removeAll( found ) ;
            found.clear() ;
            
            // Second pass - find all the questions whose id is greater than
            // or equal to the the sessions problem
            int refProblemId = -1 ;
            Problem currentProblem = session.getLastProblem() ;
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
            
            // Third pass - add all the remaining problems to the unsolved
            // problem list
            unsolvedProblems.addAll( problems ) ;
        }
    }

    @Override
    public void secondTicked( Calendar calendar ) {
        if( runState == RUNNING ) {
            runTime++ ;
            updateSessionTimeLabel( runTime ) ;
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
                // Need to show the paused dialog.
                break ;
                
            case SCREENLET_RESUME:
                pauseTime = 0 ;
                break ;
                
            case SCREENLET_STOP:
                pauseTime = 0 ;
                break ;
        }
    }

    private void play() {
        // If session type = Exercise, enable the lap buttons
        pauseTime = 0 ;
        startTime = new Date() ;
        setBtn1( Btn1Type.PAUSE ) ;
        setBtn2( Btn2Type.STOP ) ;
        
    }
}
