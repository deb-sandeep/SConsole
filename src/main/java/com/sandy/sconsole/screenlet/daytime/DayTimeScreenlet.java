package com.sandy.sconsole.screenlet.daytime;

import javax.swing.BoxLayout ;
import javax.swing.JPanel ;

import com.sandy.sconsole.core.screenlet.AbstractScreenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.core.screenlet.ScreenletSmallPanel ;

public class DayTimeScreenlet extends AbstractScreenlet {

    public DayTimeScreenlet() {
        super( "DayTime" ) ;
    }
    
    @Override
    protected ScreenletSmallPanel createSmallPanel() {
        ScreenletSmallPanel panel = new ScreenletSmallPanel( this ) ;
        constructPanel( panel, false ) ;
        return panel ;
    }

    @Override
    protected ScreenletLargePanel createLargePanel() {
        ScreenletLargePanel panel = new ScreenletLargePanel( this ) ;
        constructPanel( panel, true ) ;
        return panel ;
    }
    
    private void constructPanel( JPanel basePanel, boolean large ) {
        
        TimePanel timePanel = new TimePanel( large ) ;
        DayPanel  dayPanel  = new DayPanel( large ) ;
        DatePanel datePanel = new DatePanel( large ) ;
        
        JPanel calPanel = new JPanel() ;
        calPanel.setLayout( new BoxLayout( calPanel, BoxLayout.X_AXIS ) ) ;
        calPanel.add( dayPanel ) ;
        calPanel.add( datePanel ) ;
        
        basePanel.setLayout( new BoxLayout( basePanel, BoxLayout.Y_AXIS ) ) ;
        basePanel.add( timePanel ) ;
        basePanel.add( calPanel ) ;
    }
}
