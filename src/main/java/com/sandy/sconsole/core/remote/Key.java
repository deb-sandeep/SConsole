package com.sandy.sconsole.core.remote;

import static com.sandy.sconsole.core.remote.RemoteKeyType.FUNCTION ;
import static com.sandy.sconsole.core.remote.RemoteKeyType.NAV_CONTROL ;
import static com.sandy.sconsole.core.remote.RemoteKeyType.RUN ;

import java.util.* ;

public enum Key {
    
    SCR_SEL_SHOWHIDE ( "ScreenletSelection@ShowHide", ""  ),
    UP               ( "NavControl@Up"              , ""  ),
    LEFT             ( "NavControl@Left"            , ""  ),
    RIGHT            ( "NavControl@Right"           , ""  ),
    DOWN             ( "NavControl@Down"            , ""  ),
    SELECT           ( "NavControl@Select"          , ""  ),
    CANCEL           ( "NavControl@Cancel"          , ""  ),
    PLAYPAUSE        ( "Run@PlayPause"              , ""  ),
    STOP             ( "Run@Stop"                   , ""  ),
    FN_A             ( "Function@A"                 , "A" ),
    FN_B             ( "Function@B"                 , "B" ),
    FN_C             ( "Function@C"                 , "C" ),
    FN_D             ( "Function@D"                 , "D" ),
    FN_E             ( "Function@E"                 , "E" ),
    FN_F             ( "Function@F"                 , "F" ),
    FN_G             ( "Function@G"                 , "G" ),
    FN_H             ( "Function@H"                 , "H" );
    
    private static Map<RemoteKeyType, Key[]> keyTypeCodeMap = new HashMap<>() ;
    private static List<Key> validKeys = new ArrayList<>() ;
    
    static {
        keyTypeCodeMap.put( NAV_CONTROL, new Key[]{ 
            UP, 
            LEFT, 
            SELECT, 
            RIGHT, 
            DOWN,
            CANCEL
        } ) ;
        
        keyTypeCodeMap.put( RUN, new Key[]{ 
            PLAYPAUSE, 
            STOP 
        } ) ;
        
        keyTypeCodeMap.put( FUNCTION, new Key[]{ 
            FN_A, 
            FN_B, 
            FN_C, 
            FN_D, 
            FN_E, 
            FN_F, 
            FN_G, 
            FN_H,
        } ) ;    
        
        validKeys.add( UP ) ;
        validKeys.add( LEFT ) ;
        validKeys.add( RIGHT ) ;
        validKeys.add( DOWN ) ;
        validKeys.add( SELECT ) ;
        validKeys.add( CANCEL ) ;
        validKeys.add( PLAYPAUSE ) ;
        validKeys.add( STOP ) ;
        validKeys.add( FN_A ) ;
        validKeys.add( FN_B ) ;
        validKeys.add( FN_C ) ;
        validKeys.add( FN_D ) ;
        validKeys.add( FN_E ) ;
        validKeys.add( FN_F ) ;
        validKeys.add( FN_G ) ;
        validKeys.add( FN_H ) ;
    }
    
    public static Key[] getsKeysOfType( RemoteKeyType type ) {
        if( !keyTypeCodeMap.containsKey( type ) ) {
            throw new IllegalArgumentException( "Invalid key type - " + type ) ;
        }
        return keyTypeCodeMap.get( type ) ;
    }

    public static Map<Key, KeyActivationInfo> getDefaultKeyActivationMap() {
        Map<Key, KeyActivationInfo> map = new HashMap<>() ;
        for( Key[] keys : keyTypeCodeMap.values() ) {
            for( Key key : keys ) {
                map.put( key, new KeyActivationInfo( key, false, null ) ) ;
            }
        }
        return map ;
    }
    
    public static boolean isValidKey( Key key ) {
        return validKeys.contains( key ) ;
    }
    
    public static void assertValidKey( Key key ) {
        if( !isValidKey( key ) ) {
            throw new IllegalArgumentException( "Invalid keyID = " + key ) ;
        }
    }
    
    // ----------------- Instance methods start --------------------------------

    private String value = null ;
    private String label = null ;
    
    private Key( String value ) {
        this( value, null ) ;
    }
    
    private Key( String value, String label ) {
        this.value = value ;
        this.label = label ;
    }
    
    public String getDefaultLabel() {
        return this.label ;
    }
    
    public String toString() {
        return this.value ;
    }
}
