package com.sandy.sconsole.core.remote;

import static com.sandy.sconsole.core.remote.RemoteKeyCode.KEY_TYPE_FN ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.KEY_TYPE_NAV_CONTROL ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.KEY_TYPE_RUN ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.getsKeysOfType ;

import java.util.HashMap ;
import java.util.Map ;

public class KeyActivationManager {

    private Map<String, Boolean> keyActivationMap = null ;
    private Map<String, Integer> fnFeatureMap = new HashMap<>() ;
    
    public KeyActivationManager() {
        keyActivationMap = RemoteKeyCode.getDefaultKeyActivationMap() ;
        for( String keyId : RemoteKeyCode.getsKeysOfType( KEY_TYPE_FN ) ) {
            fnFeatureMap.put( keyId, -1 ) ;
        }
    }
    
    public void setFnKeyFeature( String keyId, int feature ) {
        if( !fnFeatureMap.containsKey( keyId ) ) {
            throw new IllegalArgumentException( "Feature can't be associated " + 
                                  "with a non function key." + keyId ) ;
        }
        fnFeatureMap.put( keyId, feature ) ;
    }
    
    public void clearFnKeyFeature( String keyId ) {
        if( !fnFeatureMap.containsKey( keyId ) ) {
            throw new IllegalArgumentException( "Feature can't be cleared " + 
                                  "from a non function key." + keyId ) ;
        }
        fnFeatureMap.put( keyId, -1 ) ;
    }
    
    public int getFnKeyFeature( String keyId ) {
        if( !fnFeatureMap.containsKey( keyId ) ) {
            throw new IllegalArgumentException( keyId + " is not a Function key.") ;
        }
        return fnFeatureMap.get( keyId ) ;
    }
    
    public boolean isKeyActive( String keyId ) {
        return keyActivationMap.get( keyId ) ;
    }

    private void enableKeyType( String keyType, boolean enable ) {
        for( String key : getsKeysOfType( keyType ) ) {
            keyActivationMap.put( key, enable ) ;
        }
    }
    
    public void enableKey( boolean enable, String... keyIds ) {
        for( String id : keyIds ) {
            setKeyEnable( id, enable ) ;
        }
    }
    
    public void setKeyEnable( String keyID, boolean enable ) {
        if( !keyActivationMap.containsKey( keyID ) ) {
            throw new IllegalArgumentException( "No key by ID : " + keyID ) ;
        }
        keyActivationMap.put( keyID, enable ) ;
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
}
