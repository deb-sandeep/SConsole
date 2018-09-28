package com.sandy.sconsole.core.remote;

import java.util.Map ;

public abstract class KeyProcessor {
    public abstract String getName() ;
    public abstract void processKey( Key key ) ;
    public abstract Map<Key, String> getActivatedKeyInfo() ;
    
    public String getDebugState() {
        StringBuffer buffer = new StringBuffer( "KeyProcessor - " + getName() + "[ " ) ;
        for( Key key : getActivatedKeyInfo().keySet() ) {
            buffer.append( "\n\t\t" )
                  .append( key.toString() )
                  .append( " - " )
                  .append( getActivatedKeyInfo().get( key ) ) ;
        }
        buffer.append( "\n]" ) ;
        
        return buffer.toString() ;
    }
    
}
