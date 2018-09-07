package com.sandy.sconsole.screenlet.study.large.tile;

import static com.sandy.sconsole.core.frame.UIConstant.BASE_FONT ;
import static com.sandy.sconsole.core.frame.UIConstant.DF_TIME_LG ;

import java.awt.BorderLayout ;
import java.awt.Font ;
import java.util.Calendar ;
import java.util.Date ;

import javax.swing.JLabel ;

import org.apache.log4j.Logger ;

import com.sandy.common.bus.Event ;
import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.core.util.SecondTickListener ;

@SuppressWarnings( "serial" )
public class TimeTile extends AbstractScreenletTile 
    implements SecondTickListener {

    static final Logger log = Logger.getLogger( SConsole.class ) ;
    
    private static Font FONT = BASE_FONT.deriveFont( 50F ) ;
    
    private JLabel label = null ;
    
    public TimeTile( ScreenletPanel mother ) {
        super( mother, false ) ;
        setUpUI() ;
        SConsole.addSecTimerTask( this ) ;
    }
    
    private void setUpUI() {
        label = getTemplateLabel() ;
        label.setFont( FONT ) ;
        refreshTime() ;
        add( label, BorderLayout.CENTER ) ;
    }

    @Override
    public void secondTicked( Calendar calendar ) {
        refreshTime( calendar ) ;
    }
    
    @Override
    public void handleEvent( Event event ) {
        switch( event.getEventType() ) {
            case EventCatalog.SCREENLET_MAXIMIZED:
                refreshTime() ;
                break ;
        }
    }
    
    private void refreshTime() {
        refreshTime( null ) ;
    }
    
    private void refreshTime( Calendar time ) {
        Date date = null ;
        if( time != null ) {
            date = time.getTime() ;
        }
        else {
            date = new Date() ;
        }
        label.setText( DF_TIME_LG.format( date ) ) ;
    }
}
