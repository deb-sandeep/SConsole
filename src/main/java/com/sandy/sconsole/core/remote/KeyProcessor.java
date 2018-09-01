package com.sandy.sconsole.core.remote;

public abstract class KeyProcessor {
    public abstract String getName() ;
    public abstract void processKey( Key key ) ;
    
    public String getDebugState() {
        return getName() ;
    }
}
