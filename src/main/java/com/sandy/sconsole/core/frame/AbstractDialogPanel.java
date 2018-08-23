package com.sandy.sconsole.core.frame;

import static com.sandy.sconsole.core.frame.SConsoleDialog.BG_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.* ;
import static javax.swing.SwingConstants.* ;

import java.awt.* ;

import javax.swing.* ;

import com.sandy.sconsole.core.remote.* ;

@SuppressWarnings("serial")
public abstract class AbstractDialogPanel extends JPanel 
    implements RemoteListener {

    public static final Font  FNBTN_FONT   = new Font( "Courier", Font.PLAIN, 20 ) ;
    
    private SConsoleDialog parentDialog = null ;
    protected KeyActivationManager kaMgr = null ;
    
    protected AbstractDialogPanel( String title ) {
        super.setName( title ) ;
        this.kaMgr = new KeyActivationManager() ;
        setLayout( new BorderLayout() ) ;
        setBackground( SConsoleDialog.BG_COLOR ) ;
    }
    
    @Override public void handleLeftNavKey() {}
    @Override public void handleRightNavKey() {}
    @Override public void handleUpNavKey() {}
    @Override public void handleDownNavKey() {}
    @Override public void handleSelectNavKey() {}

    @Override public void processPlayPauseResumeKey() {} ;
    @Override public void processStopKey() {} ;
    
    @Override public void handleFunctionKey(String fnCode) {
        this.kaMgr.processFunctionKey( fnCode ) ;
    }
    
    public abstract Object getReturnValue() ;
    
    public void setParentDialog( SConsoleDialog dialog ) {
        this.parentDialog = dialog ;
    }
    
    public void hideDialog() {
        this.parentDialog.setVisible( false ) ;
    }

    protected JPanel getFnButton( String fnCode, String text ) {
        
        JLabel label = new JLabel( text ) ;
        label.setFont( FNBTN_FONT ) ;
        label.setForeground( Color.WHITE ) ;
        label.setHorizontalAlignment( CENTER ) ;
        label.setVerticalAlignment( CENTER ) ;
        
        JPanel panel = new JPanel( new BorderLayout() ) ;
        panel.setBackground( getFnColor( fnCode ) ) ;
        panel.add( label ) ;
        panel.setBorder( BorderFactory.createLineBorder( BG_COLOR, 10 ) ) ;
        
        return panel ;
    }
    
    private Color getFnColor( String fnCode ) {
        switch( fnCode ) {
            case "A":
                return FN_A_COLOR ;
            case "B":
                return FN_B_COLOR ;
            case "C":
                return FN_C_COLOR ;
            case "D":
                return FN_D_COLOR ;
            case "E":
                return FN_E_COLOR ;
            case "F":
                return FN_F_COLOR ;
            case "G":
                return FN_G_COLOR ;
            case "H":
                return FN_H_COLOR ;
        }
        return Color.RED ;
    }

    @Override
    public boolean isKeyActive( String keyId ) {
        return kaMgr.isKeyActive( keyId ) ;
    }
    
    public KeyActivationManager getKeyActivationManager() {
        return this.kaMgr ;
    }
}
