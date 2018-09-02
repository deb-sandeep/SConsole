package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.springframework.context.ApplicationContext ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.statemc.State ;
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
}
