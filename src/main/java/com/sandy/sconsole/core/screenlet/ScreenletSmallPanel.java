package com.sandy.sconsole.core.screenlet;

import java.awt.BorderLayout ;
import java.awt.Color ;

import javax.swing.border.Border ;
import javax.swing.border.CompoundBorder ;
import javax.swing.border.LineBorder ;

import com.sandy.sconsole.SConsole ;

@SuppressWarnings( "serial" )
public class ScreenletSmallPanel extends ScreenletPanel {
    
    private static final Color MAXIMIZED_BORDER_COLOR  = Color.decode( "0x00FF00" ) ;
    private static final Color ACTIVE_BORDER_COLOR     = Color.decode( "0xFF0000" ) ;
    private static final Color PASSIVE_BORDER_COLOR    = Color.decode( "0x353535" ) ;
    private static final Color EMPTY_BORDER_COLOR      = SConsole.BG_COLOR ;

    private static final Border MAX_BORDER     = new LineBorder( MAXIMIZED_BORDER_COLOR, 1 ) ; 
    private static final Border ACTIVE_BORDER  = new LineBorder( ACTIVE_BORDER_COLOR, 2 ) ; 
    private static final Border PASSIVE_BORDER = new LineBorder( PASSIVE_BORDER_COLOR, 1 ) ; 
    private static final Border EMPTY_BORDER   = new LineBorder( EMPTY_BORDER_COLOR, 2 ) ; 
    
    private CompoundBorder border = new CompoundBorder( PASSIVE_BORDER, 
                                                        EMPTY_BORDER ) ;
    
    public ScreenletSmallPanel( Screenlet parent ) {
        super( parent ) ;
        setBorder( border ) ;
        setLayout( new BorderLayout() ) ;
    }
    
    public void setMaximizedBorder() {
        border = new CompoundBorder( MAX_BORDER, border.getInsideBorder() ) ;
        setBorder( border ) ;
    }
    
    public void setPassiveBorder() {
        border = new CompoundBorder( PASSIVE_BORDER, border.getInsideBorder() ) ;
        setBorder( border ) ;
    }
    
    public void setActiveBorder( boolean active ) {
        
        Border insideBorder = null ;
        
        if( active ) {
            insideBorder = ACTIVE_BORDER ;
        }
        else {
            insideBorder = EMPTY_BORDER ;
        }
        
        border = new CompoundBorder( border.getOutsideBorder(), insideBorder ) ;
        setBorder( border ) ;
    }
}
