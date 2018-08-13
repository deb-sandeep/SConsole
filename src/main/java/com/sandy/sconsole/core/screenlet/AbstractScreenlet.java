package com.sandy.sconsole.core.screenlet;

import javax.swing.JDialog ;

public abstract class AbstractScreenlet implements Screenlet {

    private String displayName = null ;
    private RunState runState = RunState.STOPPED ;
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
    
    protected void showDialog( JDialog dialog ) {
        
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
    public void isBeingMinimized() {
        this.smallPanel.setPassiveBorder() ;
    }

    @Override
    public void isBeingMaximized() {
        this.smallPanel.setMaximizedBorder() ;
    }

    @Override
    public void handleFunctionKey( String fnCode ) {}

    @Override
    public void run() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void stop() {}

    @Override
    public void handleLeftNavKey() {}

    @Override
    public void handleRightNavKey() {}

    @Override
    public void handleUpNavKey() {}

    @Override
    public void handleDownNavKey() {}

    @Override
    public void handleSelectNavKey() {}

    @Override
    public RunState getCurrentRunState() { return this.runState ; } ;

    @Override
    public void setCurrentRunState( RunState state ) {
        this.runState = state ;
    }
}
