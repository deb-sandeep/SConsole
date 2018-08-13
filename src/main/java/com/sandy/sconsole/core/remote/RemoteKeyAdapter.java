package com.sandy.sconsole.core.remote;

public class RemoteKeyAdapter implements RemoteKeyListener {

    private RunState runState = RunState.STOPPED ;
    
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
