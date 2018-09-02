package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.springframework.context.ApplicationContext ;

import com.sandy.sconsole.SConsole ;
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

    protected BaseControlTileState( String stateName, 
                                    SessionControlTile tile,
                                    StudyScreenletLargePanel screenletPanel ) {
        super( stateName ) ;
        this.tile = tile ;
        this.screenletPanel = screenletPanel ;
        
        ctx = SConsole.getAppContext() ;
        
        bookRepo           = ctx.getBean( BookRepository.class ) ;
        problemRepo        = ctx.getBean( ProblemRepository.class ) ;
        sessionRepo        = ctx.getBean( SessionRepository.class ) ;
        lastSessionRepo    = ctx.getBean( LastSessionRepository.class ) ;
        problemAttemptRepo = ctx.getBean( ProblemAttemptRepository.class ) ;
    }
    
    protected String getSubject() {
        return tile.getScreenlet().getDisplayName() ;
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

    protected void populateUIBasedOnSessionInfo( SessionInformation si ) {
        
        Session ls = si.sessionBlank ;
        
        log.debug( "Populating last session details" ) ;
        log.debug( ls ) ;
        
        SessionType sessionType = ls.getSessionType() ;
        
        log.debug( "Copying values of session type, topic and session time" ) ;
        tile.setSessionTypeIcon( sessionType ) ;
        tile.setTopicLabel( ls.getTopic() ) ;
        tile.updateSessionTimeLabel( ls.getDuration() ) ;
        
        if( sessionType == SessionType.EXERCISE ) {
            
            log.debug( "Last sesion was an exercise. Populate exercise details." );
            tile.setBookLabel( ls.getBook() ) ;
            tile.updateNumSkippedLabel( ls.getNumSkipped() ) ;
            tile.updateNumSolvedLabel( ls.getNumSolved() ) ;
            tile.updateNumRedoLabel( ls.getNumRedo() ) ;
            tile.updateNumPigeonLabel( ls.getNumPigeon() ) ;
            
            populateProblem( si ) ;
            
            tile.setOutcomeButtonsState( OutcomeButtonsState.INACTIVE ) ;
        }
        else {
            tile.setBookLabel( null ) ;
            tile.setProblemLabel( null ) ;
            tile.updateNumProblemsLeftInBookLabel( -1 ) ;
            tile.updateSessionTimeLabel( -1 ) ;
            tile.updateLapTimeLabel( -1 ) ;
            tile.updateNumSkippedLabel( -1 ) ;
            tile.updateNumSolvedLabel( -1 ) ;
            tile.updateNumRedoLabel( -1 ) ;
            tile.updateNumPigeonLabel( -1 ) ;
            
            tile.setOutcomeButtonsState( OutcomeButtonsState.HIDDEN ) ;
        }
    }
    
    protected void populateProblem( SessionInformation si ) {
        
        Topic topic = si.sessionBlank.getTopic() ;
        Book book = si.sessionBlank.getBook() ;
        Problem lastProblem = si.sessionBlank.getLastProblem() ;
        
        si.unsolvedProblems = loadUnsolvedProblems( topic, book, lastProblem ) ;
        if( !si.unsolvedProblems.isEmpty() ) {
            si.sessionBlank.setLastProblem( si.unsolvedProblems.get( 0 ) ) ;
        }
        else {
            si.sessionBlank.setLastProblem( null ) ;
        }
        
        tile.updateNumProblemsLeftInBookLabel( si.unsolvedProblems.size() ) ;
        tile.setProblemLabel( si.sessionBlank.getLastProblem() ) ;
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
            int refProblemId = (lastProblem != null)? lastProblem.getId() : -1 ;
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
        
        log.debug( unsolvedProblems.size() + " unsolved problems found" ) ;
        return unsolvedProblems ;
    }

    protected void processPlayReadiness( SessionInformation si ) {
        
        boolean readyToPlay = true ;
        
        if( si.sessionBlank.getSessionType() == null ) {
            readyToPlay = false ;
            tile.invalidateSessionTypePanel() ;
        }
        if( si.sessionBlank.getTopic() == null ) {
            readyToPlay = false ;
            tile.invalidateTopicPanel() ;
        }
        
        if( si.sessionBlank.getSessionType() == SessionType.EXERCISE ) {
            if( si.sessionBlank.getBook() == null ) {
                readyToPlay = false ;
                tile.invalidateBookPanel() ; 
            }
            if( si.sessionBlank.getLastProblem() == null ) {
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
}
