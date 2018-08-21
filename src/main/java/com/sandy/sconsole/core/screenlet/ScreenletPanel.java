package com.sandy.sconsole.core.screenlet;

import static com.sandy.sconsole.core.frame.UIConstant.BG_COLOR ;

import java.awt.BorderLayout ;

import javax.swing.JPanel ;

import com.sandy.common.bus.EventBus ;

@SuppressWarnings( "serial" )
public abstract class ScreenletPanel extends JPanel {

    private Screenlet parent = null ;
    
    protected ScreenletPanel( Screenlet parent ) {
        this.parent = parent ;
        super.setBackground( BG_COLOR ) ;
        super.setLayout( new BorderLayout() ) ;
    }
    
    public Screenlet getScreenlet() {
        return this.parent ;
    }
    
    public EventBus getEventBus() {
        return this.parent.getEventBus() ;
    }
}
