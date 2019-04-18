package com.sandy.sconsole.core.remote;

import java.util.Map ;

import com.sandy.sconsole.api.remote.RemoteController ;

public abstract class KeyProcessor {
    
    public abstract String getName() ;
    public abstract void processKey( Key key ) ;
    public abstract Map<Key, String> getActivatedKeyInfo() ;
    
    // By default the center panel card is the navigation panel. This will
    // get overwritten in some key processor like that of Play State.
    public String getCenterPanelCardName() {
        return RemoteController.CENTER_NAV_PANEL ;
    }
    
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
