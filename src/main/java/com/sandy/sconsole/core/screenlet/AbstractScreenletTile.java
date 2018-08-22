package com.sandy.sconsole.core.screenlet;

import static com.sandy.sconsole.core.CoreEventID.RANGE_MAX ;
import static com.sandy.sconsole.core.CoreEventID.RANGE_MIN ;
import static com.sandy.sconsole.core.frame.UIConstant.BG_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.TILE_BORDER_COLOR ;
import static javax.swing.SwingConstants.CENTER ;

import java.awt.BorderLayout ;
import java.awt.Color ;

import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.border.LineBorder ;

import com.sandy.common.bus.Event ;
import com.sandy.common.bus.EventBus ;
import com.sandy.common.bus.EventSubscriber ;

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
        this.parent.getEventBus().addSubscriberForEventRange( this, false, RANGE_MIN, RANGE_MAX );
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
    }
}
