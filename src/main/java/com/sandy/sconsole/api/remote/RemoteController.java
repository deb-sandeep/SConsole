package com.sandy.sconsole.api.remote;

import java.util.Stack ;

import org.apache.log4j.Logger ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.* ;

import com.sandy.sconsole.core.remote.RemoteKeyEventProcessor ;

@RestController
public class RemoteController {
    
    private static final Logger log = Logger.getLogger( RemoteController.class ) ;
    
    private Stack<RemoteKeyEventProcessor> processors = new Stack<>() ;
    private KeyProcessingHelper keyProcessingHelper = new KeyProcessingHelper( processors ) ;
    
    @PostMapping( "/RemoteControl" )
    public ResponseEntity<String> buttonPressed( @RequestBody KeyEvent event ) {
        
        try {
            return keyProcessingHelper.processKeyEvent( event ) ;
        }
        catch( Exception e ) {
            return ResponseEntity.status( 500 )
                                 .body( e.getMessage() ) ;
        }
    }
    
    @GetMapping( "/RemoteControl/FnKeyLabels" )
    public ResponseEntity<String> getFnKeyLabels() {
        try {
            return ResponseEntity.status( HttpStatus.OK )
                    .body( keyProcessingHelper.getFnLabelsJSON() ) ;
        }
        catch( Exception e ) {
            return ResponseEntity.status( 500 )
                    .body( e.getMessage() ) ;
        }
    }
    
    public void pushKeyProcessor( RemoteKeyEventProcessor processor ) {
        log.debug( "Pushing key processor - " ) ;
        log.debug( processor.getDebugState() ) ;
        
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
