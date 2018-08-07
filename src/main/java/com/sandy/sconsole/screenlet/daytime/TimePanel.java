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
            timeLabel.setText( df.format( new Date() ) ) ;
        }
    }
    
    private static Font LG_FONT = new Font( "Courier", Font.PLAIN, 250 ) ;
    private static Font SM_FONT = new Font( "Courier", Font.PLAIN, 60 ) ;
    
    private static SimpleDateFormat SDF_LG = new SimpleDateFormat( "H:mm:ss" ) ;
    private static SimpleDateFormat SDF_SM = new SimpleDateFormat( "H:mm" ) ;

    private SecondTriggerHandler secTimerHandler = new SecondTriggerHandler() ;
    private JLabel timeLabel = new JLabel() ;
    private boolean largeDisplay = true ;
    private SimpleDateFormat df = null ;

    public TimePanel( boolean large ) {
        super() ;
        this.largeDisplay = large ;
        this.df = this.largeDisplay ? SDF_LG : SDF_SM ;
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
