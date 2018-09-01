package com.sandy.sconsole.core.remote;

import static com.sandy.sconsole.core.remote.KeyType.FUNCTION ;
import static com.sandy.sconsole.core.remote.KeyType.NAV_CONTROL ;
import static com.sandy.sconsole.core.remote.KeyType.RUN ;

import java.util.* ;

public enum Key {
    
    SCR_1        ( "ScreenletSelection@1"       , ""  ),
    SCR_2        ( "ScreenletSelection@2"       , ""  ),
    SCR_3        ( "ScreenletSelection@3"       , ""  ),
    SCR_4        ( "ScreenletSelection@4"       , ""  ),
    SCR_5        ( "ScreenletSelection@5"       , ""  ),
    SCR_6        ( "ScreenletSelection@6"       , ""  ),
    SCR_7        ( "ScreenletSelection@7"       , ""  ),
    SCR_8        ( "ScreenletSelection@8"       , ""  ),
    SCR_SHOWHIDE ( "ScreenletSelection@ShowHide", ""  ),
    UP           ( "NavControl@Up"              , ""  ),
    LEFT         ( "NavControl@Left"            , ""  ),
    RIGHT        ( "NavControl@Right"           , ""  ),
    DOWN         ( "NavControl@Down"            , ""  ),
    SELECT       ( "NavControl@Select"          , ""  ),
    CANCEL       ( "NavControl@Cancel"          , ""  ),
    PLAYPAUSE    ( "Run@PlayPause"              , ""  ),
    STOP         ( "Run@Stop"                   , ""  ),
    FN_A         ( "Function@A"                 , "A" ),
    FN_B         ( "Function@B"                 , "B" ),
    FN_C         ( "Function@C"                 , "C" ),
    FN_D         ( "Function@D"                 , "D" ),
    FN_E         ( "Function@E"                 , "E" ),
    FN_F         ( "Function@F"                 , "F" ),
    FN_G         ( "Function@G"                 , "G" ),
    FN_H         ( "Function@H"                 , "H" );
    
    public static Key decode( String str ) {
        
            if( str.equals( "ScreenletSelection@1"        )) { return SCR_1 ; }
       else if( str.equals( "ScreenletSelection@2"        )) { return SCR_2 ; }
       else if( str.equals( "ScreenletSelection@3"        )) { return SCR_3 ; }
       else if( str.equals( "ScreenletSelection@4"        )) { return SCR_4 ; }
       else if( str.equals( "ScreenletSelection@5"        )) { return SCR_5 ; }
       else if( str.equals( "ScreenletSelection@6"        )) { return SCR_6 ; }
       else if( str.equals( "ScreenletSelection@7"        )) { return SCR_7 ; }
       else if( str.equals( "ScreenletSelection@8"        )) { return SCR_8 ; }
       else if( str.equals( "ScreenletSelection@ShowHide" ) ) { return SCR_SHOWHIDE; }
       else if( str.equals( "NavControl@Up"               ) ) { return UP;               }
       else if( str.equals( "NavControl@Left"             ) ) { return LEFT;             }
       else if( str.equals( "NavControl@Right"            ) ) { return RIGHT;            }
       else if( str.equals( "NavControl@Down"             ) ) { return DOWN;             }
       else if( str.equals( "NavControl@Select"           ) ) { return SELECT;           }
       else if( str.equals( "NavControl@Cancel"           ) ) { return CANCEL;           }
       else if( str.equals( "Run@PlayPause"               ) ) { return PLAYPAUSE;        }
       else if( str.equals( "Run@Stop"                    ) ) { return STOP;             }
       else if( str.equals( "Function@A"                  ) ) { return FN_A;             }
       else if( str.equals( "Function@B"                  ) ) { return FN_B;             }
       else if( str.equals( "Function@C"                  ) ) { return FN_C;             }
       else if( str.equals( "Function@D"                  ) ) { return FN_D;             }
       else if( str.equals( "Function@E"                  ) ) { return FN_E;             }
       else if( str.equals( "Function@F"                  ) ) { return FN_F;             }
       else if( str.equals( "Function@G"                  ) ) { return FN_G;             }
       else if( str.equals( "Function@H"                  ) ) { return FN_H;             }
        
       throw new IllegalArgumentException( "String " + str + " is not a valid key." ) ;
    }
    
    private static Map<KeyType, Key[]> keyTypeCodeMap = new HashMap<>() ;
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
    
    public static Key[] getsKeysOfType( KeyType type ) {
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

    private String id = null ;
    private String label = null ;
    
    private KeyType keyType = null ;
    private String keyCode = null ;
    
    private Key( String id ) {
        this( id, null ) ;
    }
    
    private Key( String value, String label ) {
        this.id = value ;
        this.label = label ;
        this.keyType = KeyType.decode( value.split( "@" )[0] ) ;
        this.keyCode = value.split( "@" )[1] ;
    }
    
    public String getDefaultLabel() {
        return this.label ;
    }
    
    public KeyType getKeyType() {
        return this.keyType ;
    }
    
    public String getKeyCode() {
        return this.keyCode ;
    }
    
    public String toString() {
        return this.id ;
    }
}
