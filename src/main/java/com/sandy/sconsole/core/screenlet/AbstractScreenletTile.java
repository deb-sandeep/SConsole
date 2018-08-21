package com.sandy.sconsole.core.screenlet;

import static com.sandy.sconsole.core.CoreEventID.* ;
import static com.sandy.sconsole.core.frame.UIConstant.* ;

import java.awt.BorderLayout ;
import java.awt.Color ;

import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.SwingConstants ;
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
            super.setBorder( new LineBorder( Color.DARK_GRAY.darker() ) ) ;
        }
        super.setLayout( new BorderLayout() ) ;
    }
    
    public EventBus getEventBus() {
        return this.parent.getEventBus() ;
    }
    
    public Screenlet getScreenlet() {
        return parent.getScreenlet() ;
    }
    
    protected JLabel getTemplateLabel() {
        
        JLabel label = new JLabel() ;
        label.setHorizontalAlignment( SwingConstants.CENTER ) ;
        label.setVerticalAlignment( SwingConstants.CENTER ) ;
        label.setBackground( BG_COLOR ) ;
        label.setForeground( Color.GRAY ) ;
        return label ;
    }

    @Override
    public void handleEvent( Event event ) {
    }
}
