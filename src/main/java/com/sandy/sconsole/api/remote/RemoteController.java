package com.sandy.sconsole.api.remote;

import java.util.Stack ;

import org.apache.log4j.Logger ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.* ;

import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.remote.KeyProcessor ;

@RestController
public class RemoteController {
    
    private static final Logger log = Logger.getLogger( RemoteController.class ) ;
    
    // The name of the card of the center panel representing the navigation panel
    // This is activated in all cases except for the question attempt - Play State
    public static final String CENTER_NAV_PANEL  = "NAV_PANEL" ;
    
    // The name of the card of the center panel representing the time projection
    // panel. This is activated only during the Play State.
    public static final String CENTER_PROJ_PANEL = "TIME_PROJECTION_PANEL" ;
    
    private Stack<KeyProcessor> processors = new Stack<>() ;
    private KeyProcessingHelper keyProcessingHelper = new KeyProcessingHelper( processors ) ;
    
    @PostMapping( "/RemoteControl" )
    public synchronized ResponseEntity<String> buttonPressed( @RequestBody KeyEvent event ) {
        
        log.debug( "\n\n--------------------------------------------------" );
        log.debug( "Key received " + event ) ;
        
        try {
            Key key = Key.decode( event.getKeyId() ) ;
            return keyProcessingHelper.processKey( key ) ;
        }
        catch( Exception e ) {
            log.error( "Exception while processing key. " + event.getKeyId(), e );
            return ResponseEntity.status( 500 )
                                 .body( e.getMessage() ) ;
        }
    }
    
    @GetMapping( "/RemoteControl/FnKeyLabels" )
    public ResponseEntity<String> getFnKeyLabels() {
        try {
            return ResponseEntity.status( HttpStatus.OK )
                    .body( keyProcessingHelper.getKeyActivationJSON() ) ;
        }
        catch( Exception e ) {
            log.error( "Exception while getting fn labels.", e );
            return ResponseEntity.status( 500 )
                    .body( e.getMessage() ) ;
        }
    }
    
    public void pushKeyProcessor( KeyProcessor processor ) {
        processors.push( processor ) ;
    }
    
    public KeyProcessor popKeyProcessor() {
        KeyProcessor processor = null ;
        try {
            processor = processors.pop() ;
        }
        catch( Exception e ) {
            log.error( "No more processors left." ) ;
        }
        return processor ;
    }
    
    public void enableScreenSwitching( boolean enable) {
        keyProcessingHelper.enableScreenSwitching( enable ) ;
    }
}
