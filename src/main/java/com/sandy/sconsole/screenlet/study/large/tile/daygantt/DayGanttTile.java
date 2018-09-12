package com.sandy.sconsole.screenlet.study.large.tile.daygantt;

import java.awt.BorderLayout ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;

/**
 * This tile shows a dynamic real time representation of the study periods.
 */
@SuppressWarnings( "serial" )
public class DayGanttTile extends AbstractScreenletTile {
    
    static final Logger log = Logger.getLogger( DayGanttTile.class ) ;
    
    public DayGanttTile( ScreenletPanel mother ) {
        super( mother ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        add( new DayGanttCanvas( false ), BorderLayout.CENTER ) ;
    }
}
