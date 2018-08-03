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
public class DatePanel extends SConsoleBasePanel {
    
    private class DayTriggerHandler extends TimerTask {
        public void run() {
            dateLabel.setText( SDF.format( new Date() ) );
        }
    }
    
    private static Font             FONT = new Font( "Courier", Font.PLAIN, 75 ) ;
    private static SimpleDateFormat SDF  = new SimpleDateFormat( "d MMM", Locale.ENGLISH ) ;

    private DayTriggerHandler dayTimerHandler = new DayTriggerHandler() ;
    private JLabel            dateLabel       = new JLabel() ;

    public DatePanel() {
        super() ;
        SConsole.addDayTimerTask( dayTimerHandler ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        add( dateLabel, BorderLayout.CENTER ) ;
        
        dateLabel.setHorizontalAlignment( SwingConstants.LEFT ) ;
        dateLabel.setVerticalAlignment( SwingConstants.TOP ) ;
        dateLabel.setFont( FONT ) ;
        dateLabel.setForeground( Color.GRAY ) ;
        dateLabel.setText( SDF.format( new Date() ) );
    }
}
