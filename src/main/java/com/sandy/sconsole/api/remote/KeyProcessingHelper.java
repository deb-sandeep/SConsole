package com.sandy.sconsole.api.remote;

import java.util.HashMap ;
import java.util.Map ;
import java.util.Stack ;

import org.apache.log4j.Logger ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.remote.Handler ;
import com.sandy.sconsole.core.remote.RemoteKeyUtil ;
import com.sandy.sconsole.core.remote.KeyProcessor ;

public class KeyProcessingHelper {
    
    static final Logger log = Logger.getLogger( KeyProcessingHelper.class ) ;

    private Stack<KeyProcessor> processors = null ;
    
    public KeyProcessingHelper( Stack<KeyProcessor> processors ) {
        this.processors = processors ;
    }

    public ResponseEntity<String> processKeyEvent( KeyEvent event ) 
            throws Exception {
            
        log.debug( "\n\n--------------------------------------------------" );
        log.debug( "KeyEvent received " + event.getKeyId() ) ;
        
        if( event.getBtnType().equals( RemoteKeyUtil.KEY_TYPE_SCR_SEL ) ) {
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
                KeyProcessor processor = null ;
                processor = processors.peek() ;
                log.debug( "Routing to key processor - " + processor.getName() ) ;
                processor.processRemoteKeyEvent( event ) ;
            }
        }
        
        return ResponseEntity.status( HttpStatus.OK )
                             .body( getFnLabelsJSON() ) ;
    }
    
    public String getFnLabelsJSON() 
        throws Exception {
        
        log.debug( "Activated function keys for next interaction:" ) ;
        
        ObjectMapper mapper = new ObjectMapper() ;
        
        KeyProcessor processor = null ;
        Map<String, Handler> fnHandlers = null ;
        Map<String, String> fnLabels = new HashMap<String, String>() ;
        
        if( !processors.isEmpty() ) {
            processor = processors.peek() ;
            fnHandlers = processor.getFnHandlers() ;
        }
        
        String[] fnKeyIds = RemoteKeyUtil.getsKeysOfType( RemoteKeyUtil.KEY_TYPE_FN ) ;
        for( String keyId : fnKeyIds ) {
            String label = "" ;
            if( processor != null && processor.isKeyEnabled( keyId ) ) {
                Handler handler = fnHandlers.get( keyId ) ;
                if( handler != null ) {
                    label = handler.getBtnHint() ;
                }
            }
            
            if( !keyId.equals( RemoteKeyUtil.FN_CANCEL ) ) {
                fnLabels.put( keyId.substring( keyId.indexOf( '@' )+1 ), label ) ; 
                
                if( processor.isKeyEnabled( keyId ) ) {
                    log.debug( "\t" + keyId + " -> " + label ) ;
                }
            }
        }
        return mapper.writeValueAsString( fnLabels ) ;
    }
}
