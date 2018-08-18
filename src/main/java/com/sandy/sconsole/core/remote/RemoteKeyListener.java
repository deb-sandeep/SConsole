package com.sandy.sconsole.core.remote;

public interface RemoteKeyListener {

    public static enum RunState { STOPPED, RUNNING, PAUSED } ;

    public void handleFunctionKey( String fnCode ) ;
    
    public void handleLeftNavKey() ;
    public void handleRightNavKey() ;
    public void handleUpNavKey() ;
    public void handleDownNavKey() ;
    public void handleSelectNavKey() ;
    
    public void run() ;
    public void pause() ;
    public void resume() ;
    public void stop() ;
    
    public boolean shouldProcessRunEvents() ;
    public boolean shouldProcessNavEvents() ;
    public boolean shouldProcessFnEvents() ;

    public RunState getCurrentRunState() ;
    public void setCurrentRunState( RunState state ) ;
}