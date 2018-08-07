package com.sandy.sconsole.core.screenlet;

import java.awt.Color ;

import javax.swing.JPanel ;
import javax.swing.border.LineBorder ;

@SuppressWarnings( "serial" )
public class ScreenletLargePanel extends JPanel {
    
    private static final Color ACTIVE_BORDER_COLOR  = Color.decode( "0x00FF00" ) ;
    private static final Color PASSIVE_BORDER_COLOR = Color.decode( "0x3C3C3C" ) ;
    
    private boolean isActive = false ;
    
    public ScreenletLargePanel() {
    }
}
