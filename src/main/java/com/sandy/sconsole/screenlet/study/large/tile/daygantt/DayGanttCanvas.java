package com.sandy.sconsole.screenlet.study.large.tile.daygantt;

import java.awt.Color ;
import java.awt.Dimension ;
import java.awt.Font ;
import java.awt.FontMetrics ;
import java.awt.Graphics ;
import java.awt.Graphics2D ;
import java.awt.Insets ;
import java.awt.Rectangle ;
import java.awt.image.BufferedImage ;
import java.io.File ;
import java.io.IOException ;
import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;

import javax.imageio.ImageIO ;
import javax.swing.JPanel ;
import javax.swing.SwingUtilities ;

import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.bus.Event ;
import com.sandy.common.bus.EventSubscriber ;
import com.sandy.sconsole.EventCatalog ;
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
    static final SimpleDateFormat SDF = new SimpleDateFormat( "yyyy-MM-dd" ) ;
    
    private static final int START_HR = 0 ;
    private static final int END_HR = 24 ;
    private static final int NUM_HRS = END_HR - START_HR ;
    private static final Insets INSET = new Insets( 0, 0, 30, 0 ) ;
    private static final Font TOTAL_FONT = UIConstant.BASE_FONT.deriveFont( 40F ) ;
    
    private Rectangle chartArea = new Rectangle() ;
    private float numPixelsPerHour = 0 ;
    private float numPixelsPerSecond = 0 ;
    
    private Date startOfDay = null ;
    private List<Session> todaySessions = null ;
    
    private int totalTimeInSec = 0 ;
    private boolean takeEODScreenshot = false ;
    
    private long lastSessionHeartbeatPaintTime = 0 ;
    
    public DayGanttCanvas( boolean takeEODScreenshot ) {
        
        this.takeEODScreenshot = takeEODScreenshot ;
        
        setBackground( UIConstant.BG_COLOR ) ;
        setForeground( Color.CYAN ) ;
        setDoubleBuffered( true ) ;
        setPreferredSize( new Dimension( 100, 100 ) );

        createStartOfDayDate() ;
        loadTodaySessions() ;
        
        SConsole.addDayTimerTask( this ) ;
        SConsole.GLOBAL_EVENT_BUS
                .addSubscriberForEventTypes( this, true, 
                                             EventCatalog.SESSION_STARTED,
                                             EventCatalog.SESSION_PLAY_HEARTBEAT,
                                             EventCatalog.OFFLINE_SESSION_ADDED ) ;
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
        refreshTotalTime( g ) ;
    }
    
    private void initializeYardsticks( Dimension dim ) {
        chartArea.x = INSET.left ;
        chartArea.y = INSET.top ;
        chartArea.width = dim.width - INSET.left - INSET.right ;
        chartArea.height = dim.height - INSET.top - INSET.bottom ;
        
        numPixelsPerHour = chartArea.width / NUM_HRS ;
        numPixelsPerSecond = numPixelsPerHour/3600 ;
        
        totalTimeInSec = 0 ;
    }
    
    private void paintSwimlane( Graphics2D g ) {
        
        g.setFont( UIConstant.BASE_FONT.deriveFont( 20F ) ) ;
        g.setColor( Color.DARK_GRAY.darker() ) ;
        
        g.drawRect( chartArea.x, chartArea.y, chartArea.width, chartArea.height ) ;
        
        for( int i=0; i<NUM_HRS; i++ ) {
            int x  = (int)(chartArea.x + numPixelsPerHour*i) ;
            int y1 = chartArea.y ;
            int y2 = chartArea.y + chartArea.height ;
            
            g.setColor( Color.DARK_GRAY ) ;
            g.drawLine( x, y1, x, y2 ) ;
            g.drawString( ""+(START_HR+i), x+5, y2+INSET.bottom-6 ) ;
        }
    }
    
    private void paintTodaySessions( Graphics2D g ) {
        synchronized( this ) {
            for( Session s : todaySessions ) {
                paintSession( s, g ) ;
                totalTimeInSec += s.getTodayDuration() ;
            }
        }
    }
    
    private void paintSession( Session s, Graphics2D g ) {
        
        if( g == null ) {
            return ;
        }
        
        int sessionStartTimeMarkerInSecs = 0 ;
        int sessionDuration = 0 ;
        
        long startTime = s.getStartTime().getTime() ;
        if( startTime < startOfDay.getTime() ) {
            // This implies that the session started late yesterday and the clock 
            // has rolled over.
            sessionStartTimeMarkerInSecs = 0 ;
            sessionDuration = s.getTodayDuration() ;
        }
        else {
            sessionStartTimeMarkerInSecs = (int)((startTime - startOfDay.getTime())/1000) ;
            sessionDuration = s.getTodayDuration() ;
        }
        
        int x1 = chartArea.x + (int)(sessionStartTimeMarkerInSecs * numPixelsPerSecond) ;
        int y1 = chartArea.y+1 ;
        int width = (int)Math.ceil( sessionDuration * numPixelsPerSecond ) ;
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
            
            case EventCatalog.SESSION_STARTED :
            case EventCatalog.SESSION_ENDED :
                Session s1 = ( Session )event.getValue() ;
                synchronized( this ) {
                    if( !todaySessions.contains( s1 ) ) {
                        todaySessions.add( s1 ) ;
                    }
                }
                repaint() ;
                break ;
                
            case EventCatalog.SESSION_PLAY_HEARTBEAT :
                totalTimeInSec++ ;
                SwingUtilities.invokeLater( new Runnable() {
                    @Override
                    public void run() {
                        Session s2 = ( Session )event.getValue() ;
                        
                        if( getGraphics() != null ) {
                            long projection = (long)(1F/numPixelsPerSecond)*1000 + 1000 ;
                            if( lastSessionHeartbeatPaintTime + projection < System.currentTimeMillis() ) {
                                lastSessionHeartbeatPaintTime = System.currentTimeMillis() ;
                                paintSession( s2, (Graphics2D)getGraphics() ) ;
                            }
                        }
                        refreshTotalTime( (Graphics2D)getGraphics() ) ;
                    }
                } );
                break ;
                
            case EventCatalog.OFFLINE_SESSION_ADDED :
                Session s3 = ( Session )event.getValue() ;
                if( s3.executedToday() ) {
                    synchronized( this ) {
                        if( !todaySessions.contains( s3 ) ) {
                            todaySessions.add( s3 ) ;
                        }
                    }
                    repaint() ;
                }
                break ;
        }
    }
    
    private void refreshTotalTime( Graphics2D g ) {
        
        if( g == null )return ;
        
        int startSec = 3600*2 + 10*60 ;
        int duraction = 3600*3 - 20*60 ;
        
        int x1 = chartArea.x + (int)(startSec * numPixelsPerSecond) ;
        int y1 = chartArea.y + 1 ;
        int width = (int)(duraction * numPixelsPerSecond) ;
        int height = chartArea.height-1 ;
        
        g.setColor( UIConstant.BG_COLOR ) ;
        g.fillRect( x1, y1, width, height ) ;
        g.setColor( Color.GRAY ) ;
        g.setFont( TOTAL_FONT ) ;
        
        FontMetrics metrics = g.getFontMetrics( TOTAL_FONT ) ;
        int textHeight = metrics.getHeight() ;
        
        g.drawString( getElapsedTimeLabel( totalTimeInSec ), 
                      x1+10, 
                      y1+(height/2)+(textHeight/2)-10 ) ;
    }

    @Override
    public void dayTicked( Calendar instance ) {
        
        if( takeEODScreenshot ) {
            takeScreenshot() ;
        }
        
        createStartOfDayDate() ;
        synchronized( this ) {
            todaySessions.clear() ;
        }
        repaint() ;
    }

    private String getElapsedTimeLabel( long seconds ) {
        int secs    = (int)(seconds) % 60 ;
        int minutes = (int)((seconds / 60) % 60) ;
        int hours   = (int)(seconds / (60*60)) ;
        
        return String.format("%02d:%02d:%02d", hours, minutes, secs ) ;
    }

    public void takeScreenshot() {
        
        BufferedImage img = new BufferedImage( SConsole.getApp()
                                                       .getFrame()
                                                       .getWidth(), 
                                               100, 
                                               BufferedImage.TYPE_INT_RGB ) ;
        paint( img.getGraphics() ) ;
        
        File file = getImgSaveFile() ;
        try {
            ImageIO.write( img, "png", file ) ;
        }
        catch( IOException e ) {
            log.error( "Unable to save image - " + 
                       file.getAbsolutePath(), e ) ;
        }
    }
    
    private File getImgSaveFile() {
        
        StringBuffer fileName = new StringBuffer() ;
        fileName.append( SDF.format( new Date() ) )
                .append( ".png" ) ;
        
        File dir = new File( System.getProperty( "user.home" ),
                             "projects/workspace/sconsole/capture/daygantt" ) ;
        File file = new File( dir, fileName.toString() ) ;
        return file ;
    }
}
