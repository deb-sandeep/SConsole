package com.sandy.sconsole.core.remote;

import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;

import java.util.Date ;
import java.util.HashMap ;
import java.util.Map ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.api.remote.KeyEvent ;

public class RemoteKeyEventProcessor {

    private static final Logger log = Logger.getLogger( RemoteKeyEventProcessor.class ) ;
    
    private String name = "<Unnamed Key Processor>" ;
    private Map<String, Boolean> activationMap = null ;
    private Map<String, Handler> fnHandlers = new HashMap<>() ;
    private RemoteKeyListener listener = null ;
    private Date lastKeyReceivedTime = null ;
    
    public RemoteKeyEventProcessor( String name, RemoteKeyListener listener ) {
        if( name != null ) {
            this.name = name ;
        }
        if( listener == null ) {
            throw new IllegalArgumentException( "RemoteKeyListener can't be null" ) ;
        }
        this.listener = listener ;
        activationMap = RemoteKeyCode.getDefaultKeyActivationMap() ;
        for( String keyId : RemoteKeyCode.getsKeysOfType( KEY_TYPE_FN ) ) {
            fnHandlers.put( keyId, null ) ;
        }
    }
    
    public String getName() {
        return this.name ;
    }
    
    public void setKeyEnabled( boolean enable, String... keyIds ) {
        for( String id : keyIds ) {
            assertValidKey( id ) ;
            activationMap.put( id, enable ) ;
        }
    }
    
    public void enableAllKeys() {
        for( String key : activationMap.keySet() ) {
            activationMap.put( key, true ) ;
        }
    }
    
    public void enableNavKeys( boolean enable ) {
        enableKeyType( KEY_TYPE_NAV_CONTROL, enable ) ;
    }
    
    public void enableRunKeys( boolean enable ) {
        enableKeyType( KEY_TYPE_RUN, enable ) ;
    }
    
    private void enableKeyType( String keyType, boolean enable ) {
        for( String key : getsKeysOfType( keyType ) ) {
            activationMap.put( key, enable ) ;
        }
    }
    
    public void disableAllKeys() {
        for( String key : activationMap.keySet() ) {
            activationMap.put( key, false ) ;
        }
    }

    public boolean isKeyEnabled( String keyId ) {
        assertValidKey( keyId ) ;
        return activationMap.get( keyId ) ;
    }

    public void setFnHandler( String keyId, Handler handler ) {
        assertValidFnKey( keyId ) ;
        fnHandlers.put( keyId, handler ) ;
    }
    
    public void setFnHandler( String keyId, Handler handler, boolean enable ) {
        assertValidFnKey( keyId ) ;
        fnHandlers.put( keyId, handler ) ;
        setKeyEnabled( enable, keyId ) ;
    }
    
    public void clearFnHandler( String... keyIds ) {
        for( String keyId : keyIds ) {
            if( !fnHandlers.containsKey( keyId ) ) {
                throw new IllegalArgumentException( 
                        "Feature can't be cleared " + 
                        "from a non function key." + keyId ) ;
            }
            fnHandlers.put( keyId, null ) ;
        }
    }
    
    private void assertValidFnKey( String keyId ) {
        if( !fnHandlers.containsKey( keyId ) ) {
            throw new IllegalArgumentException( 
             "Key " + keyId + " is not valid for registering FnKeyListener." ) ;
        }
    }
    
    public final void processRemoteKeyEvent( KeyEvent event ) {
        
        this.lastKeyReceivedTime = new Date() ;

        if( !isKeyEnabled( event.getKeyId() ) ) {
            log.debug( "Key " + event.getKeyId() + " deactivated. Ignoring." ) ;
            return ;
        }
        
        String type = event.getBtnType() ;
        String code = event.getBtnCode() ;
        
        switch( type ) {
            case "Run":
                switch( code ) {
                    case "PlayPause":
                        listener.handlePlayPauseResumeKey() ;
                        break ;
                    case "Stop":
                        listener.handleStopKey() ;
                        break ;
                }
                break ;
                
            case "NavControl":
                switch( code ) {
                    case "Up":
                        listener.handleUpNavKey() ;
                        break ;
                    case "Down":
                        listener.handleDownNavKey() ;
                        break ;
                    case "Right":
                        listener.handleRightNavKey() ;
                        break ;
                    case "Left":
                        listener.handleLeftNavKey() ;
                        break ;
                    case "Select":
                        listener.handleSelectNavKey() ;
                        break ;
                }
                break ;

            case "Function":
                processFunctionKey( event.getKeyId() ) ;
                break ;
        }
    }
    
    public void processFunctionKey( String keyId ) {
        
        log.debug( "Processing function key " + keyId ) ;
        if( !activationMap.containsKey( keyId ) ) {
            throw new IllegalArgumentException( "No function key by ID : " + keyId ) ;
        }
        
        Handler listener = fnHandlers.get( keyId ) ;
        if( listener != null ) {
            log.debug( "Function is mapped to - " + listener.getBtnHint() + "\n" ) ;
            listener.handle() ;
        }
    }
    
    public Map<String, Handler> getFnHandlers() {
        return this.fnHandlers ;
    }
    
    public String getDebugState() {
        StringBuffer buffer = new StringBuffer() ;
        buffer.append( "Processor = " + getName() + ". Activated keys:\n" ) ;
        for( String key : activationMap.keySet() ) {
            if( activationMap.get( key ) ) {
                buffer.append( "\t" + key + "\n" ) ;
            }
        }
        return buffer.toString() ;
    }
    
    public long getTimeSinceLastKeyReceived() {
        if( this.lastKeyReceivedTime == null ) {
            return -1 ;
        }
        return ( new Date().getTime() - lastKeyReceivedTime.getTime() )/1000 ;
    }

    public void clearLastKeyReceivedTime() {
        this.lastKeyReceivedTime = null ;
    }
}
