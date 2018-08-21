package com.sandy.sconsole.screenlet.daytime;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.text.SimpleDateFormat ;
import java.util.Calendar ;

import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.SwingConstants ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.util.SecondTickListener ;

@SuppressWarnings( "serial" )
public class TimePanel extends JPanel implements SecondTickListener {
    
    private static Font LG_FONT = new Font( "Courier", Font.PLAIN, 250 ) ;
    private static Font SM_FONT = new Font( "Courier", Font.PLAIN, 60 ) ;
    
    private static SimpleDateFormat SDF_LG = new SimpleDateFormat( "H:mm:ss" ) ;
    private static SimpleDateFormat SDF_SM = new SimpleDateFormat( "H:mm" ) ;

    private JLabel timeLabel = new JLabel() ;
    private boolean largeDisplay = true ;
    private SimpleDateFormat df = null ;

    public TimePanel( boolean large ) {
        super() ;
        this.largeDisplay = large ;
        this.df = this.largeDisplay ? SDF_LG : SDF_SM ;
        SConsole.addSecTimerTask( this ) ;
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

    @Override
    public void secondTicked( Calendar time ) {
        timeLabel.setText( df.format( time.getTime() ) ) ;
    }
}
