package com.sandy.sconsole.screenlet.study.large.tile;

import java.awt.BasicStroke ;
import java.awt.Color ;

import org.apache.commons.lang.math.NumberUtils ;
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
public class ThermometerTile extends AbstractScreenletTile {
    
    private DefaultValueDataset valueDataset = null ;
    private ThermometerPlot plot = null ;
    private JFreeChart chart = null ;
    private ChartPanel chartPanel = null ;

    public ThermometerTile( ScreenletPanel mother ) {
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
        plot.setValuePaint( Color.WHITE ) ;
        plot.setValueFont( UIConstant.BASE_FONT.deriveFont( 40F ) ) ;
        
        plot.getRangeAxis().setTickLabelPaint( Color.LIGHT_GRAY ) ;
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
        
        int maxValue = 0 ;
        int amberThreshold = 0 ;
        int greenThreshold = 0 ;
        int curVal = 0 ;

        maxValue = NumberUtils.max( new int[]{
                bi.getRevisedMilestoneBurnRate(), 
                bi.getCurrentBurnRate(),
                bi.getNumProblemsSolvedToday()
        } ) + 2 ;
           
        if( bi.getCurrentBurnRate() < 
            bi.getRevisedMilestoneBurnRate() ) {
            amberThreshold = bi.getCurrentBurnRate() ;
        }
           
        curVal = bi.getNumProblemsSolvedToday() ;
        greenThreshold = bi.getRevisedMilestoneBurnRate() ;
        
        plot.setRange( 0, maxValue ) ;
     
        if( amberThreshold > 0 ) {
            plot.setSubrange( 0, 0, amberThreshold-1 ) ;
            plot.setSubrangePaint( 0, Color.RED.darker() ) ;
            
            plot.setSubrange( 1, amberThreshold-1, greenThreshold-1 ) ;
            plot.setSubrangePaint( 1, Color.ORANGE.darker() ) ;
            
            plot.setSubrange( 2, greenThreshold-1, maxValue ) ;
            plot.setSubrangePaint( 2, Color.GREEN.darker() ) ;
        }

        valueDataset.setValue( curVal ) ;
    }
}
