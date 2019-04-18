package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import java.util.ArrayList ;
import java.util.List ;

import javax.swing.SwingUtilities ;

import org.apache.log4j.Logger ;
import org.springframework.context.ApplicationContext ;

import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.remote.RemoteController ;
import com.sandy.sconsole.core.frame.AbstractDialogPanel ;
import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.dao.entity.Session.SessionType ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.LastSessionRepository ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.dao.repository.SessionRepository ;
import com.sandy.sconsole.dao.repository.master.BookRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;
import com.sandy.sconsole.screenlet.study.TopicBurnInfo ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionInformation ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.Btn1Type ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.OutcomeButtonsState ;

public class BaseControlTileState extends State {
    
    private static final Logger log = Logger.getLogger( BaseControlTileState.class ) ;
    
    protected SessionControlTile tile = null ;

    protected ApplicationContext ctx = null ;
    
    protected BookRepository           bookRepo = null ;
    protected ProblemRepository        problemRepo = null ;
    protected SessionRepository        sessionRepo = null ;
    protected LastSessionRepository    lastSessionRepo = null ;
    protected ProblemAttemptRepository problemAttemptRepo = null ;
    
    private StudyScreenletLargePanel screenletPanel = null ;
    private RemoteController controller = null ;
    
    protected SessionInformation si = null ;
    
    protected BaseControlTileState( String stateName, 
                                    SessionControlTile tile,
                                    StudyScreenletLargePanel screenletPanel ) {
        super( stateName ) ;
        this.tile = tile ;
        this.screenletPanel = screenletPanel ;
        
        ctx = SConsole.getAppContext() ;
        controller = ctx.getBean( RemoteController.class ) ;

        bookRepo           = ctx.getBean( BookRepository.class ) ;
        problemRepo        = ctx.getBean( ProblemRepository.class ) ;
        sessionRepo        = ctx.getBean( SessionRepository.class ) ;
        lastSessionRepo    = ctx.getBean( LastSessionRepository.class ) ;
        problemAttemptRepo = ctx.getBean( ProblemAttemptRepository.class ) ;
    }
    
    public String getCenterPanelCardName() {
        return RemoteController.CENTER_NAV_PANEL ;
    }
    
    /**
     * This method is called upon by the SessionControlTile at the break 
     * of a day.
     */
    public void handleDayChange() {
        publishRefreshBurnInfo() ;
    }
    
    protected void showMessage( String msg ) {
        screenletPanel.showMessage( msg ) ;
    }
    
    protected void hideMessage() {
        screenletPanel.hideMessage() ;
    }
    
    public String getSubjectName() {
        return screenletPanel.getScreenlet().getDisplayName() ;
    }
    
    public SessionControlTile getControlTile() {
        return this.tile ;
    }

    protected void populateUIBasedOnSessionInfo( SessionInformation si ) {
        
        Session ls = si.session ;
        SessionType sessionType = ls.getSessionType() ;
        
        tile.setSessionTypeIcon( sessionType ) ;
        tile.setTopicLabel( ls.getTopic() ) ;
        tile.updateSessionTimeLabel( ls.getDuration() ) ;
        
        if( sessionType == SessionType.EXERCISE ) {
            
            log.debug( "Last sesion was an exercise. Populate exercise details." );
            tile.setBookLabel( ls.getBook() ) ;
            tile.updateOutcomeCounts( ls ) ;
            
            populateProblem( si ) ;
            
            tile.setOutcomeButtonsState( OutcomeButtonsState.INACTIVE ) ;
        }
        else {
            tile.setBookLabel( null ) ;
            tile.setProblemLabel( null ) ;
            tile.updateNumProblemsLeftInBookLabel( -1 ) ;
            tile.updateLapTimeLabel( -1 ) ;
            tile.updateOutcomeCounts( null ) ;
            
            tile.setOutcomeButtonsState( OutcomeButtonsState.HIDDEN ) ;
        }
    }
    
    protected void populateProblem( SessionInformation si ) {
        
        Topic topic = si.session.getTopic() ;
        Book book = si.session.getBook() ;
        Problem lastProblem = si.session.getLastProblem() ;
        
        si.unsolvedProblems = loadUnsolvedProblems( topic, book, lastProblem ) ;
        if( !si.unsolvedProblems.isEmpty() ) {
            si.session.setLastProblem( si.unsolvedProblems.get( 0 ) ) ;
        }
        else {
            si.session.setLastProblem( null ) ;
        }
        
        tile.updateNumProblemsLeftInBookLabel( si.unsolvedProblems.size() ) ;
        tile.setProblemLabel( si.session.getLastProblem() ) ;
    }
    
    protected List<Problem> loadUnsolvedProblems( Topic topic, Book book, 
                                                  Problem lastProblem ) {
        
        ArrayList<Problem> unsolvedProblems = new ArrayList<>() ;
        ArrayList<Problem> problems = new ArrayList<>() ;
        
        if( topic == null || book == null ) {
            return unsolvedProblems ;
        }
        
        problems.addAll( problemRepo.findUnsolvedProblems( topic.getId(), book.getId() ) ) ;
        
        if( !problems.isEmpty() ) {
            
            List<Problem> scoop = new ArrayList<>() ;

            // First pass - go through the problems and collect all redo problems
            // CR-8-Sep-18: If a redo problem is skipped, don't keep slapping 
            // it in the face in every session. Treat is like a skipped problem
            // which needs to be presented at the end
            for( Problem p : problems ) {
                if( p.getRedo() && !p.getSkipped() ) {
                    scoop.add( p ) ;
                }
            }
            unsolvedProblems.addAll( scoop ) ;
            problems.removeAll( scoop ) ;
            scoop.clear() ;
            
            // Second pass - find all the questions whose id is greater than
            // or equal to the the sessions problem and which are not skipped
            int refProblemId = (lastProblem != null)? lastProblem.getId() : -1 ;
            for( Problem p : problems ) {
                // CR-12-Sep-18 - If there are skipped problems beyond the 
                // last solved problem, don't include them ahead in the list.
                // All skipped problems should be presented at the end
                // CR-05-Jan-19 - If there are some redo problems which have been
                // skipped, ignore them in this pass too
                if( ( p.getId() >= refProblemId ) && !p.getSkipped() && !p.getRedo() ) {
                    scoop.add( p ) ;
                }
            }
            unsolvedProblems.addAll( scoop ) ;
            problems.removeAll( scoop ) ;
            scoop.clear() ;
            
            // Third pass - Find all the problems which are not skipped and add
            // CR-05-Jan-19 - If there are some redo problems which have been
            // skipped, ignore them in this pass too
            for( Problem p : problems ) {
                if( !p.getSkipped() && !p.getRedo() ) {
                    scoop.add( p ) ;
                }
            }
            unsolvedProblems.addAll( scoop ) ;
            problems.removeAll( scoop ) ;
            scoop.clear() ;
            
            
            // Fourth pass - add all the remaining problems to the unsolved
            // problem list. The remaining problems are now only the skipped
            // problems.
            unsolvedProblems.addAll( problems ) ;
        }
        
        log.debug( unsolvedProblems.size() + " unsolved problems found" ) ;
        return unsolvedProblems ;
    }

    protected void processPlayReadiness( SessionInformation si ) {
        
        boolean readyToPlay = true ;
        
        tile.clearInvalidationBorders() ;
        
        if( si.session.getSessionType() == null ) {
            readyToPlay = false ;
            tile.invalidateSessionTypePanel() ;
        }
        if( si.session.getTopic() == null ) {
            readyToPlay = false ;
            tile.invalidateTopicPanel() ;
        }
        
        if( si.session.getSessionType() == SessionType.EXERCISE ) {
            if( si.session.getBook() == null ) {
                readyToPlay = false ;
                tile.invalidateBookPanel() ; 
            }
            if( si.session.getLastProblem() == null ) {
                readyToPlay = false ;
                tile.invalidateProblemPanel() ; 
            }
        }
        
        if( readyToPlay ) {
            tile.setBtn1UI( Btn1Type.PLAY ) ;
            super.enableTransition( Key.PLAYPAUSE ) ;
            hideMessage() ;
        }
        else {
            showMessage( "Play will be enabled after the required attributes (" + 
                         "highlighted with red border) are changed." ) ;
            super.disableTransition( Key.PLAYPAUSE ) ;
        }
    }
    
    protected void showDialog( AbstractDialogPanel dialog ) {
        
        controller.pushKeyProcessor( dialog.getKeyProcessor() ) ;
        
        SwingUtilities.invokeLater( new Runnable() { public void run() {
            SConsole.getApp()
                    .getFrame()
                    .showDialog( dialog ) ;
        }});
        
    }
    
    protected void publishRefreshBurnInfo() {
        
        Topic topic = null ;
        TopicBurnInfo burnInfo = null ;
        
        if( this.si == null ) return ;
        
        try {
            topic = si.session.getTopic() ;
            burnInfo = new TopicBurnInfo( topic ) ;
            
            tile.getScreenlet()
                .getEventBus()
                .publishEvent( EventCatalog.BURN_INFO_REFRESHED, burnInfo ) ;
        }
        catch( Exception e ) {
            log.error( "BurnInfo could not be created. Topic = " + topic, e ) ;
        }
    }
}
