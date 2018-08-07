package com.sandy.sconsole.core.screenlet;

import javax.swing.JPanel ;

import com.sandy.sconsole.SConsole ;

@SuppressWarnings( "serial" )
public abstract class ScreenletPanel extends JPanel {

    private Screenlet parent = null ;
    
    protected ScreenletPanel( Screenlet parent ) {
        this.parent = parent ;
        super.setBackground( SConsole.BG_COLOR );
    }
    
    public Screenlet getScreenlet() {
        return this.parent ;
    }
    
}
