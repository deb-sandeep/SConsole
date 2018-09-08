package com.sandy.sconsole.screenlet.study.large.tile.burnchart;

import java.awt.BasicStroke ;
import java.awt.BorderLayout ;
import java.awt.Color ;
import java.util.Calendar ;
import java.util.Date ;

import javax.swing.SwingUtilities ;

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
import com.sandy.sconsole.screenlet.study.ExerciseBurnInfo ;

@SuppressWarnings( "serial" )
public class BurnTile extends AbstractScreenletTile {

    private static final Logger log = Logger.getLogger( BurnTile.class ) ;
    
    private TimeSeriesCollection seriesColl = null ;
    private JFreeChart           chart      = null ;
    private XYPlot               plot       = null ;
    private ChartPanel           chartPanel = null ;
    private AbstractRenderer     renderer = null ;
    
    private TimeSeries historicBurn = null ;
    private TimeSeries baseBurnProjection = null ;
    private TimeSeries projectedBurnForVelocity = null ;
    
    public BurnTile( ScreenletPanel mother ) {
        super( mother ) ;
        seriesColl = new TimeSeriesCollection() ;
        
        mother.getScreenlet()
              .getEventBus()
              .addSubscriberForEventTypes( this, true, 
                                           EventCatalog.BURN_INFO_REFRESHED ) ;
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
        
        historicBurn       = new TimeSeries( "Historic daily burn" ) ;
        baseBurnProjection        = new TimeSeries( "Base burn projection" ) ;
        projectedBurnForVelocity  = new TimeSeries( "Projected burn - velocity" ) ;
    
        seriesColl.addSeries( historicBurn ) ;
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
        
        ValueAxis xAxis = plot.getDomainAxis() ;
        ValueAxis yAxis = plot.getRangeAxis() ;
        
        xAxis.setLabelFont( UIConstant.CHART_XAXIS_FONT ) ;
        xAxis.setTickLabelFont( UIConstant.CHART_XAXIS_FONT ) ;
        xAxis.setTickLabelPaint( Color.LIGHT_GRAY.darker() ) ;
        
        yAxis.setLabelFont( UIConstant.CHART_YAXIS_FONT ) ;
        yAxis.setTickLabelFont( UIConstant.CHART_YAXIS_FONT ) ;
        yAxis.setTickLabelPaint( Color.LIGHT_GRAY.darker() ) ;
    }
    
    private void setUpUI() {
        
        setLayout( new BorderLayout() ) ;
        chartPanel = new ChartPanel( chart ) ;
        chartPanel.setDoubleBuffered( true ) ;
        add( chartPanel ) ;
    }

    private void clearChartData() {
        
        historicBurn.clear() ;
        baseBurnProjection.clear() ;
        projectedBurnForVelocity.clear() ;
        
        plot.clearRangeMarkers() ;
        plot.clearDomainMarkers() ;
    }
    
    @Override
    public void handleEvent( Event event ) {
        super.handleEvent( event ) ;
        
        switch( event.getEventType() ) {
            case EventCatalog.BURN_INFO_REFRESHED:
                SwingUtilities.invokeLater( new Runnable() {
                    @Override
                    public void run() {
                        try {
                            replotBurnChart( (ExerciseBurnInfo)event.getValue() ) ;
                        }
                        catch( Exception e ) {
                            log.debug( "Exception processing topic change.", e ) ;
                        }
                    }
                } );
                break ;
        }
    }

    private synchronized void replotBurnChart( ExerciseBurnInfo bi ) 
        throws Exception {
        
        log.debug( "Plotting burn chart for " + bi.getTopic().getTopicName() ) ;
        
        log.debug( "Burn info = " ) ;
        log.debug( bi ) ;
        
        chart.setNotify( false ) ;
        
        clearChartData() ;
        
        plotMilestoneMarker( bi ) ;
        plotHistoricBurns( bi ) ;
        plotBaseMilestoneBurn( bi ) ;
        plotCurrentVelocityBurn( bi ) ;
        
        historicBurn.fireSeriesChanged() ;
        baseBurnProjection.fireSeriesChanged() ;
        projectedBurnForVelocity.fireSeriesChanged() ;
        
        chart.setNotify( true ) ;
    }
    
    private void plotMilestoneMarker( ExerciseBurnInfo bi ) 
        throws Exception {
        
        Marker dateMarker = new ValueMarker( bi.getBurnCompletionDate().getTime() );
        dateMarker.setPaint( Color.LIGHT_GRAY ) ;
        plot.addDomainMarker( dateMarker ) ;
        
        Marker qMarker = new ValueMarker( bi.getNumProblems() );
        qMarker.setPaint( Color.LIGHT_GRAY ) ;
        plot.addRangeMarker( qMarker ) ;
    }
    
    private void plotHistoricBurns( ExerciseBurnInfo bi ) 
        throws Exception {
        
        int numSolved = 0 ;
        for( HistoricBurnStat hb : bi.getHistoricBurns() ) {
            
            Date date = ExerciseBurnInfo.DF.parse( hb.getDate() ) ;
            numSolved += hb.getNumQuestionsSolved() ;
            historicBurn.add( new Day( date ), numSolved, false ) ; 
        }
    }
    
    private void plotBaseMilestoneBurn( ExerciseBurnInfo bi) {
        
        int numSolved = bi.getBaseMilestoneBurnRate() ;
        Date startDate = bi.getBurnStartDate() ;
        
        int counter = 0 ;
        Day day = new Day( startDate ) ;
        
        while( numSolved < bi.getNumProblems() ) {
            
            baseBurnProjection.add( day, numSolved, false ) ;
            numSolved += bi.getBaseMilestoneBurnRate() ;
            day = (Day)day.next() ;
            
            if( ++counter > 180 ) break ;
        }
    }

    private void plotCurrentVelocityBurn( ExerciseBurnInfo bi) {
        
        int numSolved = bi.getNumProblemsSolved() ;
        Day day = new Day( DateUtils.truncate( new Date(), Calendar.HOUR ) ) ;
        
        if( historicBurn.getDataItem( day ) == null ) {
            numSolved += bi.getCurrentBurnRate() ;
        }
        
        int counter = 0 ;
        while( numSolved < bi.getNumProblems() ) {
            
            projectedBurnForVelocity.add( day, numSolved, false ) ;
            numSolved += bi.getCurrentBurnRate() ;
            day = (Day)day.next() ;
            
            if( ++counter > 180 ) break ;
        }
    }
}