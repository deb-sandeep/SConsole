package com.sandy.sconsole.core.remote;

import java.util.Map ;

public abstract class KeyProcessor {
    public abstract String getName() ;
    public abstract void processKey( Key key ) ;
    public abstract Map<Key, String> getActivatedKeyInfo() ;
    
    public String getDebugState() {
        return getName() ;
    }
    
}
