package com.sandy.sconsole.screenlet.daytime;

import static com.sandy.sconsole.core.frame.UIConstant.BG_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.DF_TIME_LG ;
import static com.sandy.sconsole.core.frame.UIConstant.DF_TIME_SM ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;

import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.SwingConstants ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.util.SecondTickListener ;

@SuppressWarnings( "serial" )
public class TimePanel extends JPanel implements SecondTickListener {
    
    private static Font LG_FONT = new Font( "Courier", Font.PLAIN, 250 ) ;
    private static Font SM_FONT = new Font( "Courier", Font.PLAIN, 60 ) ;
    
    private JLabel timeLabel = new JLabel() ;
    private boolean largeDisplay = true ;
    private SimpleDateFormat df = null ;

    public TimePanel( boolean large ) {
        super() ;
        this.largeDisplay = large ;
        this.df = this.largeDisplay ? DF_TIME_LG : DF_TIME_SM ;
        SConsole.addSecTimerTask( this ) ;
        setUpUI( large ) ;
    }
    
    private void setUpUI( boolean large ) {
        setLayout( new BorderLayout() ) ;
        setBackground( BG_COLOR ) ;
        add( timeLabel, BorderLayout.CENTER ) ;
        
        timeLabel.setHorizontalAlignment( SwingConstants.CENTER ) ;
        timeLabel.setVerticalAlignment( SwingConstants.BOTTOM ) ;
        timeLabel.setFont( large ? LG_FONT : SM_FONT );
        timeLabel.setForeground( Color.GRAY ) ;
        timeLabel.setText( df.format( new Date() ) ) ;
    }

    @Override
    public void secondTicked( Calendar time ) {
        timeLabel.setText( df.format( time.getTime() ) ) ;
    }
}
