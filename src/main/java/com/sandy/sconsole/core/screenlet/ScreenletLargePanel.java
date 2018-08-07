package com.sandy.sconsole.core.screenlet;

import java.awt.Color ;

@SuppressWarnings( "serial" )
public class ScreenletLargePanel extends ScreenletPanel {
    
    private static final Color ACTIVE_BORDER_COLOR  = Color.decode( "0x00FF00" ) ;
    private static final Color PASSIVE_BORDER_COLOR = Color.decode( "0x3C3C3C" ) ;
    
    public ScreenletLargePanel( Screenlet parent ) {
        super( parent ) ;
    }
}
