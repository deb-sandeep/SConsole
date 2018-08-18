package com.sandy.sconsole.screenlet.dummy;

import java.awt.Dimension ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.frame.AbstractDialogPanel ;

@SuppressWarnings( "serial" )
public class DummyDialog extends AbstractDialogPanel {
    
    private static final Logger log = Logger.getLogger( DummyDialog.class ) ;

    public DummyDialog() {
        super( "Dummy" ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        setPreferredSize( new Dimension( 400, 400 ) ) ;
    }

    @Override
    public Object getReturnValue() {
        return "Success" ;
    }

    @Override
    public void handleSelectNavKey() {
        log.debug( "Select key press received. Hiding the dialog." ) ;
        super.hideDialog() ;
    }
}
