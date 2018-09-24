package com.sandy.sconsole.screenlet.study.large.tile.last30d;

import java.awt.Color ;
import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;

import javax.swing.SwingUtilities ;

import org.apache.log4j.Logger ;
import org.jfree.chart.ChartFactory ;
import org.jfree.chart.JFreeChart ;
import org.jfree.chart.axis.AxisLocation ;
import org.jfree.chart.axis.ValueAxis ;
import org.jfree.chart.plot.XYPlot ;
import org.jfree.chart.renderer.xy.StandardXYBarPainter ;
import org.jfree.chart.renderer.xy.XYBarRenderer ;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer ;
import org.jfree.data.time.* ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.util.DayTickListener ;
import com.sandy.sconsole.core.util.DayValue ;

public class Last30DChart 
    implements DayTickListener {
    
    static final Logger log = Logger.getLogger( Last30DChart.class ) ;
    
    public static final SimpleDateFormat DF = new SimpleDateFormat( "yyyy-MM-dd" ) ;
    
    public interface Last30DaysDataProvider {
        public List<DayValue> getLast30DaysData() ;
    }

    private JFreeChart chart = null ;
    private XYPlot plot = null ;
    
    private TimeSeries valueSeries = null ;
    private TimeSeries mavSeries = null ;
    
    private TimeSeriesCollection valDataset = null ;
    private TimeSeriesCollection mavDataset = null ;
    
    private String valueAxisLabel = null ;
    private Color valueColor = null ;
    private Color trendColor = null ;
    private boolean subtle = false ;
    
    private Last30DaysDataProvider dataProvider = null ;
    
    private Object lock = new Object() ;
    
    public Last30DChart( String valueAxisLabel,
                            Color valueColor,
                            Color trendColor,
                            Last30DaysDataProvider dataProvider,
                            boolean subtle ) {
        
        this.valueAxisLabel = valueAxisLabel ;
        this.valueColor = valueColor ;
        this.trendColor = trendColor ;
        this.dataProvider = dataProvider ;
        this.subtle = subtle ;
        
        SConsole.addDayTimerTask( this ) ;
        
        createDataSet() ;
        createJFreeChart() ;
        refreshHistoricValues() ;
    }
    
    private void createDataSet() {
        
        valueSeries = new TimeSeries( "Value" ) ;
        mavSeries = new TimeSeries( "MAV" ) ;
        
        mavSeries = MovingAverage.createMovingAverage( valueSeries,
                                                       "Moving average", 
                                                       7, 0 ) ;
        valDataset = new TimeSeriesCollection() ;
        valDataset.addSeries( valueSeries ) ;
        
        mavDataset = new TimeSeriesCollection() ;
        mavDataset.addSeries( mavSeries ) ;
    }
    
    private void createJFreeChart() {
        
        chart = ChartFactory.createTimeSeriesChart( 
                null,            // Chart title
                null,            // Domain axis label
                valueAxisLabel,  // Range axis label
                mavDataset,      // The dataset
                false,           // Legend required?
                false,           // Tooltips required?
                false            // Does chart generate URLs?
        ) ;
        
        plot = ( XYPlot )chart.getPlot() ;
        plot.setDataset( 1, valDataset ) ;
        
        configureChart() ;
        configurePlot() ;
    }
    
    private void configureChart() {
        
        chart.setBackgroundPaint( UIConstant.BG_COLOR ) ;
        chart.removeLegend() ;
    }
    
    private void configurePlot() {
        
        plot.setBackgroundPaint( UIConstant.BG_COLOR ) ;
        
        if( subtle ) {
            plot.setDomainGridlinesVisible( false ) ;
            plot.setRangeGridlinesVisible( false ) ;
        }
        
        configurePlotAxes( plot.getDomainAxis() ) ;
        configurePlotAxes( plot.getRangeAxis() ) ;
        
        plot.setRangeAxisLocation( AxisLocation.TOP_OR_RIGHT ) ;
        
        configureValSeriesRenderer() ;
        configureMAVSeriesRenderer() ;
    }
    
    private void configurePlotAxes( ValueAxis axis ) {
        
        Color color = subtle ? Color.DARK_GRAY : Color.LIGHT_GRAY ;
        
        axis.setAxisLinePaint( color ) ;
        axis.setTickLabelPaint( color ) ;
        axis.setTickLabelFont( UIConstant.BASE_FONT.deriveFont( 15F ) ) ;
        axis.setLabelPaint( color ) ;
        
        if( subtle ) {
            axis.setLabel( null ) ;
        }
    }
    
    private void configureValSeriesRenderer() {

        XYBarRenderer.setDefaultShadowsVisible( false ) ;
        XYBarRenderer renderer = new XYBarRenderer( 0.2F ) ;
        renderer.setDrawBarOutline( false ) ;
        renderer.setSeriesPaint( 0, valueColor ) ;
        renderer.setBarPainter( new StandardXYBarPainter() ) ;
        
        if( subtle ) {
            renderer.setMargin( 0.4F ) ;
        }
        
        plot.setRenderer( 1, renderer ) ;
    }
    
    private void configureMAVSeriesRenderer() {
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer() ;
        renderer.setSeriesPaint( 0, trendColor ) ;
        plot.setRenderer( 0, renderer ) ;
    }
    
    public JFreeChart getJFreeChart() {
        return this.chart ;
    }
    
    public void refreshHistoricValues() {
        
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                try {
                    refreshHistoricValuesAsync() ;
                }
                catch( ParseException e ) {
                    log.error( "Error populating initial data.", e ) ;
                }
            }
        } ) ;
    }
    
    private void refreshHistoricValuesAsync() 
        throws ParseException {
        
        List<DayValue> historicValues = null ;
        synchronized( lock ) {
            valueSeries.clear() ;
            mavSeries.clear() ;
            
            Day day = new Day( new Date() ) ;
            for( int i=0; i<30; i++ ) {
                valueSeries.add( day, 0.0 ) ;
                mavSeries.add( day, 0.0 ) ;
                day = ( Day )day.previous() ;
            }

            historicValues = dataProvider.getLast30DaysData() ;
            
            if( historicValues != null ) {
                for( DayValue dv : historicValues ) {
                    
                    Day date = new Day( DF.parse( dv.getDate() ) ) ;
                    valueSeries.addOrUpdate( date, dv.getValue() ) ;
                }
                
                mavSeries = MovingAverage.createMovingAverage( valueSeries,
                                                               "Moving average", 
                                                               7, 0 ) ;
                mavDataset.removeAllSeries() ;
                mavDataset.addSeries( mavSeries ) ;
            }
        }
    }
    
    public void addToTodayValue( float delta ) {
        
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                synchronized( lock ) {
                    
                    Day today = new Day( new Date() ) ;
                    TimeSeriesDataItem dataItem = valueSeries.getDataItem( today ) ;
                    dataItem.setValue( dataItem.getValue().floatValue() + delta ) ;
                    
                    valueSeries.fireSeriesChanged() ;
                    
                    mavSeries = MovingAverage.createMovingAverage( valueSeries,
                                                                    "Moving average", 
                                                                    7, 0 ) ;
                    mavDataset.removeAllSeries() ;
                    mavDataset.addSeries( mavSeries ) ;
                }
            }
        } ) ;
    }

    @Override
    public void dayTicked( Calendar instance ) {
        
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                valueSeries.clear() ;
                
                Day day = new Day( new Date() ) ;
                for( int i=0; i<30; i++ ) {
                    valueSeries.add( day, 0.0 ) ;
                    day = ( Day )day.previous() ;
                }
                
                try {
                    refreshHistoricValuesAsync() ;
                }
                catch( ParseException e ) {
                    log.error( "Error populating initial data.", e ) ;
                }
            }
        } ) ;
    }
}
