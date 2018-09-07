package com.sandy.sconsole.core.screenlet;

import com.sandy.common.bus.EventBus ;
import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.frame.AbstractDialogPanel ;

public abstract class AbstractScreenlet implements Screenlet {

    private String displayName = null ;
    private RunState runState = RunState.STOPPED ;
    private ScreenletSmallPanel smallPanel = null ;
    private ScreenletLargePanel largePanel = null ;
    private EventBus eventBus = null ;
    private boolean isVisible = false ;
    
    protected AbstractScreenlet( String displayName ) {
        this.displayName = displayName ;
        this.eventBus = new EventBus() ;
    }
    
    // This function will be called by the framework after creating an
    // instance of this class.
    public final Screenlet initialize() {
        this.smallPanel = createSmallPanel() ;
        this.largePanel = createLargePanel() ;
        
        eventBus.addSubscriberForEventRange( smallPanel, false, 
                                                EventCatalog.CORE_EVENT_RANGE_MIN, 
                                                EventCatalog.CORE_EVENT_RANGE_MAX ) ;
        initScreenlet() ;
        return this ;
    }
    
    protected abstract ScreenletSmallPanel createSmallPanel() ;
    protected abstract ScreenletLargePanel createLargePanel() ;
    protected void initScreenlet() {};
    
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
    public void isBeingMaximized() {
        this.smallPanel.setMaximizedBorder() ;
        this.isVisible = true ;
        eventBus.publishEvent( EventCatalog.SCREENLET_MAXIMIZED, this ) ;
    }
    
    @Override
    public void isBeingMinimized() {
        this.smallPanel.setPassiveBorder() ;
        this.isVisible = false ;
        eventBus.publishEvent( EventCatalog.SCREENLET_MINIMIZED, this ) ;
    }

    @Override
    public boolean isVisible() {
        return this.isVisible ;
    }

    @Override
    public RunState getRunState() { return this.runState ; } ;
    
    @Override
    public void setCurrentRunState( RunState state ) {
        this.runState = state ;
        eventBus.publishEvent( EventCatalog.SCREENLET_RUN_STATE_CHANGED, this ) ;
    }

    public EventBus getEventBus() { return this.eventBus ; }
}
