package com.sandy.sconsole.core.screenlet;

import javax.swing.JPanel ;

@SuppressWarnings( "serial" )
public abstract class ScreenletPanel extends JPanel {

    private Screenlet parent = null ;
    
    protected ScreenletPanel( Screenlet parent ) {
        this.parent = parent ;
    }
    
    public Screenlet getScreenlet() {
        return this.parent ;
    }
    
}
