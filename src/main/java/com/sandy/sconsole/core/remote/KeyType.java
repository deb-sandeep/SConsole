package com.sandy.sconsole.core.remote;

public enum KeyType {
    
    SCREEN_SEL  ( "ScreenletSelection" ),
    NAV_CONTROL ( "NavControl"         ),
    RUN         ( "Run"                ),
    FUNCTION    ( "Function"           );
    
    private String value = null ;
    
    private KeyType( String value ) {
        this.value = value ;
    }
    
    public String toString() {
        return this.value ;
    }
}
