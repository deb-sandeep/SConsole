package com.sandy.sconsole.core.frame;

import javax.swing.JPanel;

import com.sandy.sconsole.core.remote.RemoteKeyListener;

@SuppressWarnings("serial")
public abstract class AbstractDialogPanel extends JPanel 
	implements RemoteKeyListener {

	@Override public void handleFunctionKey(String fnCode) {}
	@Override public void handleLeftNavKey() {}
	@Override public void handleRightNavKey() {}
	@Override public void handleUpNavKey() {}
	@Override public void handleDownNavKey() {}
	@Override public void handleSelectNavKey() {}

	@Override public RunState getCurrentRunState() { throw new UnsupportedOperationException() ; }
	@Override public void setCurrentRunState( RunState state ) { throw new UnsupportedOperationException() ; }
	@Override public void run() { throw new UnsupportedOperationException() ; }
	@Override public void pause() { throw new UnsupportedOperationException() ; }
	@Override public void resume() { throw new UnsupportedOperationException() ; }
	@Override public void stop() { throw new UnsupportedOperationException() ; }
}
