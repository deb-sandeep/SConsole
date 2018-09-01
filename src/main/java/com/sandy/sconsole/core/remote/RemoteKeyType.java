package com.sandy.sconsole.core.remote;

public enum RemoteKeyType {
    
    SCREEN_SEL  ( "ScreenletSelection" ),
    NAV_CONTROL ( "NavControl"         ),
    RUN         ( "Run"                ),
    FUNCTION    ( "Function"           );
    
    private String value = null ;
    
    private RemoteKeyType( String value ) {
        this.value = value ;
    }
    
    public String toString() {
        return this.value ;
    }
}
