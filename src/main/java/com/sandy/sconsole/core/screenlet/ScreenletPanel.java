package com.sandy.sconsole.core.screenlet;

import static com.sandy.sconsole.core.frame.UIConstant.* ;

import java.awt.* ;

import javax.swing.* ;

import com.sandy.common.bus.* ;
import com.sandy.common.bus.Event ;
import com.sandy.sconsole.EventCatalog ;

@SuppressWarnings( "serial" )
public abstract class ScreenletPanel extends JPanel
    implements EventSubscriber {

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

    @Override
    public void handleEvent( Event event ) {
        
        switch( event.getEventType() ) {
            case EventCatalog.SCREENLET_MINIMIZED:
                screenletMinimized( parent ) ;
                break ;
            case EventCatalog.SCREENLET_MAXIMIZED:
                screenletMaximized( parent ) ;
                break ;
            case EventCatalog.SCREENLET_RUN_STATE_CHANGED:
                screenletRunStateChanged( parent ) ;
                break ;
        }
    }

    private void screenletMaximized( Screenlet screenlet ) {}

    private void screenletRunStateChanged( Screenlet screenlet ) {}

    private void screenletMinimized( Screenlet screenlet ) {}
}
