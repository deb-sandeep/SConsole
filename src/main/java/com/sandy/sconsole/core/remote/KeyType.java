package com.sandy.sconsole.core.remote;

public enum KeyType {
    
    SCREEN_SEL  ( "ScreenletSelection" ),
    NAV_CONTROL ( "NavControl"         ),
    RUN         ( "Run"                ),
    FUNCTION    ( "Function"           ),
    TIME_PROJ   ( "TimeProjection"     );
    
    public static KeyType decode( String str ) {
        if( str.equals( "ScreenletSelection" ) ) {
            return SCREEN_SEL;
        }
        if( str.equals( "NavControl" ) ) {
            return NAV_CONTROL;
        }    
        if( str.equals( "Run" ) ) {
            return RUN;
        }    
        if( str.equals( "Function" ) ) {
            return FUNCTION;
        }
        if( str.equals( "TimeProjection" ) ) {
            return TIME_PROJ;
        }
        
        throw new IllegalArgumentException( "String " + str + " is not a valid key type." ) ;
    }
    
    private String value = null ;
    
    private KeyType( String value ) {
        this.value = value ;
    }
    
    public String toString() {
        return this.value ;
    }
}
