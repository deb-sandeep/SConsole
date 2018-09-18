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
    
    private Stack<KeyProcessor> processors = new Stack<>() ;
    private KeyProcessingHelper keyProcessingHelper = new KeyProcessingHelper( processors ) ;
    
    @PostMapping( "/RemoteControl" )
    public ResponseEntity<String> buttonPressed( @RequestBody KeyEvent event ) {
        
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
        log.debug( "Pushing key processor - " ) ;
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
