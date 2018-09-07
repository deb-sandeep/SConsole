package com.sandy.sconsole.core.screenlet;

import static com.sandy.sconsole.core.frame.UIConstant.* ;
import static javax.swing.SwingConstants.* ;

import java.awt.* ;

import javax.swing.* ;
import javax.swing.border.* ;

import com.sandy.common.bus.* ;
import com.sandy.common.bus.Event ;
import com.sandy.sconsole.EventCatalog ;

@SuppressWarnings( "serial" )
public abstract class AbstractScreenletTile extends JPanel
    implements EventSubscriber {

    protected ScreenletPanel parent = null ;
    
    protected AbstractScreenletTile( ScreenletPanel mother ) {
        this( mother, true ) ;
    }
    
    protected AbstractScreenletTile( ScreenletPanel mother,
                                     boolean drawBorder ) {
        this.parent = mother ;
        this.parent.getEventBus()
                   .addSubscriberForEventRange( this, false, 
                                                EventCatalog.CORE_EVENT_RANGE_MIN, 
                                                EventCatalog.CORE_EVENT_RANGE_MAX );
        
        super.setBackground( BG_COLOR ) ;
        
        if( drawBorder ) {
            super.setBorder( new LineBorder( TILE_BORDER_COLOR ) ) ;
        }
        super.setLayout( new BorderLayout() ) ;
    }
    
    public EventBus getEventBus() {
        return this.parent.getEventBus() ;
    }
    
    public AbstractScreenlet getScreenlet() {
        return (AbstractScreenlet)parent.getScreenlet() ;
    }
    
    protected JLabel getTemplateLabel() {
        
        JLabel label = new JLabel() ;
        label.setHorizontalAlignment( CENTER ) ;
        label.setVerticalAlignment( CENTER ) ;
        label.setBackground( BG_COLOR ) ;
        label.setForeground( Color.GRAY ) ;
        return label ;
    }

    @Override
    public void handleEvent( Event event ) {
        
        switch( event.getEventType() ) {
            
            case EventCatalog.SCREENLET_MINIMIZED:
                screenletMinimized() ;
                break ;
                
            case EventCatalog.SCREENLET_MAXIMIZED:
                screenletMaximized() ;
                break ;
                
            case EventCatalog.SCREENLET_RUN_STATE_CHANGED:
                Screenlet screenlet = this.parent.getScreenlet() ;
                screenletRunStateChanged( screenlet ) ;
                break ;
        }
    }
    
    protected void screenletMinimized() {}
    protected void screenletMaximized() {}
    protected void screenletRunStateChanged( Screenlet screenlet ) {}
}
