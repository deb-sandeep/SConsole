package com.sandy.sconsole.core.screenlet;

import com.sandy.sconsole.core.remote.RemoteKeyListener ;

public interface Screenlet extends RemoteKeyListener {

    public void setDisplayName( String displayName ) ;
    public String getDisplayName() ;
    
    public ScreenletSmallPanel getSmallPanel() ;
    public ScreenletLargePanel getLargePanel() ;
    
    public void isBeingMinimized() ;
    public void isBeingMaximized() ;
}
