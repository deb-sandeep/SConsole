package com.sandy.sconsole.screenlet.daytime;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.text.SimpleDateFormat ;
import java.util.Date ;
import java.util.TimerTask ;

import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.SwingConstants ;

import com.sandy.sconsole.SConsole ;

@SuppressWarnings( "serial" )
public class TimePanel extends JPanel {
    
    private class SecondTriggerHandler extends TimerTask {
        @Override
        public void run() {
            timeLabel.setText( SDF.format( new Date() ) ) ;
        }
    }
    
    private static Font LG_FONT = new Font( "Courier", Font.PLAIN, 250 ) ;
    private static Font SM_FONT = new Font( "Courier", Font.PLAIN, 70 ) ;
    
    private static SimpleDateFormat SDF     = new SimpleDateFormat( "H:mm:ss" ) ;

    private SecondTriggerHandler secTimerHandler = new SecondTriggerHandler() ;
    private JLabel timeLabel = new JLabel() ;

    public TimePanel( boolean large ) {
        super() ;
        SConsole.addSecTimerTask( secTimerHandler ) ;
        setUpUI( large ) ;
    }
    
    private void setUpUI( boolean large ) {
        setLayout( new BorderLayout() ) ;
        setBackground( SConsole.BG_COLOR ) ;
        add( timeLabel, BorderLayout.CENTER ) ;
        
        timeLabel.setHorizontalAlignment( SwingConstants.CENTER ) ;
        timeLabel.setVerticalAlignment( SwingConstants.BOTTOM ) ;
        timeLabel.setFont( large ? LG_FONT : SM_FONT );
        timeLabel.setForeground( Color.GRAY ) ;
    }
}
