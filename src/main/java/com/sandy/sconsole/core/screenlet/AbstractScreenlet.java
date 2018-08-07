package com.sandy.sconsole.core.screenlet;

public abstract class AbstractScreenlet implements Screenlet {
    
    private String displayName = null ;
    private ScreenletSmallPanel smallPanel = null ;
    private ScreenletLargePanel largePanel = null ;
    
    protected AbstractScreenlet( String displayName ) {
        this.displayName = displayName ;
    }
    
    // This function will be called by the framework after creating an
    // instance of this class.
    public Screenlet initialize() {
        this.smallPanel = createSmallPanel() ;
        this.largePanel = createLargePanel() ;
        return this ;
    }
    
    protected abstract ScreenletSmallPanel createSmallPanel() ;
    protected abstract ScreenletLargePanel createLargePanel() ;
    
    public void setDisplayName( String displayName ) {
        this.displayName = displayName ;
    }

    @Override
    public String getDisplayName() {
        return this.displayName ;
    }
    
    @Override
    public ScreenletSmallPanel getSmallPanel() {
        return this.smallPanel ;
    }
    
    @Override
    public ScreenletLargePanel getLargePanel() {
        return this.largePanel ;
    }
    
    @Override
    public void toggleStartPause() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void handleFunctionKeyPress( String fnCode ) {
    }

    @Override
    public void handleNavKeyPress( String navKeyCode ) {
    }

    @Override
    public void isBeingMinimized() {
        this.smallPanel.setPassiveBorder() ;
    }

    @Override
    public void isBeingMaximized() {
        this.smallPanel.setMaximizedBorder() ;
    }
}
