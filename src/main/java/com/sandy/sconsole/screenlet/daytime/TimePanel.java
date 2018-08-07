package com.sandy.sconsole.screenlet.daytime;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.text.SimpleDateFormat ;
import java.util.Date ;
import java.util.TimerTask ;

import javax.swing.JLabel ;
import javax.swing.SwingConstants ;

import com.sandy.sconsole.SConsole ;

@SuppressWarnings( "serial" )
public class TimePanel extends SConsoleBasePanel {
    
    private class SecondTriggerHandler extends TimerTask {
        @Override
        public void run() {
            timeLabel.setText( SDF.format( new Date() ) ) ;
        }
    }
    
    private static Font             FONT = new Font( "Courier", Font.PLAIN, 250 ) ;
    private static SimpleDateFormat SDF  = new SimpleDateFormat( "H:mm:ss" ) ;

    private SecondTriggerHandler secTimerHandler = new SecondTriggerHandler() ;
    private JLabel timeLabel = new JLabel() ;

    public TimePanel() {
        super() ;
        SConsole.addSecTimerTask( secTimerHandler ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        add( timeLabel, BorderLayout.CENTER ) ;
        
        timeLabel.setHorizontalAlignment( SwingConstants.CENTER ) ;
        timeLabel.setVerticalAlignment( SwingConstants.BOTTOM ) ;
        timeLabel.setFont( FONT ) ;
        timeLabel.setForeground( Color.GRAY ) ;
    }
}
