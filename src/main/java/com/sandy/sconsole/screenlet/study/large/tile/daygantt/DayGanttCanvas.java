package com.sandy.sconsole.screenlet.study.large.tile.daygantt;

import java.awt.* ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;

import javax.swing.JPanel ;

import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.bus.Event ;
import com.sandy.common.bus.EventSubscriber ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.util.DayTickListener ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.dao.repository.SessionRepository ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;

/**
 * This tile shows a dynamic real time representation of the study periods.
 */
@SuppressWarnings( "serial" )
public class DayGanttCanvas extends JPanel
    implements DayTickListener, EventSubscriber {
    
    static final Logger log = Logger.getLogger( DayGanttCanvas.class ) ;
    
    private static final int START_HR = 0 ;
    private static final int END_HR = 24 ;
    private static final int NUM_HRS = END_HR - START_HR ;
    private static final Insets INSET = new Insets( 5, 5, 40, 5 ) ;
    
    private Rectangle chartArea = new Rectangle() ;
    private float numPixelsPerHour = 0 ;
    private float numPixelsPerSecond = 0 ;
    
    private Date startOfDay = null ;
    private List<Session> todaySessions = null ;
    
    public DayGanttCanvas() {
        setBackground( UIConstant.BG_COLOR ) ;
        setForeground( Color.CYAN ) ;
        setDoubleBuffered( true ) ;
        setPreferredSize( new Dimension( 100, 100 ) );

        createStartOfDayDate() ;
        loadTodaySessions() ;
        
        SConsole.addDayTimerTask( this ) ;
        SConsole.GLOBAL_EVENT_BUS
                .addSubscriberForEventTypes( this, true, 
                                             SConsole.GlobalEvent.SESSION_SAVED ) ;
    }
    
    private void createStartOfDayDate() {
        Date date = new Date() ;
        date = DateUtils.setHours( date, START_HR ) ;
        date = DateUtils.setMinutes( date, 0 ) ;
        date = DateUtils.setSeconds( date, 0 ) ;
        date = DateUtils.setMilliseconds( date, 0 ) ;
        startOfDay = date ;
    }

    private void loadTodaySessions() {
        SessionRepository sessionRepo = null ;
        sessionRepo = SConsole.getAppContext().getBean( SessionRepository.class ) ;
        todaySessions = sessionRepo.getTodaySessions() ;
    }
    
    @Override
    public void paint( Graphics gOld ) {
        super.paint( gOld ) ;
        Graphics2D g = ( Graphics2D )gOld ;
        
        Dimension dim = getSize() ;
        initializeYardsticks( dim ) ;

        g.setColor( UIConstant.BG_COLOR ) ;
        g.fillRect( 0, 0, dim.width, dim.height ) ;
        
        paintTodaySessions( g ) ;
        paintSwimlane( g ) ;
    }
    
    private void initializeYardsticks( Dimension dim ) {
        chartArea.x = INSET.left ;
        chartArea.y = INSET.top ;
        chartArea.width = dim.width - INSET.left - INSET.right ;
        chartArea.height = dim.height - INSET.top - INSET.bottom ;
        
        numPixelsPerHour = chartArea.width / NUM_HRS ;
        numPixelsPerSecond = numPixelsPerHour/3600 ;
    }
    
    private void paintSwimlane( Graphics2D g ) {
        
        g.setFont( UIConstant.BASE_FONT.deriveFont( 20F ) ) ;
        g.setColor( Color.DARK_GRAY.darker() ) ;
        
        g.drawRect( chartArea.x, chartArea.y, chartArea.width, chartArea.height ) ;
        
        for( int i=0; i<NUM_HRS; i++ ) {
            int x  = (int)(chartArea.x + numPixelsPerHour*i) ;
            int y1 = chartArea.y ;
            int y2 = chartArea.y + chartArea.height ;
            
            g.setColor( Color.GRAY ) ;
            g.drawLine( x, y1, x, y2 ) ;
            g.setColor( Color.DARK_GRAY ) ;
            g.drawString( ""+(START_HR+i), x, y2+INSET.bottom-10 ) ;
        }
    }
    
    private void paintTodaySessions( Graphics2D g ) {
        for( Session s : todaySessions ) {
            paintSession( s, g ) ;
        }
    }
    
    private void paintSession( Session s, Graphics2D g ) {
        
        int secsSinceStartOfDay = 0 ;
        int sessionDuration = 0 ;
        
        long startTime = s.getStartTime().getTime() ;
        if( startTime < startOfDay.getTime() ) {
            // This implies that the session started late yesterday and the clock 
            // has rolled over.
            secsSinceStartOfDay = 0 ;
            sessionDuration = (int)((s.getEndTime().getTime() - startOfDay.getTime())/1000) ;
        }
        else {
            secsSinceStartOfDay = (int)((s.getStartTime().getTime() - startOfDay.getTime())/1000) ;
            sessionDuration = (int)((s.getEndTime().getTime() - s.getStartTime().getTime())/1000) ;
        }
        
        int x1 = chartArea.x + (int)(secsSinceStartOfDay * numPixelsPerSecond) ;
        int y1 = chartArea.y+1 ;
        int width = (int)(sessionDuration * numPixelsPerSecond) ;
        int height = chartArea.height-1 ;
        
        if( width == 0 ) {
            width = 1 ;
        }
        
        String subjectName = s.getTopic().getSubject().getName() ;
        g.setColor( StudyScreenlet.getSubjectColor( subjectName ) ) ;
        g.fillRect( x1, y1, width, height ) ;
    }

    @Override
    public void handleEvent( Event event ) {
        switch( event.getEventType() ) {
            case SConsole.GlobalEvent.SESSION_SAVED :
                Session s = ( Session )event.getValue() ;
                if( !todaySessions.contains( s ) ) {
                    todaySessions.add( s ) ;
                }
                repaint() ;
                break ;
        }
    }

    @Override
    public void dayTicked( Calendar instance ) {
        createStartOfDayDate() ;
        todaySessions.clear() ;
        repaint() ;
    }
}
