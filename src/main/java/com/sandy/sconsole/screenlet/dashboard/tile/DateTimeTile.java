package com.sandy.sconsole.screenlet.dashboard.tile;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.Locale ;

import javax.swing.BorderFactory ;
import javax.swing.JLabel ;
import javax.swing.SwingConstants ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.core.util.DayTickListener ;
import com.sandy.sconsole.core.util.SecondTickListener ;

@SuppressWarnings( "serial" )
public class DateTimeTile extends AbstractScreenletTile 
    implements DayTickListener, SecondTickListener {

    private static Font TIME_FONT = new Font( "Courier", Font.PLAIN, 200 ) ;
    private static Font DATE_FONT = new Font( "Courier", Font.PLAIN, 80 ) ;
    
    private static SimpleDateFormat TIME_SDF = new SimpleDateFormat( "HH:mm:ss", Locale.ENGLISH ) ; ;
    private static SimpleDateFormat DATE_SDF = new SimpleDateFormat( "EEE, d MMM", Locale.ENGLISH ) ;

    private JLabel timeLabel = new JLabel() ;
    private JLabel dateLabel = new JLabel() ;
    
    public DateTimeTile( ScreenletPanel mother ) {
        super( mother, false ) ;
        SConsole.addDayTimerTask( this ) ;
        SConsole.addSecTimerTask( this ) ;
        setUpUI() ;
    }

    private void setUpUI() {
        
        timeLabel.setHorizontalAlignment( SwingConstants.CENTER ) ;
        timeLabel.setVerticalAlignment( SwingConstants.BOTTOM ) ;
        timeLabel.setFont( TIME_FONT ) ;
        timeLabel.setForeground( Color.GRAY ) ;
        timeLabel.setText( TIME_SDF.format( new Date() ) ) ;

        dateLabel.setHorizontalAlignment( SwingConstants.CENTER ) ;
        dateLabel.setVerticalAlignment( SwingConstants.TOP ) ;
        dateLabel.setFont( DATE_FONT ) ;
        dateLabel.setForeground( Color.GRAY ) ;
        dateLabel.setText( DATE_SDF.format( new Date() ) );
        
        add( timeLabel, BorderLayout.CENTER ) ;
        add( dateLabel, BorderLayout.SOUTH ) ;
        setBorder( BorderFactory.createEmptyBorder( 40, 50, 50, 50 ) ) ;
    }

    @Override
    public void dayTicked( Calendar time ) {
        dateLabel.setText( DATE_SDF.format( time.getTime() ) ) ;
    }
    
    @Override
    public void secondTicked( Calendar time ) {
        if( getScreenlet().isVisible() ) {
            timeLabel.setText( TIME_SDF.format( time.getTime() ) ) ;
        }
    }
}
