package com.sandy.sconsole.screenlet.study.large.tile;

import static com.sandy.sconsole.core.frame.UIConstant.* ;

import java.awt.* ;
import java.util.* ;

import javax.swing.* ;

import org.apache.log4j.* ;

import com.sandy.common.bus.Event ;
import com.sandy.sconsole.* ;
import com.sandy.sconsole.core.* ;
import com.sandy.sconsole.core.screenlet.* ;
import com.sandy.sconsole.core.util.* ;

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
            case CoreEventID.SCREENLET_MAXIMIZED:
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
