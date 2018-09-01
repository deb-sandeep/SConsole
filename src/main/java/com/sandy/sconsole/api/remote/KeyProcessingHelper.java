package com.sandy.sconsole.api.remote;

import java.util.Stack ;

import org.apache.log4j.Logger ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.remote.* ;

public class KeyProcessingHelper {
    
    static final Logger log = Logger.getLogger( KeyProcessingHelper.class ) ;

    private Stack<KeyProcessor> processors = null ;
    
    public KeyProcessingHelper( Stack<KeyProcessor> processors ) {
        this.processors = processors ;
    }

    public ResponseEntity<String> processKey( Key key ) 
            throws Exception {
            
        log.debug( "\n\n--------------------------------------------------" );
        log.debug( "Key received " + key ) ;
        
        if( key.getKeyType() == KeyType.SCREEN_SEL ) {
            SConsole.getApp()
                    .getFrame()
                    .handleScreenletSelectionEvent( key.getKeyCode() ) ;
        }
        else {
            if( processors.isEmpty() ) {
                log.warn( "No key processors found. Ignoring the key." ) ;
                return ResponseEntity.status( HttpStatus.NOT_ACCEPTABLE )
                                     .body( "No key processors registered. " + 
                                            "Ignoring key stroke" ) ;
            }
            else {
                KeyProcessor processor = null ;
                processor = processors.peek() ;
                log.debug( "Routing to key processor - " + processor.getName() ) ;
                processor.processKey( key ) ;
            }
        }
        
        return ResponseEntity.status( HttpStatus.OK )
                             .body( getFnLabelsJSON() ) ;
    }
    
    public String getFnLabelsJSON() 
        throws Exception {
        
        log.debug( "Activated function keys for next interaction:" ) ;
        log.error( "TODO: Have to implement this method." ) ;
        return "" ;
    }
}
