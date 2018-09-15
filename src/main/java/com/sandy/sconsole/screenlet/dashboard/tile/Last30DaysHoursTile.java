package com.sandy.sconsole.screenlet.dashboard.tile;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Dimension ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.jfree.chart.ChartPanel ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.core.util.DayValue ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.screenlet.study.large.tile.last30d.Last30DChart ;
import com.sandy.sconsole.screenlet.study.large.tile.last30d.Last30DChart.Last30DaysDataProvider ;

@SuppressWarnings( "serial" )
public class Last30DaysHoursTile extends AbstractScreenletTile 
    implements Last30DaysDataProvider {

    static final Logger log = Logger.getLogger( Last30DaysHoursTile.class ) ;
    
    private Last30DChart chart = null ;
    private ChartPanel chartPanel = null ;
    private ProblemAttemptRepository paRepo = null ;
    
    public Last30DaysHoursTile( ScreenletPanel mother ) {
        
        super( mother, true ) ;
        
        paRepo = SConsole.getAppContext()
                         .getBean( ProblemAttemptRepository.class ) ;
        
        chart = new Last30DChart( "Hours", 
                                  Color.DARK_GRAY,
                                  Color.GRAY.brighter(),
                                  this, true ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        chartPanel = new ChartPanel( chart.getJFreeChart() ) ;
        chartPanel.setPreferredSize( new Dimension( 1920, 300 ) ) ;
        
        setLayout( new BorderLayout() ) ;
        add( chartPanel ) ;
    }

    @Override
    public List<DayValue> getLast30DaysData() {
        return paRepo.getLast30DaysTimeSpent() ;
    }
    
    public void refreshHistoricValues() {
        chart.refreshHistoricValues() ;
    }
}
