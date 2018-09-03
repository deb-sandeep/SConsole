package com.sandy.sconsole.core.screenlet;

import com.sandy.common.bus.* ;

public interface Screenlet {

    public static enum RunState { STOPPED, RUNNING, PAUSED } ;

    public void setDisplayName( String displayName ) ;
    public String getDisplayName() ;
    
    public ScreenletSmallPanel getSmallPanel() ;
    public ScreenletLargePanel getLargePanel() ;
    
    public void isBeingMinimized() ;
    public void isBeingMaximized() ;
    public boolean isVisible() ;
    
    public EventBus getEventBus() ;

    public RunState getRunState() ;
    public void setCurrentRunState( RunState state ) ;
}
