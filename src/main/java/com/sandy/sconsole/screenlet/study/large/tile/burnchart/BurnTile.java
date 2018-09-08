package com.sandy.sconsole.screenlet.study.large.tile.burnchart;

import java.awt.BasicStroke ;
import java.awt.BorderLayout ;
import java.awt.Color ;
import java.util.Calendar ;
import java.util.Date ;

import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;
import org.jfree.chart.ChartFactory ;
import org.jfree.chart.ChartPanel ;
import org.jfree.chart.JFreeChart ;
import org.jfree.chart.axis.ValueAxis ;
import org.jfree.chart.plot.Marker ;
import org.jfree.chart.plot.ValueMarker ;
import org.jfree.chart.plot.XYPlot ;
import org.jfree.chart.renderer.AbstractRenderer ;
import org.jfree.data.time.Day ;
import org.jfree.data.time.TimeSeries ;
import org.jfree.data.time.TimeSeriesCollection ;

import com.sandy.common.bus.Event ;
import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.api.burn.HistoricBurnStat ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.dao.entity.master.Topic ;

@SuppressWarnings( "serial" )
public class BurnTile extends AbstractScreenletTile {

    private static final Logger log = Logger.getLogger( BurnTile.class ) ;
    
    
    private TimeSeriesCollection seriesColl = null ;
    private JFreeChart           chart      = null ;
    private XYPlot               plot       = null ;
    private ChartPanel           chartPanel = null ;
    private AbstractRenderer     renderer = null ;
    
    private TimeSeries cumulativeDailyBurn = null ;
    private TimeSeries baseBurnProjection = null ;
    private TimeSeries projectedBurnForVelocity = null ;
    
    public BurnTile( ScreenletPanel mother ) {
        super( mother ) ;
        seriesColl = new TimeSeriesCollection() ;
        
        mother.getScreenlet()
              .getEventBus()
              .addSubscriberForEventTypes( this, true, EventCatalog.TOPIC_CHANGED ) ;
        
        createChart() ;
        setUpUI() ;
    }
    
    private void createChart() {
        
        configureTimeSeries() ;
        configureChart() ;
        configurePlot() ;
        configureRenderer() ;
        configureAxes() ;
    }
    
    private void configureTimeSeries() {
        
        cumulativeDailyBurn       = new TimeSeries( "Historic daily burn" ) ;
        baseBurnProjection        = new TimeSeries( "Base burn projection" ) ;
        projectedBurnForVelocity  = new TimeSeries( "Projected burn - velocity" ) ;
    
        seriesColl.addSeries( cumulativeDailyBurn ) ;
        seriesColl.addSeries( baseBurnProjection ) ;
        seriesColl.addSeries( projectedBurnForVelocity ) ;
    }
    
    private void configureChart() {
        
        chart = ChartFactory.createTimeSeriesChart( 
                null, 
                null, 
                null, 
                seriesColl ) ;
        chart.setBackgroundPaint( UIConstant.BG_COLOR ) ;
        chart.removeLegend() ;
    }
    
    private void configurePlot() {
        
        plot = ( XYPlot )chart.getPlot() ;
        plot.setBackgroundPaint( UIConstant.BG_COLOR ) ;
        plot.setDomainGridlinePaint( Color.GRAY ) ;
        plot.setRangeGridlinePaint( Color.GRAY ) ;
        plot.setRangePannable( false ) ;
        plot.setDomainPannable( false ) ;
        plot.setDomainGridlinesVisible( true ) ;
        plot.setRangeGridlinesVisible( true ) ;
    }
    
    private void configureRenderer() {
        
        renderer = ( AbstractRenderer )plot.getRenderer() ;
        renderer.setSeriesPaint( 0, UIConstant.HISTORIC_BURN_COLOR ) ;
        renderer.setSeriesPaint( 1, UIConstant.BASE_BURN_COLOR ) ;
        renderer.setSeriesPaint( 2, UIConstant.PROJECTED_VELOCITY_BURN ) ;
        
        renderer.setSeriesStroke( 0, new BasicStroke(1.5f) );
        
        renderer.setSeriesStroke( 1, new BasicStroke(
                1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {2.0f, 6.0f}, 0.0f) );
        
        renderer.setSeriesStroke( 2, new BasicStroke(
                1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {2.0f, 6.0f}, 0.0f) );
    }
    
    private void configureAxes() {
        
        ValueAxis xAxis = plot.getRangeAxis() ;
        ValueAxis yAxis = plot.getDomainAxis() ;
        
        xAxis.setLabelFont( UIConstant.CHART_XAXIS_FONT ) ;
        yAxis.setLabelFont( UIConstant.CHART_YAXIS_FONT ) ;
        
        xAxis.setTickLabelFont( UIConstant.CHART_XAXIS_FONT ) ;
        yAxis.setTickLabelFont( UIConstant.CHART_YAXIS_FONT ) ;
        
        xAxis.setTickLabelPaint( Color.LIGHT_GRAY.darker() ) ;
        yAxis.setTickLabelPaint( Color.LIGHT_GRAY.darker() ) ;
    }
    
    private void setUpUI() {
        
        setLayout( new BorderLayout() ) ;
        chartPanel = new ChartPanel( chart ) ;
        chartPanel.setDoubleBuffered( true ) ;
        add( chartPanel ) ;
    }

    private void clearChartData() {
        cumulativeDailyBurn.clear() ;
        baseBurnProjection.clear() ;
        projectedBurnForVelocity.clear() ;
        
        plot.clearRangeMarkers() ;
        plot.clearDomainMarkers() ;
    }
    
    @Override
    public void handleEvent( Event event ) {
        super.handleEvent( event ) ;
        
        switch( event.getEventType() ) {
            case EventCatalog.TOPIC_CHANGED:
                try {
                    replotBurnChart( (Topic)event.getValue() ) ;
                }
                catch( Exception e ) {
                    log.debug( "Exception processing topic change.", e ) ;
                }
                break ;
        }
    }

    private synchronized void replotBurnChart( Topic topic ) 
        throws Exception {
        
        log.debug( "Replotting burn for topic - " + topic.getTopicName() ) ;
        BurnInfo bi = new BurnInfo( topic ) ;
        
        log.debug( "Burn info = " ) ;
        log.debug( bi ) ;
        
        clearChartData() ;
        plotMilestoneMarker( bi ) ;
        plotHistoricBurns( bi ) ;
        plotBaseMilestoneBurn( bi ) ;
        plotCurrentVelocityBurn( bi ) ;
    }
    
    private void plotMilestoneMarker( BurnInfo bi ) 
        throws Exception {
        
        Marker dateMarker = new ValueMarker( bi.getBurnCompletionDate().getTime() );
        dateMarker.setPaint( Color.GREEN.darker() ) ;
        plot.addDomainMarker( dateMarker ) ;
        
        Marker qMarker = new ValueMarker( bi.getNumProblems() );
        qMarker.setPaint( Color.GREEN.darker() ) ;
        plot.addRangeMarker( qMarker ) ;
    }
    
    private void plotHistoricBurns( BurnInfo bi ) 
        throws Exception {
        
        int numSolved = 0 ;
        for( HistoricBurnStat hb : bi.getHistoricBurns() ) {
            Date date = BurnInfo.DF.parse( hb.getDate() ) ;
            numSolved += hb.getNumQuestionsSolved() ;
            cumulativeDailyBurn.add( new Day( date ), numSolved, false ) ; 
        }
        
        cumulativeDailyBurn.fireSeriesChanged() ;
    }
    
    private void plotBaseMilestoneBurn( BurnInfo bi) {
        
        int numSolved = bi.getBaseMilestoneBurnRate() ;
        Date startDate = bi.getBurnStartDate() ;
        
        int counter = 0 ;
        Date date = startDate ;
        
        while( numSolved < bi.getNumProblems() ) {
            baseBurnProjection.add( new Day( date ), numSolved, false ) ;
            numSolved += bi.getBaseMilestoneBurnRate() ;
            date = DateUtils.addDays( date, 1 ) ;
            
            if( ++counter > 180 ) break ;
        }
        
        baseBurnProjection.fireSeriesChanged() ;
    }

    private void plotCurrentVelocityBurn( BurnInfo bi) {
        
        int numSolved = bi.getNumProblemsSolved() + bi.getCurrentBurnRate() ;
        Date date = DateUtils.truncate( new Date(), Calendar.HOUR ) ;
        
        int counter = 0 ;
        while( numSolved < bi.getNumProblems() ) {
            projectedBurnForVelocity.add( new Day( date ), numSolved, false ) ;
            numSolved += bi.getCurrentBurnRate() ;
            date = DateUtils.addDays( date, 1 ) ;
            
            if( ++counter > 180 ) break ;
        }
        
        projectedBurnForVelocity.fireSeriesChanged() ;
    }
}