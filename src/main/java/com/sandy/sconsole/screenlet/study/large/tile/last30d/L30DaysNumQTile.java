package com.sandy.sconsole.screenlet.study.large.tile.last30d;

import java.awt.Color ;
import java.util.List ;

import org.jfree.chart.ChartPanel ;

import com.sandy.common.bus.Event ;
import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.core.util.DayValue ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.screenlet.study.TopicBurnInfo ;
import com.sandy.sconsole.screenlet.study.large.tile.last30d.Last30DChart.Last30DaysDataProvider ;

@SuppressWarnings( "serial" )
public class L30DaysNumQTile extends AbstractScreenletTile 
    implements Last30DaysDataProvider {

    private Last30DChart chart = null ;
    private ChartPanel chartPanel = null ;
    private ProblemAttemptRepository paRepo = null ;
    
    private Topic currentTopic = null ;
    
    public L30DaysNumQTile( ScreenletPanel mother ) {
        super( mother ) ;
        
        paRepo = SConsole.getAppContext()
                         .getBean( ProblemAttemptRepository.class ) ;
        
        mother.getScreenlet()
              .getEventBus()
              .addSubscriberForEventTypes( this, true, 
                                           EventCatalog.BURN_INFO_REFRESHED ) ;
        
        chart = new Last30DChart( "#Problems", 
                                  Color.decode( "#2034B8" ),
                                  Color.WHITE, 
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
                TopicBurnInfo bi = (TopicBurnInfo)event.getValue() ;
                currentTopic = bi.getTopic() ;
                chart.refreshHistoricValues() ;
                break ;
        }
    }
    
    @Override
    public List<DayValue> getLast30DaysData() {
        
        if( currentTopic == null ) {
            return null ;
        }
        return paRepo.getLast30DaysProblemsSolved( currentTopic.getId() ) ;
    }
}
