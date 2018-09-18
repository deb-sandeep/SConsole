package com.sandy.sconsole.screenlet.study.large;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Component ;
import java.awt.GridLayout ;

import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.SwingConstants ;
import javax.swing.border.EmptyBorder ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;
import com.sandy.sconsole.screenlet.study.large.tile.DateTile ;
import com.sandy.sconsole.screenlet.study.large.tile.DayBurnUpdateTile ;
import com.sandy.sconsole.screenlet.study.large.tile.L30DNATile ;
import com.sandy.sconsole.screenlet.study.large.tile.TimeTile ;
import com.sandy.sconsole.screenlet.study.large.tile.TitleTile ;
import com.sandy.sconsole.screenlet.study.large.tile.burnchart.BurnTile ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionInformation ;
import com.sandy.sconsole.screenlet.study.large.tile.daygantt.DayGanttTile ;
import com.sandy.sconsole.screenlet.study.large.tile.last30d.L30DaysNumQTile ;
import com.sandy.sconsole.screenlet.study.large.tile.last30d.L30DaysSubjectHrsTile ;
import com.sandy.sconsole.screenlet.study.large.tile.topicburnstat.TopicBurnStatTile ;

import info.clearthought.layout.TableLayout ;

//TODO: Implement DNA tile
@SuppressWarnings( "serial" )
public class StudyScreenletLargePanel extends ScreenletLargePanel {
    
    static final Logger log = Logger.getLogger( StudyScreenletLargePanel.class ) ;

    // PC stands for Panel Constraints
    private static final String TIME_PC            = "0,0,2,0" ;
    private static final String TITLE_PC           = "3,0,6,0" ;
    private static final String DATE_PC            = "7,0,9,0" ;
    
    private static final String GANTT_PC           = "0,1,9,1" ;
    
    private static final String TOPIC_BURN_PC      = "0,2,1,5" ;
    private static final String SESSION_CONTROL_PC = "2,2,7,5" ;
    private static final String DAY_STAT_PC        = "8,0,9,5" ;
    
    private static final String BURN_PC            = "0,6,4,9" ;
    private static final String THERMOMETER_PC     = "5,6,5,9" ;
    private static final String LAST_30D_PC        = "6,6,9,9" ;
    
    private TableLayout centerPanelLayout = null ;
    
    private TimeTile              timeTile            = null ;
    private TitleTile             titleTile           = null ;
    private DateTile              dateTile            = null ;
    private DayGanttTile          dayGanttTile        = null ;
    private TopicBurnStatTile     topicBurnStatTile   = null ;
    private SessionControlTile    sessionControlTile  = null ;
    private L30DNATile            l30DNATile          = null ;
    private BurnTile              burnTile            = null ;
    private DayBurnUpdateTile     thermometerTile     = null ;
    private L30DaysNumQTile       l30DaysNumQTile     = null ;
    private L30DaysSubjectHrsTile l30DaysHoursTile    = null ;
    
    private JLabel messageLabel = null ;
    
    public StudyScreenletLargePanel( StudyScreenlet screenlet ) {
        super( screenlet ) ;
        initializeMessageLabel() ;
        initializeTiles() ;
        setUpUI() ;
    }
    
    private void initializeTiles() {
        timeTile           = new TimeTile( this ) ;
        titleTile          = new TitleTile( this ) ;
        dateTile           = new DateTile( this ) ;
        dayGanttTile       = new DayGanttTile( this ) ;
        topicBurnStatTile  = new TopicBurnStatTile( this ) ;
        sessionControlTile = new SessionControlTile( this ) ;
        l30DNATile         = new L30DNATile( this ) ;
        burnTile           = new BurnTile( this ) ;
        thermometerTile    = new DayBurnUpdateTile( this ) ;
        l30DaysNumQTile    = new L30DaysNumQTile( this ) ;
        l30DaysHoursTile   = new L30DaysSubjectHrsTile( this ) ;
    }
    
    private void initializeMessageLabel() {
        messageLabel = new JLabel() ;
        messageLabel.setOpaque( true ) ;
        messageLabel.setBackground( Color.YELLOW ) ; 
        messageLabel.setFont( UIConstant.BASE_FONT.deriveFont( 20F ) ) ;
        messageLabel.setForeground( Color.BLACK ) ;
        messageLabel.setVerticalAlignment( SwingConstants.CENTER ) ;
        messageLabel.setHorizontalAlignment( SwingConstants.LEFT ) ;
        messageLabel.setBorder( new EmptyBorder( 5, 10, 5, 10 ) );
    }
    
    private void setUpUI() {
        
        JPanel panel = new JPanel() ;
        panel.setBackground( UIConstant.BG_COLOR ) ;
        
        setLayout( panel ) ;
        layoutHeaderRow( panel ) ;
        layoutDayGanttRow( panel ) ;
        layoutStatRow( panel ) ;
        layoutBurnAndStatRow( panel ) ;
        
        add( panel, BorderLayout.CENTER ) ;
    }
    
    private void setLayout( JPanel panel ) {
        double[] colSizes = new double[10] ;
        double[] rowSizes = new double[10] ;
        
        for( int i=0; i<10; i++ ) {
            colSizes[i] = rowSizes[i] = 0.1D ;
        }

        centerPanelLayout = new TableLayout( colSizes, rowSizes ) ;
        panel.setLayout( centerPanelLayout ) ;
    }
    
    private void layoutHeaderRow( JPanel panel ) {
        panel.add( timeTile, TIME_PC ) ;
        panel.add( titleTile, TITLE_PC ) ;
        panel.add( dateTile, DATE_PC ) ;
    }
    
    private void layoutDayGanttRow( JPanel panel ) {
        panel.add( dayGanttTile, GANTT_PC ) ;
    }
    
    private void layoutStatRow( JPanel panel ) {
        panel.add( topicBurnStatTile, TOPIC_BURN_PC ) ;
        panel.add( sessionControlTile, SESSION_CONTROL_PC ) ;
        panel.add( l30DNATile, DAY_STAT_PC ) ;
    }
    
    private void layoutBurnAndStatRow( JPanel panel ) {
        panel.add( burnTile, BURN_PC ) ;
        panel.add( thermometerTile, THERMOMETER_PC ) ;
        panel.add( getLast30DaysPanel(), LAST_30D_PC ) ;
    }
    
    private JPanel getLast30DaysPanel() {
        
        JPanel panel = new JPanel() ;
        panel.setLayout( new GridLayout( 2, 1 ) ) ;
        panel.add( l30DaysNumQTile ) ;
        panel.add( l30DaysHoursTile ) ;
        return panel ;
    }
    
    public SessionInformation getCurrentSessionInfo() {
        return sessionControlTile.getSessionInfo() ;
    }
    
    public void showMessage( String msg ) {
        BorderLayout borderLayout = ( BorderLayout )getLayout() ;
        Component comp = borderLayout.getLayoutComponent( BorderLayout.SOUTH ) ;
        if( comp == null ) {
            add( messageLabel, BorderLayout.SOUTH ) ;
            revalidate() ;
        }
        messageLabel.setText( msg ) ;
    }
    
    public void hideMessage() {
        BorderLayout borderLayout = ( BorderLayout )getLayout() ;
        Component comp = borderLayout.getLayoutComponent( BorderLayout.SOUTH ) ;
        if( comp != null ) {
            remove( messageLabel ) ;
            revalidate() ;
        }
    }
}
