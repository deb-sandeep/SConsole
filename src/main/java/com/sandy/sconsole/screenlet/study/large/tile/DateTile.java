package com.sandy.sconsole.screenlet.study.large.tile;

import static com.sandy.sconsole.core.frame.UIConstant.BASE_FONT ;

import java.awt.BorderLayout ;
import java.awt.Font ;
import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.Locale ;

import javax.swing.JLabel ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.core.util.DayTickListener ;

@SuppressWarnings( "serial" )
public class DateTile extends AbstractScreenletTile 
    implements DayTickListener {

    private static SimpleDateFormat SDF  = new SimpleDateFormat( "EEE, d MMM", Locale.ENGLISH ) ;
    private static Font FONT = BASE_FONT.deriveFont( 40F ) ;
    
    private JLabel label = null ;
    
    public DateTile( ScreenletPanel mother ) {
        super( mother, false ) ;
        setUpUI() ;
        SConsole.addDayTimerTask( this ) ;
    }
    
    private void setUpUI() {
        label = getTemplateLabel() ;
        label.setFont( FONT ) ;
        label.setText( SDF.format( new Date() ) ) ;
        add( label, BorderLayout.CENTER ) ;
    }

    @Override
    public void dayTicked( Calendar calendar ) {
        label.setText( SDF.format( calendar.getTime() ) ) ;
    }
}
