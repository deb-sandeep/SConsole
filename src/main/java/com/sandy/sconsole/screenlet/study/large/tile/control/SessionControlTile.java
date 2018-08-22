package com.sandy.sconsole.screenlet.study.large.tile.control;

import com.sandy.sconsole.core.remote.RemoteKeyCode ;
import com.sandy.sconsole.core.screenlet.AbstractScreenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.dao.entity.Session ;

@SuppressWarnings( "serial" )
public class SessionControlTile extends SessionControlTileUI {

    public SessionControlTile( ScreenletPanel parent ) {
        super( parent ) ;
    }

    public void populateLastSessionDetails( Session session ) {
        
        AbstractScreenlet screenlet = getScreenlet() ;
        screenlet.disableAllKeys() ;
        screenlet.enableKey( RemoteKeyCode.FN_A, true ) ;
        
        if( session == null ) {
            // There has been no last session. Keep everything blank and
            // enable only the change button.
            setProblemButtonsVisible( false ) ;
            setBtn1( Btn1Type.CLEAR ) ;
            setBtn2( Btn2Type.CHANGE ) ;
        }
        else {
            setIcon( session.getSessionType() ) ;
            setTopic( session.getTopic().getTopicName() ) ;
            setBook( session.getBook().getBookShortName() ) ;
            setProblem( session.getLastProblem() ) ;
            
            setProblemButtonsVisible( false ) ;
            setBtn1( Btn1Type.PLAY ) ;
            setBtn2( Btn2Type.CHANGE ) ;
            screenlet.enableKey( RemoteKeyCode.RUN_PLAYPAUSE, true ) ;
        }
    }
}
