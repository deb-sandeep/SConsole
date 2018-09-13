package com.sandy.sconsole.screenlet.daytime;

import javax.swing.* ;

import com.sandy.sconsole.core.remote.* ;
import com.sandy.sconsole.core.screenlet.* ;

public class DayTimeScreenlet extends AbstractScreenlet {

    private DemuxKeyProcessor keyProcessor = null ;
    private TimePanel timePanel = null ;
    private DatePanel datePanel = null ;
    
    public DayTimeScreenlet() {
        super( "DayTime" ) ;
        keyProcessor = new DemuxKeyProcessor( "DayTime", new KeyListenerAdapter() ) ;
        keyProcessor.disableAllKeys() ;
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
        
        timePanel = new TimePanel( large ) ;
        datePanel = new DatePanel( large ) ;
        
        basePanel.setLayout( new BoxLayout( basePanel, BoxLayout.Y_AXIS ) ) ;
        basePanel.add( timePanel ) ;
        basePanel.add( datePanel ) ;
    }

    @Override
    public void isBeingMaximized() {
        super.isBeingMaximized() ;
        datePanel.refreshHistoricValues() ;
    }
}
