package com.sandy.sconsole.core.screenlet;

public interface Screenlet {

    public void setDisplayName( String displayName ) ;
    public String getDisplayName() ;
    
    public ScreenletSmallPanel getSmallPanel() ;
    public ScreenletLargePanel getLargePanel() ;
    
    public void handleFunctionKeyPress( String fnCode ) ;
    public void handleNavKeyPress( String navKeyCode ) ;
    public void handleRunKeyPress( String code ) ;
    
    public void isBeingMinimized() ;
    public void isBeingMaximized() ;
}
