package com.sandy.sconsole.core.remote;

import java.util.concurrent.LinkedBlockingQueue ;

import org.apache.log4j.Logger ;
import org.springframework.stereotype.Component ;

import com.sandy.sconsole.api.remote.KeyPressEvent ;

@Component
public class RemoteKeyEventRouter extends Thread {

    private static final Logger log = Logger.getLogger( RemoteKeyEventRouter.class ) ;
    
    private LinkedBlockingQueue<KeyPressEvent> eventQueue = new LinkedBlockingQueue<>() ;
    private RemoteKeyReceiver keyReceiver = null ;
    
    public RemoteKeyEventRouter() {
        super.setDaemon( true ) ;
        start() ;
    }
    
    public void registerRemoteKeyReceiver( RemoteKeyReceiver keyReceiver ) {
        this.keyReceiver = keyReceiver ;
    }
    
    public void routeKeyEvent( KeyPressEvent event ) {
        eventQueue.add( event ) ;
    }
    
    public void run() {
        
        while( true ) {
            try {
                KeyPressEvent event = eventQueue.take() ;
                keyReceiver.handleRemoteKeyEvent( event ) ;
            }
            catch( InterruptedException e ) {
                log.debug( "Event pump interrupted." ) ;
            }
        }
    }
}
