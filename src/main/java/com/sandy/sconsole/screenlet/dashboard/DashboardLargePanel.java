package com.sandy.sconsole.screenlet.dashboard;

import static com.sandy.sconsole.screenlet.study.StudyScreenlet.* ;

import com.sandy.sconsole.core.screenlet.Screenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.screenlet.dashboard.tile.DateTimeTile ;
import com.sandy.sconsole.screenlet.dashboard.tile.Last30DaysHoursTile ;
import com.sandy.sconsole.screenlet.study.large.tile.daygantt.DayGanttTile ;
import com.sandy.sconsole.screenlet.study.large.tile.last30d.Last30DaysSubjectHoursTile ;

import info.clearthought.layout.TableLayout ;

@SuppressWarnings( "serial" )
public class DashboardLargePanel extends ScreenletLargePanel {

    private static final int LAYOUT_ROWS = 12 ;
    private static final int LAYOUT_COLS = 12 ;
    
    private static final String DAY_GANTT_TILE_PC = "0,0,11,0" ;
    private static final String DATE_TIME_TILE_PC = "0,1,11,4" ;
    private static final String L30HRS_TILE_PC    = "0,10,11,11" ;
    
    private static final String PHY_L30_TILE_PC  = "0,8,3,9" ;
    private static final String CHEM_L30_TILE_PC = "4,8,7,9" ;
    private static final String MATHS_TILE_PC    = "8,8,11,9" ;
    
    private DateTimeTile dateTimeTile = null ;
    private Last30DaysHoursTile l30HrsTile = null ;
    private DayGanttTile dayGanttTile = null ;
    
    private Last30DaysSubjectHoursTile phyL30DHrsTile  = null ;
    private Last30DaysSubjectHoursTile chemL30DHrsTile = null ;
    private Last30DaysSubjectHoursTile mathL30DHrsTile = null ;
    
    public DashboardLargePanel( Screenlet parent ) {
        super( parent ) ;
        initTiles() ;
        setUpUI() ;
    }
    
    private void initTiles() {
        dateTimeTile = new DateTimeTile( this ) ;
        l30HrsTile = new Last30DaysHoursTile( this ) ;
        dayGanttTile = new DayGanttTile( this ) ;
        
        phyL30DHrsTile = new Last30DaysSubjectHoursTile( 
                                            this, 
                                            IIT_PHYSICS, 
                                            getSubjectColor( IIT_PHYSICS ),
                                            true ) ;
        chemL30DHrsTile = new Last30DaysSubjectHoursTile( 
                                            this, 
                                            IIT_CHEM, 
                                            getSubjectColor( IIT_CHEM ),
                                            true ) ;
        mathL30DHrsTile = new Last30DaysSubjectHoursTile( 
                                            this, 
                                            IIT_MATHS, 
                                            getSubjectColor( IIT_MATHS ),
                                            true ) ;
    }

    private void setUpUI() {
        setLayout() ;
        add( dayGanttTile,    DAY_GANTT_TILE_PC ) ;
        add( dateTimeTile,    DATE_TIME_TILE_PC ) ;
        add( l30HrsTile,      L30HRS_TILE_PC    ) ;
        add( phyL30DHrsTile,  PHY_L30_TILE_PC   ) ;
        add( chemL30DHrsTile, CHEM_L30_TILE_PC  ) ;
        add( mathL30DHrsTile, MATHS_TILE_PC     ) ;
    }

    private void setLayout() {
        
        float rowHeightPct = 1.0F/LAYOUT_ROWS ;
        float colHeightPct = 1.0F/LAYOUT_COLS ;
        
        TableLayout layout = new TableLayout() ;
        
        for( int i=0; i<LAYOUT_ROWS; i++ ) {
            layout.insertRow( 0, rowHeightPct ) ;
        }
        
        for( int i=0; i<LAYOUT_COLS; i++ ) {
            layout.insertColumn( i, colHeightPct ) ;
        }
        setLayout( layout ) ;
    }
}
