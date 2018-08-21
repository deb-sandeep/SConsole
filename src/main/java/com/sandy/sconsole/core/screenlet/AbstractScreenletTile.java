package com.sandy.sconsole.core.screenlet;

import java.awt.Color ;

import javax.swing.JPanel ;
import javax.swing.border.LineBorder ;

import com.sandy.common.bus.EventBus ;
import com.sandy.sconsole.SConsole ;

@SuppressWarnings( "serial" )
public abstract class AbstractScreenletTile extends JPanel {

    protected ScreenletPanel parent = null ;
    
    protected AbstractScreenletTile( ScreenletPanel mother ) {
        this.parent = mother ;
        super.setBackground( SConsole.BG_COLOR ) ;
        super.setBorder( new LineBorder( Color.DARK_GRAY.darker() ) );
    }
    
    public EventBus getEventBus() {
        return this.parent.getEventBus() ;
    }
}
