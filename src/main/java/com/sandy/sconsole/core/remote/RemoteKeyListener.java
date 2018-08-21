package com.sandy.sconsole.core.remote;

public interface RemoteKeyListener {

    public void handleFunctionKey( String fnCode ) ;
    
    public void handleLeftNavKey() ;
    public void handleRightNavKey() ;
    public void handleUpNavKey() ;
    public void handleDownNavKey() ;
    public void handleSelectNavKey() ;

    public void processPlayPauseResumeKey() ;
    public void processStopKey() ;
    
    public boolean shouldProcessRunEvents() ;
    public boolean shouldProcessNavEvents() ;
    public boolean shouldProcessFnEvents() ;
}
