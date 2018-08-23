package com.sandy.sconsole.core.remote;

import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;

import java.util.* ;

import org.jfree.util.* ;

public class KeyActivationManager {

    private Map<String, Boolean> keyActivationMap = null ;
    private Map<String, FnKeyHandler> fnKeyListeners = new HashMap<>() ;
    
    public KeyActivationManager() {
        keyActivationMap = RemoteKeyCode.getDefaultKeyActivationMap() ;
        for( String keyId : RemoteKeyCode.getsKeysOfType( KEY_TYPE_FN ) ) {
            fnKeyListeners.put( keyId, null ) ;
        }
    }
    
    private void setFnKeyFeature( String keyId, FnKeyHandler listener ) {
        if( !fnKeyListeners.containsKey( keyId ) ) {
            throw new IllegalArgumentException( 
             "Key " + keyId + " is not valid for registering FnKeyListener." ) ;
        }
        fnKeyListeners.put( keyId, listener ) ;
    }
    
    public void clearFnKeyFeature( String... keyIds ) {
        for( String keyId : keyIds ) {
            if( !fnKeyListeners.containsKey( keyId ) ) {
                throw new IllegalArgumentException( 
                        "Feature can't be cleared " + 
                        "from a non function key." + keyId ) ;
            }
            fnKeyListeners.put( keyId, null ) ;
        }
    }
    
    public boolean isKeyActive( String keyId ) {
        return keyActivationMap.get( keyId ) ;
    }

    public void enableFnKey( String keyId, FnKeyHandler listener ) {
        setFnKeyFeature( keyId, listener ) ;
        enableKey( true, keyId ) ;
    }
    
    private void enableKeyType( String keyType, boolean enable ) {
        for( String key : getsKeysOfType( keyType ) ) {
            keyActivationMap.put( key, enable ) ;
        }
    }
    
    public void enableKey( boolean enable, String... keyIds ) {
        for( String id : keyIds ) {
            if( !keyActivationMap.containsKey( id ) ) {
                throw new IllegalArgumentException( "No key by ID : " + id ) ;
            }
            keyActivationMap.put( id, enable ) ;
        }
    }
    
    public void enableNavKeys( boolean enable ) {
        enableKeyType( KEY_TYPE_NAV_CONTROL, enable ) ;
    }
    
    public void enableRunKeys( boolean enable ) {
        enableKeyType( KEY_TYPE_RUN, enable ) ;
    }
    
    public void enableFnKeys( boolean enable ) {
        enableKeyType( KEY_TYPE_FN, enable ) ;
    }
    
    public void disableAllKeys() {
        for( String key : keyActivationMap.keySet() ) {
            keyActivationMap.put( key, false ) ;
        }
    }

    public void enableAllKeys() {
        for( String key : keyActivationMap.keySet() ) {
            keyActivationMap.put( key, true ) ;
        }
    }

    public void processFunctionKey( String keyCode ) {
        String keyId = RemoteKeyCode.KEY_TYPE_FN + "@" + keyCode ;
        Log.debug( "Processing function key " + keyCode ) ;
        if( !keyActivationMap.containsKey( keyId ) ) {
            throw new IllegalArgumentException( "No function key by ID : " + keyId ) ;
        }
        FnKeyHandler listener = fnKeyListeners.get( keyId ) ;
        if( listener != null ) {
            listener.process() ;
        }
    }
}
