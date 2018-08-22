package com.sandy.sconsole.core.screenlet;

import static com.sandy.sconsole.core.CoreEventID.RANGE_MAX ;
import static com.sandy.sconsole.core.CoreEventID.RANGE_MIN ;
import static com.sandy.sconsole.core.CoreEventID.SCREENLET_MAXIMIZED ;
import static com.sandy.sconsole.core.CoreEventID.SCREENLET_MINIMIZED ;
import static com.sandy.sconsole.core.CoreEventID.SCREENLET_PAUSE ;
import static com.sandy.sconsole.core.CoreEventID.SCREENLET_PLAY ;
import static com.sandy.sconsole.core.CoreEventID.SCREENLET_RESUME ;
import static com.sandy.sconsole.core.CoreEventID.SCREENLET_STOP ;

import com.sandy.common.bus.EventBus ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.frame.AbstractDialogPanel ;
import com.sandy.sconsole.core.remote.KeyActivationManager ;

public abstract class AbstractScreenlet implements Screenlet {

    private String displayName = null ;
    private RunState runState = RunState.STOPPED ;
    private ScreenletSmallPanel smallPanel = null ;
    private ScreenletLargePanel largePanel = null ;
    private EventBus eventBus = null ;
    protected KeyActivationManager kaMgr = null ;
    
    protected AbstractScreenlet( String displayName ) {
        this.displayName = displayName ;
        this.eventBus = new EventBus() ;
        this.kaMgr = new KeyActivationManager() ;
    }
    
    // This function will be called by the framework after creating an
    // instance of this class.
    public final Screenlet initialize() {
        this.smallPanel = createSmallPanel() ;
        this.largePanel = createLargePanel() ;
        
        eventBus.addSubscriberForEventRange( smallPanel, false, RANGE_MIN, RANGE_MAX ) ;
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
    public boolean isKeyActive( String keyId ) {
        return kaMgr.isKeyActive( keyId ) ;
    }

    @Override
    public final void processPlayPauseResumeKey() {
        switch( getCurrentRunState() ) {
            case RUNNING:
                setCurrentRunState( RunState.PAUSED ) ;
                pause() ;
                eventBus.publishEvent( SCREENLET_PAUSE, this );
                break ;
            case PAUSED:
                setCurrentRunState( RunState.RUNNING ) ;
                resume() ;
                eventBus.publishEvent( SCREENLET_RESUME, this );
                break ;
            case STOPPED:
                setCurrentRunState( RunState.RUNNING ) ;
                play() ;
                eventBus.publishEvent( SCREENLET_PLAY, this );
                break ;
        }
    }
    
    @Override
    public final void processStopKey() {
        if( getCurrentRunState() != RunState.STOPPED ) {
            setCurrentRunState( RunState.STOPPED ) ; 
            stop() ;
            eventBus.publishEvent( SCREENLET_STOP, this );
        }
    }
    
    @Override
    public void handleFunctionKey( String fnCode ) {}

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

    public EventBus getEventBus() { return this.eventBus ; }
    
    public void play(){} ;
    public void pause(){} ;
    public void resume(){} ;
    public void stop(){} ;

    public KeyActivationManager getKeyActivationManager() {
        return this.kaMgr ;
    }
}
