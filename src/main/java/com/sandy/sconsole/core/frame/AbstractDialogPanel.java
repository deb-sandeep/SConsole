package com.sandy.sconsole.core.frame;

import static com.sandy.sconsole.core.frame.SConsoleDialog.BG_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.FN_A_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.FN_B_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.FN_C_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.FN_D_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.FN_E_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.FN_F_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.FN_G_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.FN_H_COLOR ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.KEY_TYPE_FN ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.KEY_TYPE_NAV_CONTROL ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.KEY_TYPE_RUN ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.getsKeysOfType ;
import static javax.swing.SwingConstants.CENTER ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.util.Map ;

import javax.swing.BorderFactory ;
import javax.swing.JLabel ;
import javax.swing.JPanel;

import com.sandy.sconsole.core.remote.RemoteKeyCode ;
import com.sandy.sconsole.core.remote.RemoteKeyListener;

@SuppressWarnings("serial")
public abstract class AbstractDialogPanel extends JPanel 
    implements RemoteKeyListener {

    public static final Font  FNBTN_FONT   = new Font( "Courier", Font.PLAIN, 20 ) ;
    
    private SConsoleDialog parentDialog = null ;
    private Map<String, Boolean> keyActivationMap = null ;
    
    protected AbstractDialogPanel( String title ) {
        super.setName( title ) ;
        this.keyActivationMap = RemoteKeyCode.getDefaultKeyActivationMap() ;
        setLayout( new BorderLayout() ) ;
        setBackground( SConsoleDialog.BG_COLOR ) ;
    }
    
    @Override public void handleFunctionKey(String fnCode) {}
    @Override public void handleLeftNavKey() {}
    @Override public void handleRightNavKey() {}
    @Override public void handleUpNavKey() {}
    @Override public void handleDownNavKey() {}
    @Override public void handleSelectNavKey() {}

    @Override public void processPlayPauseResumeKey() {} ;
    @Override public void processStopKey() {} ;
    
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
        return keyActivationMap.get( keyId ) ;
    }
    
    private void enableKeyType( String keyType, boolean enable ) {
        for( String key : getsKeysOfType( keyType ) ) {
            keyActivationMap.put( key, enable ) ;
        }
    }
    
    public void enableKey( String keyID, boolean enable ) {
        if( !keyActivationMap.containsKey( keyID ) ) {
            throw new IllegalArgumentException( "No key by ID : " + keyID ) ;
        }
        keyActivationMap.put( keyID, enable ) ;
    }
    
    public void enableKey( boolean enable, String... keyIds ) {
        for( String id : keyIds ) {
            enableKey( id, enable ) ;
        }
    }
    
    public void enableNavKeys( boolean enable ) {
        enableKeyType( KEY_TYPE_NAV_CONTROL, enable ) ;
    }
    
    public void enableRunKeys( boolean enable ) {
        enableKeyType( KEY_TYPE_RUN, enable ) ;
    }
    
    public void enableFnKeys( boolean enable ) {
        enableKeyType( KEY_TYPE_FN, enable ) ;
    }
    
    public void disableAllKeys() {
        for( String key : keyActivationMap.keySet() ) {
            keyActivationMap.put( key, false ) ;
        }
    }

    public void enableAllKeys() {
        for( String key : keyActivationMap.keySet() ) {
            keyActivationMap.put( key, true ) ;
        }
    }
}
