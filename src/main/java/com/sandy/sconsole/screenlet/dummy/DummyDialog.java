package com.sandy.sconsole.screenlet.dummy;

import javax.swing.JDialog ;
import javax.swing.JRootPane ;

import com.sandy.sconsole.SConsole ;

@SuppressWarnings( "serial" )
public class DummyDialog extends JDialog {

    public DummyDialog() {
        super( SConsole.getApp().getFrame(), true ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        super.setUndecorated( true ) ;
        super.getRootPane().setWindowDecorationStyle( JRootPane.NONE ) ;
        
        setBounds( 500, 500, 300, 300 ) ;
    }
}
