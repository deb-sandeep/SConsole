package com.sandy.sconsole ;

import java.io.File ;
import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;
import java.util.Timer ;
import java.util.TimerTask ;

import org.apache.log4j.Logger ;
import org.jfree.data.time.Day ;
import org.springframework.beans.BeansException ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.boot.SpringApplication ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;
import org.springframework.context.ApplicationContext ;
import org.springframework.context.ApplicationContextAware ;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry ;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer ;

import com.sandy.common.bus.EventBus ;
import com.sandy.sconsole.core.SConsoleConfig ;
import com.sandy.sconsole.core.frame.SConsoleFrame ;
import com.sandy.sconsole.core.screenlet.Screenlet ;
import com.sandy.sconsole.core.util.DayTickListener ;
import com.sandy.sconsole.core.util.SecondTickListener ;
import com.sandy.sconsole.dao.repository.SessionRepository ;
import com.sandy.sconsole.dao.repository.SessionRepository.SessionSummary ;
import com.sandy.sconsole.screenlet.dashboard.DashboardScreenlet ;
import com.sandy.sconsole.screenlet.fragmentation.FragmentationScreenlet ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;

@SpringBootApplication
public class SConsole 
    implements ApplicationContextAware, WebMvcConfigurer, DayTickListener {

    private static final Logger log = Logger.getLogger( SConsole.class ) ;
    
    public static File SCREENSHOT_DIR = new File( System.getProperty( "user.home" ),
                                                  "projects/workspace/sconsole/capture/screenshot" ) ;

    private static Timer              SEC_TIMER = new Timer( "SEC_TIMER", true ) ;
    private static SimpleDateFormat   SDF       = new SimpleDateFormat( "yyyy-MM-dd" ) ;
    private static ApplicationContext APP_CTX   = null ;
    private static SConsole           APP       = null ;
    
    private static List<SecondTickListener> secondListeners = new ArrayList<>() ;
    private static List<DayTickListener>    dayListeners    = new ArrayList<>() ;
    
    private static Object lock = new Object() ;
    
    public static EventBus GLOBAL_EVENT_BUS = new EventBus() ;
    
    static {
        SEC_TIMER.scheduleAtFixedRate( new TimerTask() {
            Calendar lastDate = null ;

            public void run() {
                
                Calendar now = Calendar.getInstance() ;
                
                synchronized( lock ) {
                    for( SecondTickListener task : secondListeners ) {
                        task.secondTicked( now ) ;
                    }
                }

                if( lastDate == null ) {
                    lastDate = now ;
                }
                else {
                    if( now.get( Calendar.DAY_OF_YEAR ) != 
                        lastDate.get( Calendar.DAY_OF_YEAR ) ) {

                        for( DayTickListener task : dayListeners ) {
                            task.dayTicked( now ) ;
                        }
                    }
                    lastDate = now ;
                }
            }
        }, new Date(), 1000 ) ;
    }

    public static void addSecTimerTask( SecondTickListener task ) {
        synchronized( lock ) {
            secondListeners.add( task ) ;
        }
    }
    
    public static void removeSecTimerTask( SecondTickListener task ) {
        synchronized( lock ) {
            secondListeners.remove( task ) ;
        }
    }

    public static void addDayTimerTask( DayTickListener task ) {
        dayListeners.add( task ) ;
    }

    public static ApplicationContext getAppContext() {
        return APP_CTX ;
    }

    public static SConsoleConfig getConfig() {
        return (SConsoleConfig) APP_CTX.getBean( "config" ) ;
    }

    public static SConsole getApp() {
        return APP ;
    }

    // ---------------- Instance methods start ---------------------------------

    private List<Screenlet> screenlets = new ArrayList<Screenlet>() ;
    private SConsoleFrame   frame      = null ;
    
    @Autowired
    private SessionRepository sessionRepo = null ;
    
    private Map<Day, List<SessionSummary>> l30SessionSummary = 
                                    new HashMap<Day, List<SessionSummary>>() ;
    public SConsole() {
        APP = this ;
        SConsole.addDayTimerTask( this ) ;
    }

    public void initialize() {
        registerScreenlets() ;
        this.frame = new SConsoleFrame() ;
        loadL30DaysSessionSummary() ;
    }
    
    @Override
    public void setApplicationContext( ApplicationContext applicationContext )
            throws BeansException {
        APP_CTX = applicationContext ;
    }

    private void registerScreenlets() {

        log.debug( "Registering screenlets" ) ;
        screenlets.add( new DashboardScreenlet().initialize() ) ;
        screenlets.add( new StudyScreenlet( "IIT - Physics" ).initialize() ) ;
        screenlets.add( new StudyScreenlet( "IIT - Chemistry" ).initialize() ) ;
        screenlets.add( new StudyScreenlet( "IIT - Maths" ).initialize() ) ;
        screenlets.add( new FragmentationScreenlet().initialize() ) ;
    }

    public List<Screenlet> getScreenlets() {
        return this.screenlets ;
    }
    
    public SConsoleFrame getFrame() {
        return this.frame ;
    }
    
    @Override
    public void addResourceHandlers( ResourceHandlerRegistry registry ) {
        registry.addResourceHandler("/screenshot/**")
                .addResourceLocations( "file:" + SCREENSHOT_DIR.getAbsolutePath() + "/",
                                       "classpath:/static/img/" ) ;
    }

    @Override
    public void dayTicked( Calendar instance ) {
        loadL30DaysSessionSummary() ;
    }
    
    private void loadL30DaysSessionSummary() {
        try {
            log.debug( "Loading last 30 days session summary" ) ;
            
            l30SessionSummary.clear() ;
            
            List<SessionSummary> summaryList = sessionRepo.getLast30DSessionSummary() ;
            for( SessionSummary ss : summaryList ) {
                Day day = new Day( SDF.parse( ss.getDate() ) ) ;
                List<SessionSummary> dayList = l30SessionSummary.get( day ) ;
                if( dayList == null ) {
                    dayList = new ArrayList<SessionSummary>() ;
                    l30SessionSummary.put( day, dayList ) ;
                }
                dayList.add( ss ) ;
            }
            
            log.debug( "Publishing L30 session info refresh event." ) ;
            GLOBAL_EVENT_BUS.publishEvent( EventCatalog.L30_SESSION_INFO_REFRESHED, 
                                           l30SessionSummary ) ;
        }
        catch( ParseException e ) {
            log.error( "Error loading last 30 days sessions", e ) ;
        }
    }
    
    // --------------------- Main method ---------------------------------------

    public static void main( String[] args ) {
        log.debug( "Starting Spring Booot..." ) ;
        SpringApplication.run( SConsole.class, args ) ;

        log.debug( "Starting SConsole.." ) ;
        SConsole app = SConsole.getAppContext().getBean( SConsole.class ) ;
        app.initialize() ;
    }

}
