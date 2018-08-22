package com.sandy.sconsole.screenlet.study.large.tile.control;

import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.dao.entity.Session ;

@SuppressWarnings( "serial" )
public class SessionControlTile extends SessionControlTileUI {

    public SessionControlTile( ScreenletPanel parent ) {
        super( parent ) ;
    }

    public void populateLastSessionDetails( Session session ) {
        if( session == null ) return ;
        
        setIcon( session.getSessionType() ) ;
        setTopic( session.getTopic().getTopicName() ) ;
        setBook( session.getBook().getBookShortName() ) ;
        setProblem( session.getLastProblem() ) ;
        
        setProblemButtonsVisible( false ) ;
    }
}
