package com.sandy.sconsole.api.remote;

import java.util.HashMap ;
import java.util.Map ;
import java.util.Stack ;

import org.apache.log4j.Logger ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.* ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.remote.Handler ;
import com.sandy.sconsole.core.remote.RemoteKeyCode ;
import com.sandy.sconsole.core.remote.RemoteKeyEventProcessor ;

@RestController
public class RemoteController {
    
    private static final Logger log = Logger.getLogger( RemoteController.class ) ;
    
    private Stack<RemoteKeyEventProcessor> processors = new Stack<>() ;
    
    @PostMapping( "/RemoteControl" )
    public ResponseEntity<String> buttonPressed( @RequestBody KeyEvent event ) {
        
        try {
            ResponseEntity<String> response = processKeyEvent( event ) ;
            return response ;
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
                                 .body( getFnLabelsJSON() ) ;
        }
        catch( Exception e ) {
            return ResponseEntity.status( 500 )
                                 .body( e.getMessage() ) ;
        }
    }

    public ResponseEntity<String> processKeyEvent( KeyEvent event ) 
        throws Exception {
        
        log.debug( "\n\n--------------------------------------------------" );
        log.debug( "KeyEvent received " + event.getKeyId() ) ;
        
        if( event.getBtnType().equals( RemoteKeyCode.KEY_TYPE_SCR_SEL ) ) {
            SConsole.getApp()
                    .getFrame()
                    .handleScreenletSelectionEvent( event.getBtnCode() ) ;
        }
        else {
            if( processors.isEmpty() ) {
                log.warn( "No key processors found. Ignoring the key." ) ;
                return ResponseEntity.status( HttpStatus.NOT_ACCEPTABLE )
                                     .body( "No key processors registered. " + 
                                            "Ignoring key stroke" ) ;
            }
            else {
                RemoteKeyEventProcessor processor = null ;
                processor = processors.peek() ;
                log.debug( "Routing to key processor - " + processor.getName() ) ;
                processor.processRemoteKeyEvent( event ) ;
            }
        }
        
        return ResponseEntity.status( HttpStatus.OK )
                             .body( getFnLabelsJSON() ) ;
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
    
    private String getFnLabelsJSON() 
        throws Exception {
        
        log.debug( "Activated function keys for next interaction:" ) ;
        
        ObjectMapper mapper = new ObjectMapper() ;
        
        RemoteKeyEventProcessor processor = null ;
        Map<String, Handler> fnHandlers = null ;
        Map<String, String> fnLabels = new HashMap<String, String>() ;
        
        if( !processors.isEmpty() ) {
            processor = processors.peek() ;
            fnHandlers = processor.getFnHandlers() ;
        }
        
        String[] fnKeyIds = RemoteKeyCode.getsKeysOfType( RemoteKeyCode.KEY_TYPE_FN ) ;
        for( String keyId : fnKeyIds ) {
            String label = "" ;
            if( processor != null && processor.isKeyEnabled( keyId ) ) {
                Handler handler = fnHandlers.get( keyId ) ;
                if( handler != null ) {
                    label = handler.getBtnHint() ;
                }
            }
            
            if( !keyId.equals( RemoteKeyCode.FN_CANCEL ) ) {
                fnLabels.put( keyId.substring( keyId.indexOf( '@' )+1 ), label ) ; 
                
                if( processor.isKeyEnabled( keyId ) ) {
                    log.debug( "\t" + keyId + " -> " + label ) ;
                }
            }
        }
        return mapper.writeValueAsString( fnLabels ) ;
    }
}
