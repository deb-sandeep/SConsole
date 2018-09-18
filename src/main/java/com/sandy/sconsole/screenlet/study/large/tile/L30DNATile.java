package com.sandy.sconsole.screenlet.study.large.tile;

import java.awt.Color ;
import java.awt.Graphics ;
import java.awt.Graphics2D ;
import java.awt.Insets ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;
import org.jfree.data.time.Day ;

import com.sandy.common.bus.Event ;
import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.dao.repository.SessionRepository.SessionSummary ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;

@SuppressWarnings( "serial" )
public class L30DNATile extends AbstractScreenletTile {
    
    private static final Logger log = Logger.getLogger( L30DNATile.class ) ;
    
    private static final Insets I = new Insets( 10, 10, 10, 10 ) ;
    private static final int DAY_START_HR = 6 ;
    private static final int NUM_HRS_IN_DAY = 24 - DAY_START_HR ;
            
    private Map<Day, List<SessionSummary>> ssMap = null ;
    private List<Day> dayList = new ArrayList<Day>() ;
    
    private float dayWidth = 0 ;
    private float hourHeight = 0 ;
    
    private int chartWidth = 0 ;
    private int chartHeight = 0 ;
    
    private String subjectName = null ;
    private Color subjectColor = null ;

    public L30DNATile( ScreenletPanel mother ) {
        super( mother ) ;
        this.subjectName = mother.getScreenlet().getDisplayName() ;
        this.subjectColor = StudyScreenlet.getSubjectColor( this.subjectName ) ;
        
        SConsole.GLOBAL_EVENT_BUS.addSubscriberForEventTypes( this, true, 
                                   EventCatalog.L30_SESSION_INFO_REFRESHED ) ;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public void handleEvent( Event event ) {
        
        super.handleEvent( event ) ;
        if( event.getEventType() == EventCatalog.L30_SESSION_INFO_REFRESHED ) {
            log.debug( "L30 refresh received." ) ;
            ssMap = ( Map<Day, List<SessionSummary>> )event.getValue() ;
            repaint() ;
        }
    }

    @Override
    public void paint( Graphics g1D ) {
        
        super.paint( g1D ) ;
        if( ssMap != null && !ssMap.isEmpty() ) {
            log.debug( "Repaining DNA tile" ) ;
            Graphics2D g = ( Graphics2D )g1D ;
            initMeasures() ;
            paintDaySummaries( g ) ;
            drawGrid( g ) ;
        }
    }
    
    private void paintDaySummaries( Graphics2D g ) {
        
        for( int dayNum=0; dayNum<dayList.size(); dayNum++ ) {
            Day day = dayList.get( dayNum ) ;
            List<SessionSummary> ssList = ssMap.get( day ) ;
            if( ssList != null ) {
                long firstMil =  day.getFirstMillisecond() ;
                firstMil += DAY_START_HR * 3600 * 1000 ;
                
                for( SessionSummary ss : ssList ) {
                    paintSessionSummary( dayNum, firstMil, ss, g ) ;
                }
            }
        }
    }
    
    private void paintSessionSummary( int dayNum, long firstMil, 
                                      SessionSummary ss, Graphics2D g ) {
        
        int x = (int)(I.left + dayNum*dayWidth) ;
        float startHr = (float)( ss.getStartTime().getTime() - firstMil )/(1000*60*60) ;
        
        int startY = I.top + (int)( startHr * hourHeight ) ;
        int height = (int)( ((float)ss.getDuration()/3600) * hourHeight ) ;
        
        if( ss.getSubject().equals( this.subjectName ) ) {
            g.setColor( this.subjectColor ) ;
        }
        else {
            g.setColor( Color.DARK_GRAY ) ;
        }
        g.fillRect( x, startY, (int)dayWidth, height ) ;
    }
    
    private void drawGrid( Graphics2D g ) {
        
        g.setColor( Color.DARK_GRAY.darker() ) ;
        g.drawRect( I.left, I.top, chartWidth, chartHeight ) ;
        
        for( int d=1; d<dayList.size(); d++ ) {
            int x = (int)( I.left + dayWidth*d ) ;
            g.drawLine( x, I.top, x, I.top+chartHeight );
        }
        
        for( int h=1; h<NUM_HRS_IN_DAY; h++ ) {
            int hr = DAY_START_HR + h ;
            int y = (int)( I.top + hourHeight*h ) ;
            
            if( hr%3 == 0 ) {
                g.setColor( Color.DARK_GRAY ) ;
            }
            else {
                g.setColor( Color.DARK_GRAY.darker() ) ;
            }
            g.drawLine( I.left, y, I.left+chartWidth, y ) ;
        }
    }
    
    private void initMeasures() {
        
        refreshDayList() ;
        
        chartWidth  = getWidth() - I.left - I.right ;
        chartHeight = getHeight() - I.top - I.bottom ;
        
        dayWidth = ((float)chartWidth) / dayList.size() ;
        hourHeight = ((float)chartHeight) / NUM_HRS_IN_DAY ;
    }
    
    private void refreshDayList() {
        
        Day day = new Day( new Date() ) ;
        dayList.clear() ;
        for( int i=0; i<30; i++ ) {
            day = (Day)day.previous() ;
            dayList.add( 0, day ) ;
        }
    }
}
