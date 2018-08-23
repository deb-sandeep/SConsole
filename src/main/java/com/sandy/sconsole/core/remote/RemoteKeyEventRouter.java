package com.sandy.sconsole.core.remote;

import java.util.concurrent.* ;

import org.apache.log4j.* ;
import org.springframework.stereotype.* ;

import com.sandy.sconsole.api.remote.* ;

@Component
public class RemoteKeyEventRouter extends Thread {

    private static final Logger log = Logger.getLogger( RemoteKeyEventRouter.class ) ;
    
    private LinkedBlockingQueue<KeyEvent> eventQueue = new LinkedBlockingQueue<>() ;
    private RemoteKeyReceiver keyReceiver = null ;
    
    public RemoteKeyEventRouter() {
        super.setDaemon( true ) ;
        start() ;
    }
    
    public void registerRemoteKeyReceiver( RemoteKeyReceiver keyReceiver ) {
        this.keyReceiver = keyReceiver ;
    }
    
    public void routeKeyEvent( KeyEvent event ) {
        eventQueue.add( event ) ;
    }
    
    public void run() {
        
        while( true ) {
            try {
                KeyEvent event = eventQueue.take() ;
                if( keyReceiver != null ) {
                    keyReceiver.handleRemoteKeyEvent( event ) ;
                }
                else {
                    log.warn( "No key receiver registered with key router" ) ;
                }
            }
            catch( InterruptedException e ) {
                log.debug( "Event pump interrupted." ) ;
            }
        }
    }
}
