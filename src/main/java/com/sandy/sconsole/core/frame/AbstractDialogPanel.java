package com.sandy.sconsole.core.frame;

import java.awt.* ;

import javax.swing.* ;

import com.sandy.sconsole.core.remote.* ;

@SuppressWarnings("serial")
public abstract class AbstractDialogPanel extends JPanel 
    implements RemoteKeyListener {

    public static final Font  FNBTN_FONT   = new Font( "Courier", Font.PLAIN, 20 ) ;
    
    private SConsoleDialog parentDialog = null ;
    protected RemoteKeyEventProcessor keyProcessor = null ;
    
    protected AbstractDialogPanel( String title ) {
        super.setName( title ) ;
        keyProcessor = new RemoteKeyEventProcessor( "Dialog" + title , this ) ;
        setLayout( new BorderLayout() ) ;
        setBackground( SConsoleDialog.BG_COLOR ) ;
    }
    
    public void isBeingMadeVisible() {} ;
    public void isBeingHidden() {} ;
    
    @Override public void handleLeftNavKey() {}
    @Override public void handleRightNavKey() {}
    @Override public void handleUpNavKey() {}
    @Override public void handleDownNavKey() {}
    @Override public void handleSelectNavKey() {}
    @Override public void handleCancelNavKey() {}

    @Override public void handlePlayPauseResumeKey() {} ;
    @Override public void handleStopKey() {} ;
    
    public abstract Object getReturnValue() ;
    
    public void setParentDialog( SConsoleDialog dialog ) {
        this.parentDialog = dialog ;
    }
    
    public void hideDialog() {
        this.parentDialog.setVisible( false ) ;
    }

    public RemoteKeyEventProcessor getKeyProcessor() {
        return keyProcessor ;
    }
}
