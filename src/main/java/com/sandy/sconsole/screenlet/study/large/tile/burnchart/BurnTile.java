package com.sandy.sconsole.screenlet.study.large.tile.burnchart;

import java.awt.BorderLayout ;
import java.awt.Color ;

import org.jfree.chart.ChartFactory ;
import org.jfree.chart.ChartPanel ;
import org.jfree.chart.JFreeChart ;
import org.jfree.chart.axis.ValueAxis ;
import org.jfree.chart.plot.XYPlot ;
import org.jfree.chart.title.LegendTitle ;
import org.jfree.data.time.TimeSeries ;
import org.jfree.data.time.TimeSeriesCollection ;

import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;

@SuppressWarnings( "serial" )
public class BurnTile extends AbstractScreenletTile {

    private TimeSeriesCollection seriesColl = null ;
    private JFreeChart           chart      = null ;
    private XYPlot               plot       = null ;
    private ChartPanel           chartPanel = null ;
    
    private TimeSeries historicDailyBurn = null ;
    private TimeSeries historicMovingAvg = null ;
    private TimeSeries projectedBurnForMilestone = null ;
    private TimeSeries projectedBurnForVelocity = null ;
    
    public BurnTile( ScreenletPanel mother ) {
        super( mother ) ;
        seriesColl = new TimeSeriesCollection() ;
        
        createTimeSeries() ;
        createChart() ;
        setUpUI() ;
    }
    
    private void createTimeSeries() {
        historicDailyBurn         = new TimeSeries( "Historic daily burn" ) ;
        historicMovingAvg         = new TimeSeries( "Historic moving avg" ) ;
        projectedBurnForMilestone = new TimeSeries( "Projected burn - milestone" ) ;
        projectedBurnForVelocity  = new TimeSeries( "Projected burn - velocity" ) ;
    }
    
    private void createChart() {
        chart = ChartFactory.createTimeSeriesChart( 
                      null, 
                      null, 
                      "#Problems", 
                      seriesColl ) ;
        chart.setBackgroundPaint( UIConstant.BG_COLOR ) ;
        
        configurePlot() ;
        configureAxes() ;
        configureLegends() ;
    }
    
    private void configurePlot() {
        
        plot = ( XYPlot )chart.getPlot() ;
        plot.setBackgroundPaint( UIConstant.BG_COLOR ) ;
        plot.setDomainGridlinePaint( Color.DARK_GRAY ) ;
        plot.setRangeGridlinePaint( Color.DARK_GRAY ) ;
        plot.setRangePannable( true ) ;
        plot.setDomainPannable( true ) ;
    }
    
    private void configureAxes() {
        
        ValueAxis xAxis = plot.getRangeAxis() ;
        ValueAxis yAxis = plot.getDomainAxis() ;
        
        xAxis.setLabelFont( UIConstant.CHART_AXIS_FONT ) ;
        yAxis.setLabelFont( UIConstant.CHART_AXIS_FONT ) ;
        
        xAxis.setTickLabelFont( UIConstant.CHART_AXIS_FONT ) ;
        yAxis.setTickLabelFont( UIConstant.CHART_AXIS_FONT ) ;
        
        xAxis.setTickLabelPaint( Color.LIGHT_GRAY.darker() ) ;
        yAxis.setTickLabelPaint( Color.LIGHT_GRAY.darker() ) ;
    }
    
    private void configureLegends() {
        
        LegendTitle legend = chart.getLegend() ;
        legend.setItemFont( UIConstant.CHART_LEGEND_FONT ) ;
        legend.setItemPaint( Color.LIGHT_GRAY.darker() ) ;
        legend.setBackgroundPaint( Color.BLACK );
    }

    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        chartPanel = new ChartPanel( chart ) ;
        add( chartPanel ) ;
    }
}