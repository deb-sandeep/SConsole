package com.sandy.sconsole.core.remote;

public class RemoteKeyAdapter implements RemoteKeyListener {

    @Override
    public void handleFunctionKey( String fnCode ) {}

    @Override
    public void processPlayPauseResumeKey() {}
    
    @Override
    public void processStopKey() {}
    
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
    public boolean isKeyActive( String keyId ) { return true ; }
}
