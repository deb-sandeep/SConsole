package com.sandy.sconsole.ui.panels.daytime;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.text.SimpleDateFormat ;
import java.util.Date ;
import java.util.Locale ;
import java.util.TimerTask ;

import javax.swing.JLabel ;
import javax.swing.SwingConstants ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.ui.panels.SConsoleBasePanel ;

@SuppressWarnings( "serial" )
public class DayPanel extends SConsoleBasePanel {
    
    private class DayTriggerHandler extends TimerTask {
        public void run() {
            dayLabel.setText( SDF.format( new Date() ) );
        }
    }
    
    private static Font             FONT = new Font( "Courier", Font.PLAIN, 75 ) ;
    private static SimpleDateFormat SDF  = new SimpleDateFormat( "EEE, ", Locale.ENGLISH ) ;

    private DayTriggerHandler dayTimerHandler = new DayTriggerHandler() ;
    private JLabel            dayLabel        = new JLabel() ;

    public DayPanel() {
        super() ;
        SConsole.addDayTimerTask( dayTimerHandler ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        add( dayLabel, BorderLayout.CENTER ) ;
        
        dayLabel.setHorizontalAlignment( SwingConstants.RIGHT ) ;
        dayLabel.setVerticalAlignment( SwingConstants.TOP ) ;
        dayLabel.setFont( FONT ) ;
        dayLabel.setForeground( Color.GRAY ) ;
        dayLabel.setText( SDF.format( new Date() ) );
    }
}
