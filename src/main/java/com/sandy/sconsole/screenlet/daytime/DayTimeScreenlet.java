package com.sandy.sconsole.screenlet.daytime;

import javax.swing.* ;

import com.sandy.sconsole.core.remote.* ;
import com.sandy.sconsole.core.screenlet.* ;

public class DayTimeScreenlet extends AbstractScreenlet {

    private DemuxKeyProcessor keyProcessor = null ;
    
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
        
        TimePanel timePanel = new TimePanel( large ) ;
        DatePanel datePanel = new DatePanel( large ) ;
        
        basePanel.setLayout( new BoxLayout( basePanel, BoxLayout.Y_AXIS ) ) ;
        basePanel.add( timePanel ) ;
        basePanel.add( datePanel ) ;
    }
}
