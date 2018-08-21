package com.sandy.sconsole.core.screenlet;

import static com.sandy.sconsole.core.CoreEventID.* ;

import com.sandy.common.bus.EventBus ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.frame.AbstractDialogPanel ;

public abstract class AbstractScreenlet implements Screenlet {

    private String displayName = null ;
    private RunState runState = RunState.STOPPED ;
    private ScreenletSmallPanel smallPanel = null ;
    private ScreenletLargePanel largePanel = null ;
    private EventBus eventBus = null ;
    
    protected AbstractScreenlet( String displayName ) {
        this.displayName = displayName ;
        this.eventBus = new EventBus() ;
    }
    
    // This function will be called by the framework after creating an
    // instance of this class.
    public Screenlet initialize() {
        this.smallPanel = createSmallPanel() ;
        this.largePanel = createLargePanel() ;
        
        eventBus.addSubscriberForEventRange( smallPanel, false, RANGE_MIN, RANGE_MAX ) ;
        
        return this ;
    }
    
    protected abstract ScreenletSmallPanel createSmallPanel() ;
    protected abstract ScreenletLargePanel createLargePanel() ;
    
    public void setDisplayName( String displayName ) {
        this.displayName = displayName ;
    }
    
    protected Object showDialog( AbstractDialogPanel panel ) {
        return SConsole.getApp().getFrame().showDialog( panel ) ;
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
        eventBus.publishEvent( SCREENLET_MINIMIZED, this ) ;
    }

    @Override
    public void isBeingMaximized() {
        this.smallPanel.setMaximizedBorder() ;
        eventBus.publishEvent( SCREENLET_MAXIMIZED, this ) ;
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

    @Override
    public boolean shouldProcessRunEvents() { return true ; }

    @Override
    public boolean shouldProcessNavEvents() { return true ; }

    @Override
    public boolean shouldProcessFnEvents() { return true ; }
    
    public EventBus getEventBus() { return this.eventBus ; }
}
