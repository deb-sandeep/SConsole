package com.sandy.sconsole.ui.panels.daytime;

import javax.swing.BoxLayout ;
import javax.swing.JPanel ;

import com.sandy.sconsole.ui.panels.SConsoleBasePanel ;

@SuppressWarnings( "serial" )
public class DayTimePanel extends SConsoleBasePanel {
    
    private TimePanel timePanel = new TimePanel() ;
    private DayPanel  dayPanel  = new DayPanel() ;
    private DatePanel datePanel = new DatePanel() ;
    
    private JPanel calendarPanel = new SConsoleBasePanel() ;
    
    public DayTimePanel() {
        super() ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        calendarPanel.setLayout( new BoxLayout( calendarPanel, BoxLayout.X_AXIS ) ) ;
        calendarPanel.add( dayPanel ) ;
        calendarPanel.add( datePanel ) ;
        
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) ) ;
        add( timePanel ) ;
        add( calendarPanel ) ;
    }
}
