package com.sandy.sconsole.screenlet.daytime;

import static com.sandy.sconsole.core.frame.UIConstant.BG_COLOR ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.Locale ;

import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.SwingConstants ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.util.DayTickListener ;

@SuppressWarnings( "serial" )
public class DatePanel extends JPanel implements DayTickListener {
    
    private static Font LG_FONT = new Font( "Courier", Font.PLAIN, 80 ) ;
    private static Font SM_FONT = new Font( "Courier", Font.PLAIN, 30 ) ;
    
    private static SimpleDateFormat SDF  = new SimpleDateFormat( "EEE, d MMM", Locale.ENGLISH ) ;

    private JLabel dateLabel       = new JLabel() ;

    public DatePanel( boolean large ) {
        super() ;
        SConsole.addDayTimerTask( this ) ;
        setUpUI( large ) ;
    }
    
    public void dayTicked( Calendar time ) {
        dateLabel.setText( SDF.format( time.getTime() ) ) ;
    }
    
    private void setUpUI( boolean large ) {
        setLayout( new BorderLayout() ) ;
        setBackground( BG_COLOR ) ;
        add( dateLabel, BorderLayout.CENTER ) ;
        
        dateLabel.setHorizontalAlignment( SwingConstants.CENTER ) ;
        dateLabel.setVerticalAlignment( SwingConstants.TOP ) ;
        dateLabel.setFont( large ? LG_FONT : SM_FONT ) ;
        dateLabel.setForeground( Color.GRAY ) ;
        dateLabel.setText( SDF.format( new Date() ) );
    }
}
