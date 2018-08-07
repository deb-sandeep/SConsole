package com.sandy.sconsole.core.screenlet;

import java.awt.Color ;

import javax.swing.JPanel ;
import javax.swing.border.Border ;
import javax.swing.border.LineBorder ;

@SuppressWarnings( "serial" )
public class ScreenletSmallPanel extends JPanel {
    
    private static final Color MAXIMIZED_BORDER_COLOR  = Color.decode( "0x00FF00" ) ;
    private static final Color ACTIVE_BORDER_COLOR     = Color.decode( "0xFF0000" ) ;
    private static final Color PASSIVE_BORDER_COLOR    = Color.decode( "0x3C3C3C" ) ;

    private static final Border MAX_BORDER     = new LineBorder( MAXIMIZED_BORDER_COLOR, 1 ) ; 
    private static final Border ACTIVE_BORDER  = new LineBorder( ACTIVE_BORDER_COLOR, 1 ) ; 
    private static final Border PASSIVE_BORDER = new LineBorder( PASSIVE_BORDER_COLOR, 1 ) ; 
    
    public ScreenletSmallPanel() {
        setPassiveBorder() ;
    }
    
    public void setMaximizedBorder() {
        setBorder( MAX_BORDER ) ;
    }
    
    public void setPassiveBorder() {
        setBorder( PASSIVE_BORDER ) ;
    }
    
    public void setActiveBorder( boolean flag ) {
        
    }
}
