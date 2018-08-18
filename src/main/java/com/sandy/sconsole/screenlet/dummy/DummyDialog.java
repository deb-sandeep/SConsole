package com.sandy.sconsole.screenlet.dummy;

import java.awt.Dimension ;
import java.awt.GridLayout ;

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
        setLayout( new GridLayout( 2, 1 ) ) ;
        add( getFnButton( "A", "Shutdown" ) ) ;
        add( getFnButton( "B", "Hide" ) ) ;
    }

    @Override
    public Object getReturnValue() {
        return "Success" ;
    }

    @Override
    public void handleFunctionKey( String fnCode ) {
        log.debug( "Handling " + fnCode + " in the dialog." ) ;
        switch( fnCode ) {
            case "A":
                System.exit( 1 ) ;
            case "B":
                super.hideDialog() ;
                break ;
        }
    }
}
