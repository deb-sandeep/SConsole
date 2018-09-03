package com.sandy.sconsole.api.remote;

import java.util.HashMap ;
import java.util.Map ;
import java.util.Stack ;

import org.apache.log4j.Logger ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.remote.KeyProcessor ;
import com.sandy.sconsole.core.remote.KeyType ;

import static com.sandy.sconsole.core.remote.Key.* ;

public class KeyProcessingHelper {
    
    static final Logger log = Logger.getLogger( KeyProcessingHelper.class ) ;

    private Stack<KeyProcessor> processors = null ;
    
    private static Key KEYS[] = {
            UP,
            LEFT,
            RIGHT,
            DOWN,
            SELECT,
            CANCEL,
            PLAYPAUSE,
            STOP,
            FN_A,
            FN_B,
            FN_C,
            FN_D,
            FN_E,
            FN_F,
            FN_G,
            FN_H
    } ;
    
    private boolean screenSwitchingEnabled = true ;
    
    public KeyProcessingHelper( Stack<KeyProcessor> processors ) {
        this.processors = processors ;
    }

    public ResponseEntity<String> processKey( Key key ) 
            throws Exception {
            
        log.debug( "\n\n--------------------------------------------------" );
        log.debug( "Key received " + key ) ;
        
        if( key.getKeyType() == KeyType.SCREEN_SEL ) {
            if( screenSwitchingEnabled ) {
                SConsole.getApp()
                .getFrame()
                .handleScreenletSelectionEvent( key.getKeyCode() ) ;
            }
            else {
                log.info( "Screen switching is disabled." ) ;
            }
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
                             .body( getKeyActivationJSON() ) ;
    }
    
    public String getKeyActivationJSON() 
        throws Exception {
        
        ObjectMapper mapper = new ObjectMapper() ;
        Map<String, String> keyActivationInfo = new HashMap<String, String>() ;
        
        KeyProcessor processor = null ;
        Map<Key, String> info = null ;
        if( !processors.isEmpty() ) {
            processor = processors.peek() ;
            info = processor.getActivatedKeyInfo() ;
        }
        else {
            info = new HashMap<>() ;
        }
        
        for( Key key : KEYS ) {
            String value = null ;
            if( info.containsKey( key ) ) {
                String label = info.get( key ) ;
                value = ( label == null )? "" : label ;
            }
            keyActivationInfo.put( key.name(), value ) ;
        }
        
        return mapper.writeValueAsString( keyActivationInfo ) ;
    }

    public void enableScreenSwitching( boolean enable ) {
        this.screenSwitchingEnabled = enable ;
    }
}
