package com.sandy.sconsole.core.frame;

import java.awt.BorderLayout ;

import javax.swing.JLabel ;
import javax.swing.JPanel;

import com.sandy.sconsole.core.remote.RemoteKeyListener;

@SuppressWarnings("serial")
public abstract class AbstractDialogPanel extends JPanel 
    implements RemoteKeyListener {

    private SConsoleDialog parentDialog = null ;
    
    protected AbstractDialogPanel( String title ) {
        setLayout( new BorderLayout() ) ;
        setTitle( title ) ;
    }
    
    private void setTitle( String title ) {
        JLabel label = new JLabel( title ) ;
        add( label, BorderLayout.NORTH ) ;
    }
    
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
    
    public abstract Object getReturnValue() ;
    
    public void setParentDialog( SConsoleDialog dialog ) {
        this.parentDialog = dialog ;
    }
    
    public void hideDialog() {
        this.parentDialog.setVisible( false ) ;
    }

    @Override
    public boolean shouldProcessRunEvents() { return false ; }

    @Override
    public boolean shouldProcessNavEvents() { return true ; }

    @Override
    public boolean shouldProcessFnEvents() { return true ; }
}
