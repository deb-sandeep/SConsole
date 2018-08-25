package com.sandy.sconsole.core.remote;

import java.util.* ;

public class RemoteKeyCode {

    public static final String KEY_TYPE_SCR_SEL     = "ScreenletSelection" ;
    public static final String KEY_TYPE_NAV_CONTROL = "NavControl" ;
    public static final String KEY_TYPE_RUN         = "Run" ;
    public static final String KEY_TYPE_FN          = "Function" ;

    public static final String SCR_SEL_SHOWHIDE   = "ScreenletSelection@ShowHide" ;
    public static final String NAV_CONTROL_UP     = "NavControl@Up" ;
    public static final String NAV_CONTROL_LEFT   = "NavControl@Left" ;
    public static final String NAV_CONTROL_RIGHT  = "NavControl@Right" ;
    public static final String NAV_CONTROL_DOWN   = "NavControl@Down" ;
    public static final String NAV_CONTROL_SELECT = "NavControl@Select" ;
    public static final String NAV_CONTROL_CANCEL = "NavControl@Cancel" ;
    public static final String RUN_PLAYPAUSE      = "Run@PlayPause" ;
    public static final String RUN_STOP           = "Run@Stop" ;
    public static final String FN_A               = "Function@A" ;
    public static final String FN_B               = "Function@B" ;
    public static final String FN_C               = "Function@C" ;
    public static final String FN_D               = "Function@D" ;
    public static final String FN_E               = "Function@E" ;
    public static final String FN_F               = "Function@F" ;
    public static final String FN_G               = "Function@G" ;
    public static final String FN_H               = "Function@H" ;
    
    private static Map<String, String[]> keyTypeCodeMap = new HashMap<>() ;
    private static List<String> validKeys = new ArrayList<>() ;
    
    static {
        keyTypeCodeMap.put( KEY_TYPE_NAV_CONTROL, new String[]{ 
            NAV_CONTROL_UP, 
            NAV_CONTROL_LEFT, 
            NAV_CONTROL_SELECT, 
            NAV_CONTROL_RIGHT, 
            NAV_CONTROL_DOWN,
            NAV_CONTROL_CANCEL
        } ) ;
        
        keyTypeCodeMap.put( KEY_TYPE_RUN, new String[]{ 
            RUN_PLAYPAUSE, 
            RUN_STOP 
        } ) ;
        
        keyTypeCodeMap.put( KEY_TYPE_FN, new String[]{ 
            FN_A, 
            FN_B, 
            FN_C, 
            FN_D, 
            FN_E, 
            FN_F, 
            FN_G, 
            FN_H 
        } ) ;    
        
        validKeys.add( NAV_CONTROL_UP ) ;
        validKeys.add( NAV_CONTROL_LEFT ) ;
        validKeys.add( NAV_CONTROL_RIGHT ) ;
        validKeys.add( NAV_CONTROL_DOWN ) ;
        validKeys.add( NAV_CONTROL_SELECT ) ;
        validKeys.add( NAV_CONTROL_CANCEL ) ;
        validKeys.add( RUN_PLAYPAUSE ) ;
        validKeys.add( RUN_STOP ) ;
        validKeys.add( FN_A ) ;
        validKeys.add( FN_B ) ;
        validKeys.add( FN_C ) ;
        validKeys.add( FN_D ) ;
        validKeys.add( FN_E ) ;
        validKeys.add( FN_F ) ;
        validKeys.add( FN_G ) ;
        validKeys.add( FN_H ) ;
    }
    
    public static String[] getsKeysOfType( String type ) {
        if( !keyTypeCodeMap.containsKey( type ) ) {
            throw new IllegalArgumentException( "Invalid key type - " + type ) ;
        }
        return keyTypeCodeMap.get( type ) ;
    }

    public static Map<String, Boolean> getDefaultKeyActivationMap() {
        Map<String, Boolean> map = new HashMap<>() ;
        for( String[] keys : keyTypeCodeMap.values() ) {
            for( String key : keys ) {
                map.put( key, false ) ;
            }
        }
        return map ;
    }
    
    public static boolean isValidKey( String keyId ) {
        return validKeys.contains( keyId ) ;
    }
    
    public static void assertValidKey( String keyId ) {
        if( !isValidKey( keyId ) ) {
            throw new IllegalArgumentException( "Invalid keyID = " + keyId ) ;
        }
    }
}
