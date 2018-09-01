package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import org.springframework.context.ApplicationContext ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.dao.repository.LastSessionRepository ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.dao.repository.SessionRepository ;
import com.sandy.sconsole.dao.repository.master.BookRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;

public class BaseControlTileState extends State {
    
    protected SessionControlTile tile = null ;

    protected ApplicationContext ctx = null ;
    
    protected BookRepository           bookRepo = null ;
    protected ProblemRepository        problemRepo = null ;
    protected SessionRepository        sessionRepo = null ;
    protected LastSessionRepository    lastSessionRepo = null ;
    protected ProblemAttemptRepository problemAttemptRepo = null ;

    protected BaseControlTileState( String stateName, SessionControlTile tile ) {
        super( stateName ) ;
        this.tile = tile ;
        
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
}
