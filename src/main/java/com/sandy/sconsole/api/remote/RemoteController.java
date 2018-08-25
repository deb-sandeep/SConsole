package com.sandy.sconsole.api.remote;

import java.util.* ;
import java.util.concurrent.* ;

import org.apache.log4j.* ;
import org.springframework.http.* ;
import org.springframework.web.bind.annotation.* ;

import com.sandy.sconsole.* ;
import com.sandy.sconsole.core.api.* ;
import com.sandy.sconsole.core.remote.* ;

@RestController
public class RemoteController implements Runnable {
    
    private static final Logger log = Logger.getLogger( RemoteController.class ) ;
    
    private LinkedBlockingDeque<KeyEvent> eventQueue = new LinkedBlockingDeque<>() ;
    private Stack<RemoteKeyEventProcessor> processors = new Stack<>() ;
    
    public RemoteController() {
        Thread t = new Thread( this ) ;
        t.setDaemon( true ) ;
        t.start() ;
    }
    
    @PostMapping( "/RemoteControl" )
    public ResponseEntity<APIResponse> buttonPressed( @RequestBody KeyEvent event ) {
        
        try {
            eventQueue.putLast( event ) ;
            return ResponseEntity.ok().body( new APIResponse( "Success" ) ) ;
        }
        catch( InterruptedException e ) {
            return ResponseEntity.status( 500 )
                                 .body( new APIResponse( e.getMessage() ) ) ;
        }
    }

    public void run() {
        
        while( true ) {
            try {
                KeyEvent event = eventQueue.takeFirst() ;
                
                if( event.getBtnType() == RemoteKeyCode.KEY_TYPE_SCR_SEL ) {
                    SConsole.getApp()
                            .getFrame()
                            .handleScreenletSelectionEvent( event.getBtnCode() ) ;
                }
                else {
                    RemoteKeyEventProcessor listener = processors.peek() ;
                    if( listener == null ) {
                        log.warn( "No key listeners found. Will try processing " +  
                                "the event again in some time." ) ;
                        eventQueue.putFirst( event ) ;
                        Thread.sleep( 1000 ) ;
                    }
                    else {
                        listener.processRemoteKeyEvent( event ) ;
                    }
                }
            }
            catch( InterruptedException e ) {
                log.debug( "Event pump interrupted." ) ;
            }
        }
    }
    
    public void pushKeyProcessor( RemoteKeyEventProcessor processor ) {
        processors.push( processor ) ;
    }
    
    public RemoteKeyEventProcessor popKeyProcessor() {
        RemoteKeyEventProcessor processor = null ;
        try {
            processor = processors.pop() ;
        }
        catch( Exception e ) {
            log.error( "No more processors left." ) ;
        }
        return processor ;
    }
}
