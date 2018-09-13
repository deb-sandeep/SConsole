package com.sandy.sconsole.screenlet.study.large.tile.last30d;

import java.awt.Color ;
import java.util.Calendar ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.jfree.chart.ChartPanel ;

import com.sandy.common.bus.Event ;
import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.core.screenlet.Screenlet.RunState ;
import com.sandy.sconsole.core.util.DayValue ;
import com.sandy.sconsole.core.util.SecondTickListener ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.screenlet.study.large.tile.last30d.Last30DChart.Last30DaysDataProvider ;

@SuppressWarnings( "serial" )
public class Last30DaysSubjectHoursTile extends AbstractScreenletTile 
    implements Last30DaysDataProvider, SecondTickListener {

    static final Logger log = Logger.getLogger( Last30DaysSubjectHoursTile.class ) ;
    
    private Last30DChart chart = null ;
    private ChartPanel chartPanel = null ;
    private ProblemAttemptRepository paRepo = null ;
    
    private long lastRefresh = 0 ;
    
    public Last30DaysSubjectHoursTile( ScreenletPanel mother ) {
        super( mother ) ;
        
        paRepo = SConsole.getAppContext()
                         .getBean( ProblemAttemptRepository.class ) ;
        
        SConsole.addSecTimerTask( this ) ;
        
        mother.getScreenlet()
              .getEventBus()
              .addSubscriberForEventTypes( this, true, 
                                           EventCatalog.BURN_INFO_REFRESHED ) ;
  
        chart = new Last30DChart( "Hours", 
                                  Color.decode( "#A24C06" ),
                                  Color.YELLOW,
                                  this, false ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        chartPanel = new ChartPanel( chart.getJFreeChart() ) ;
        add( chartPanel ) ;
    }

    @Override
    public void handleEvent( Event event ) {
        super.handleEvent( event ) ;
        
        switch( event.getEventType() ) {
            case EventCatalog.BURN_INFO_REFRESHED:
                chart.refreshHistoricValues() ;
                break ;
        }
    }
    
    @Override
    public List<DayValue> getLast30DaysData() {
        lastRefresh = System.currentTimeMillis() ;
        return paRepo.getLast30DaysTimeSpent( getScreenlet().getDisplayName() ) ;
    }

    @Override
    public void secondTicked( Calendar calendar ) {
        if( getScreenlet().getRunState() == RunState.RUNNING ) {
            if( (System.currentTimeMillis() - lastRefresh) > (30*1000) ) {
                chart.refreshHistoricValues() ;
            }
        }
    }
}
