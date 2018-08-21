package com.sandy.sconsole.screenlet.study.large;

import java.awt.Color ;

import javax.swing.JPanel ;
import javax.swing.border.LineBorder ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;

@SuppressWarnings( "serial" )
public abstract class AbstractScreenletTile extends JPanel {

    protected ScreenletPanel parent    = null ;
    
    protected AbstractScreenletTile( ScreenletPanel mother ) {
        this.parent = mother ;
        super.setBackground( SConsole.BG_COLOR ) ;
        super.setBorder( new LineBorder( Color.DARK_GRAY.darker().darker() ) );
    }
}
