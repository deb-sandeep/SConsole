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
public class DayPanel extends JPanel {
    
    private class DayTriggerHandler extends TimerTask {
        public void run() {
            dayLabel.setText( SDF.format( new Date() ) );
        }
    }
    
    private static Font LG_FONT = new Font( "Courier", Font.PLAIN, 75 ) ;
    private static Font SM_FONT = new Font( "Courier", Font.PLAIN, 30 ) ;
    
    private static SimpleDateFormat SDF  = new SimpleDateFormat( "EEE, ", Locale.ENGLISH ) ;

    private DayTriggerHandler dayTimerHandler = new DayTriggerHandler() ;
    private JLabel            dayLabel        = new JLabel() ;

    public DayPanel( boolean large ) {
        super() ;
        SConsole.addDayTimerTask( dayTimerHandler ) ;
        setUpUI( large ) ;
    }
    
    private void setUpUI( boolean large ) {
        setLayout( new BorderLayout() ) ;
        setBackground( SConsole.BG_COLOR ) ;
        add( dayLabel, BorderLayout.CENTER ) ;
        
        dayLabel.setHorizontalAlignment( SwingConstants.RIGHT ) ;
        dayLabel.setVerticalAlignment( SwingConstants.TOP ) ;
        dayLabel.setFont( large ? LG_FONT : SM_FONT ) ;
        dayLabel.setForeground( Color.GRAY ) ;
        dayLabel.setText( SDF.format( new Date() ) );
    }
}
