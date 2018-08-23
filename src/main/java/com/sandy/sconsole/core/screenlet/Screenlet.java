package com.sandy.sconsole.core.screenlet;

import com.sandy.common.bus.* ;
import com.sandy.sconsole.core.remote.* ;

public interface Screenlet extends RemoteListener {

    public static enum RunState { STOPPED, RUNNING, PAUSED } ;

    public void setDisplayName( String displayName ) ;
    public String getDisplayName() ;
    
    public ScreenletSmallPanel getSmallPanel() ;
    public ScreenletLargePanel getLargePanel() ;
    
    public void isBeingMinimized() ;
    public void isBeingMaximized() ;
    
    public EventBus getEventBus() ;

    public RunState getCurrentRunState() ;
    public void setCurrentRunState( RunState state ) ;
}
