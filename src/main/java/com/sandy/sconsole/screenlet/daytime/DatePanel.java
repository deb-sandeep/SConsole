package com.sandy.sconsole.screenlet.daytime;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.text.SimpleDateFormat ;
import java.util.Date ;
import java.util.Locale ;
import java.util.TimerTask ;

import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.SwingConstants ;

import com.sandy.sconsole.SConsole ;

@SuppressWarnings( "serial" )
public class DatePanel extends JPanel {
    
    private class DayTriggerHandler extends TimerTask {
        public void run() {
            dateLabel.setText( SDF.format( new Date() ) );
        }
    }
    
    private static Font LG_FONT = new Font( "Courier", Font.PLAIN, 75 ) ;
    private static Font SM_FONT = new Font( "Courier", Font.PLAIN, 35 ) ;
    
    private static SimpleDateFormat SDF  = new SimpleDateFormat( "d MMM", Locale.ENGLISH ) ;

    private DayTriggerHandler dayTimerHandler = new DayTriggerHandler() ;
    private JLabel            dateLabel       = new JLabel() ;

    public DatePanel( boolean large ) {
        super() ;
        SConsole.addDayTimerTask( dayTimerHandler ) ;
        setUpUI( large ) ;
    }
    
    private void setUpUI( boolean large ) {
        setLayout( new BorderLayout() ) ;
        setBackground( SConsole.BG_COLOR ) ;
        add( dateLabel, BorderLayout.CENTER ) ;
        
        dateLabel.setHorizontalAlignment( SwingConstants.LEFT ) ;
        dateLabel.setVerticalAlignment( SwingConstants.TOP ) ;
        dateLabel.setFont( large ? LG_FONT : SM_FONT ) ;
        dateLabel.setForeground( Color.GRAY ) ;
        dateLabel.setText( SDF.format( new Date() ) );
    }
}
