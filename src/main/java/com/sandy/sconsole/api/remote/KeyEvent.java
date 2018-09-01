package com.sandy.sconsole.api.remote;

import com.sandy.sconsole.core.remote.Key ;

public class KeyEvent {
    
    private Key key = null ;
    
    public KeyEvent( String type, String code ) {
        key = Key.valueOf( type + "@" + code ) ;
    }
    
    public Key getKey() {
        return this.key ;
    }
    
    public String toString() {
        return key.toString() ;
    }
}
