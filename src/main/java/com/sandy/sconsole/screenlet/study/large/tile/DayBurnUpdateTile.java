package com.sandy.sconsole.screenlet.study.large.tile;

import java.awt.BasicStroke ;
import java.awt.Color ;

import org.jfree.chart.ChartPanel ;
import org.jfree.chart.JFreeChart ;
import org.jfree.chart.plot.ThermometerPlot ;
import org.jfree.data.general.DefaultValueDataset ;
import org.jfree.ui.RectangleInsets ;

import com.sandy.common.bus.Event ;
import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.screenlet.study.TopicBurnInfo ;

@SuppressWarnings( "serial" )
public class DayBurnUpdateTile extends AbstractScreenletTile {
    
    private DefaultValueDataset valueDataset = null ;
    private ThermometerPlot plot = null ;
    private JFreeChart chart = null ;
    private ChartPanel chartPanel = null ;

    public DayBurnUpdateTile( ScreenletPanel mother ) {
        super( mother ) ;
        mother.getScreenlet()
              .getEventBus()
              .addSubscriberForEventTypes( this, true, 
                                           EventCatalog.BURN_INFO_REFRESHED ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        valueDataset = new DefaultValueDataset( 0 ) ;
        plot         = new ThermometerPlot( valueDataset ) ;
        chart        = new JFreeChart( plot ) ;
        chartPanel   = new ChartPanel( chart ) ;
        
        configureChart() ;
        configurePlot() ;
        
        add( chartPanel ) ;
    }
    
    private void configureChart() {

        chart.setBackgroundPaint( UIConstant.BG_COLOR ) ;
        chart.removeLegend() ;
    }
    
    private void configurePlot() {

        plot.setGap( 7 ) ;
        plot.setInsets( new RectangleInsets( 0, 0, 0, 0 ) ) ;
        plot.setPadding(new RectangleInsets( 15.0, 0.0, 15.0, 0.0 ) ) ;
        plot.setBackgroundPaint( UIConstant.BG_COLOR ) ;
        plot.setThermometerStroke( new BasicStroke( 1.0f ) ) ;
        plot.setThermometerPaint( Color.GRAY ) ;
        plot.setMercuryPaint( Color.DARK_GRAY ) ;
        plot.setUnits( ThermometerPlot.UNITS_NONE ) ;
        
        plot.setBulbRadius( 50 ) ;
        plot.setColumnRadius( 30 ) ;
        
        plot.setValueLocation( ThermometerPlot.BULB );
        plot.setValuePaint( Color.BLACK ) ;
        plot.setValueFont( UIConstant.BASE_FONT.deriveFont( 40F ) ) ;
        
        plot.getRangeAxis().setTickLabelPaint( Color.GRAY.brighter() ) ;
        plot.getRangeAxis().setTickLabelFont( UIConstant.BASE_FONT.deriveFont( 20F ) ) ;
    }

    @Override
    public void handleEvent( Event event ) {
        super.handleEvent( event ) ;
        
        switch( event.getEventType() ) {
            case EventCatalog.BURN_INFO_REFRESHED:
                TopicBurnInfo bi = (TopicBurnInfo)event.getValue() ;
                refreshPlot( bi ) ;
                break ;
        }
    }
    
    private void refreshPlot( TopicBurnInfo bi ) {
        
        int numSolved = bi.getNumProblemsSolvedToday() ;
        int upperRange = Integer.max( bi.getCurrentBurnRate(), 
                                      bi.getRevisedMilestoneBurnRate() ) + 5 ;
        int range1UL = 0 ;
        int range2UL = 0 ;
        
        if( bi.getCurrentBurnRate() < bi.getRevisedMilestoneBurnRate() ) {
            range1UL = bi.getCurrentBurnRate() ;
            range2UL = bi.getRevisedMilestoneBurnRate() ;
        }
        else {
            range1UL = bi.getRevisedMilestoneBurnRate() ;
            range2UL = bi.getCurrentBurnRate() ;
        }

        
        if( numSolved > upperRange ) {
            upperRange = bi.getNumProblemsSolvedToday() + 2 ;
        }
        
        plot.setRange( 0, upperRange ) ;
        
        plot.setSubrange( 0, 0, range1UL ) ;
        plot.setSubrangePaint( 0, Color.RED.darker() ) ;
        
        plot.setSubrange( 1, range1UL, range2UL ) ;
        plot.setSubrangePaint( 1, Color.ORANGE.darker() ) ;
        
        plot.setSubrange( 2, range2UL, upperRange ) ;
        plot.setSubrangePaint( 2, Color.GREEN.darker() ) ;
        
        valueDataset.setValue( bi.getNumProblemsSolvedToday() ) ;
    }
}
