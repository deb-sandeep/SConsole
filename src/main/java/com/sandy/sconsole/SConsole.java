package com.sandy.sconsole ;

import java.io.File ;
import java.io.IOException ;
import java.sql.Timestamp ;
import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;
import java.util.Properties ;
import java.util.Timer ;
import java.util.TimerTask ;
import java.util.concurrent.LinkedBlockingQueue ;

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

import com.sandy.common.bus.Event ;
import com.sandy.common.bus.EventBus ;
import com.sandy.common.bus.EventSubscriber ;
import com.sandy.sconsole.analysis.PAAGenerator ;
import com.sandy.sconsole.api.remote.KeyEvent ;
import com.sandy.sconsole.api.remote.RemoteController ;
import com.sandy.sconsole.core.SConsoleConfig ;
import com.sandy.sconsole.core.frame.SConsoleFrame ;
import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.screenlet.Screenlet ;
import com.sandy.sconsole.core.util.DayTickListener ;
import com.sandy.sconsole.core.util.SecondTickListener ;
import com.sandy.sconsole.dao.repository.ProblemAttemptAnalysisRepository ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.dao.repository.SessionRepository ;
import com.sandy.sconsole.dao.repository.SessionRepository.SessionSummary ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;
import com.sandy.sconsole.screenlet.dashboard.DashboardScreenlet ;
import com.sandy.sconsole.screenlet.fragmentation.FragmentationScreenlet ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;

@SpringBootApplication
public class SConsole 
    implements ApplicationContextAware, WebMvcConfigurer, DayTickListener,
               EventSubscriber {

    private static final Logger log = Logger.getLogger( SConsole.class ) ;
    
    public static File SCREENSHOT_DIR = new File( System.getProperty( "user.home" ),
                                                  "projects/workspace/sconsole/capture/screenshot" ) ;

    public static File JEETEST_IMG_DIR = new File( System.getProperty( "user.home" ),
                                                  "projects/workspace/sconsole/jeetest/images" ) ;

    public static File MATHJAX_DIR = new File( "/var/www/lib-ext/MathJax" ) ;

    private static Timer              SEC_TIMER = new Timer( "SEC_TIMER", true ) ;
    private static SimpleDateFormat   SDF       = new SimpleDateFormat( "yyyy-MM-dd" ) ;
    private static ApplicationContext APP_CTX   = null ;
    private static SConsole           APP       = null ;
    
    private static List<SecondTickListener> secondListeners = new ArrayList<>() ;
    private static List<DayTickListener>    dayListeners    = new ArrayList<>() ;
    
    private static Object lock = new Object() ;
    
    public static EventBus GLOBAL_EVENT_BUS = new EventBus() ;
    
    // Convenience variables for ease of access.
    public static Day TODAY = new Day( new Date() ) ;
    public static long TODAY_FIRST_MIL = TODAY.getFirstMillisecond() ;
    public static long TODAY_LAST_MIL = TODAY.getLastMillisecond() ;
    public static Timestamp TODAY_FIRST_TS = new Timestamp( TODAY_FIRST_MIL ) ;
    public static Timestamp TODAY_LAST_TS  = new Timestamp( TODAY_LAST_MIL ) ;
    public static Properties BULK_ANS_LOOKUP = new Properties() ;
    
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

                        TODAY = new Day( new Date() ) ;
                        TODAY_FIRST_MIL = TODAY.getFirstMillisecond() ;
                        TODAY_LAST_MIL = TODAY.getLastMillisecond() ;
                        TODAY_FIRST_TS = new Timestamp( TODAY_FIRST_MIL ) ;
                        TODAY_LAST_TS  = new Timestamp( TODAY_LAST_MIL ) ;
                        
                        for( DayTickListener task : dayListeners ) {
                            
                            if( !task.isAsync() ) {
                                task.dayTicked( now ) ;
                            }
                            else {
                                new Thread(() -> {
                                    task.dayTicked( now ) ;
                                }).start() ;                 
                            }
                        }
                    }
                    lastDate = now ;
                }
            }
        }, new Date(), 1000 ) ;
        
        try {
            BULK_ANS_LOOKUP.load( SConsole.class.getResourceAsStream( "/ans_lookup.properties" ) ) ;
            SConsole.preProcessBulkAnswers() ;
        }
        catch( IOException e ) {
            log.error( "Could not load bulk answer lookup properties.", e ) ;
        }
    }
    
    public static void preProcessBulkAnswers() {
        
        for( Object key : BULK_ANS_LOOKUP.keySet() ) {
            String value = BULK_ANS_LOOKUP.getProperty( (String)key ) ;
            
            value = value.replace( "(", "" ) ;
            value = value.replace( ")", "" ) ;
            value = value.replace( "A", "1" ) ;
            value = value.replace( "B", "2" ) ;
            value = value.replace( "C", "3" ) ;
            value = value.replace( "D", "4" ) ;
            
            BULK_ANS_LOOKUP.put( key, value ) ;
        }
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
    
    private LinkedBlockingQueue<KeyEvent> asyncRemoteEventList = new LinkedBlockingQueue<>() ;
    
    @Autowired
    private SessionRepository sessionRepo = null ;
    
    @Autowired
    private ProblemAttemptAnalysisRepository paaRepo = null ;
    
    @Autowired
    private RemoteController remoteController = null ;
    
    @Autowired
    private TopicRepository topicRepo = null ;
    
    @Autowired
    private ProblemAttemptRepository paRepo = null ;
    
    private Map<Day, List<SessionSummary>> l30SessionSummary = 
                                    new HashMap<Day, List<SessionSummary>>() ;
    
    private PAAGenerator paaGenerator = null ;
    
    public SConsole() {
        APP = this ;
        SConsole.addDayTimerTask( this ) ;
        
        SConsole.GLOBAL_EVENT_BUS
                .addSubscriberForEventTypes( this, true, 
                                             EventCatalog.OFFLINE_SESSION_ADDED ) ;
        
        Thread t = new Thread( "Async keyevent postman" ) {
            public void run() {
                while( true ) {
                    try {
                        KeyEvent event = asyncRemoteEventList.take() ;
                        log.debug( "Processing async key event." + event );
                        remoteController.buttonPressed( event ) ;
                    }
                    catch( InterruptedException e ) {
                        log.debug( "Could not process async software key event", e ) ;
                    }
                }
            }
        } ;
        t.setDaemon( true ) ;
        t.start() ;
    }

    public void initialize() {
        registerScreenlets() ;
        this.frame = new SConsoleFrame() ;
        loadL30DaysSessionSummary() ;
        
        paaGenerator = new PAAGenerator( paaRepo, topicRepo, paRepo ) ;
        SConsole.addDayTimerTask( paaGenerator ) ;
        
        new Thread(() -> {
            paaGenerator.analyzeProblemAttempts() ;
        }).start() ;
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
        
        registry.addResourceHandler("/jeetest/images/**")
                .addResourceLocations( "file:" + JEETEST_IMG_DIR.getAbsolutePath() + "/" ) ;
        
        registry.addResourceHandler("/js/lib/MathJax/**")
                .addResourceLocations( "file:" + MATHJAX_DIR.getAbsolutePath() + "/" ) ;
    }

    @Override
    public void dayTicked( Calendar instance ) {
        loadL30DaysSessionSummary() ;
    }
    
    @Override
    public void handleEvent( Event event ) {
        if( event.getEventType() == EventCatalog.OFFLINE_SESSION_ADDED ) {
            loadL30DaysSessionSummary() ;
        }
    }
    
    private void loadL30DaysSessionSummary() {
        try {
            log.debug( "Loading last 30 days session summary" ) ;
            
            l30SessionSummary.clear() ;
            
            List<SessionSummary> summaryList = sessionRepo.getLast30DSessionSummary() ;
            for( SessionSummary ss : summaryList ) {
                Day day = new Day( SDF.parse( ss.getDate() ) ) ;
                addToDayList( day, ss ) ;
            }
            
            GLOBAL_EVENT_BUS.publishEvent( EventCatalog.L30_SESSION_INFO_REFRESHED, 
                                           l30SessionSummary ) ;
        }
        catch( ParseException e ) {
            log.error( "Error loading last 30 days sessions", e ) ;
        }
    }

    private void addToDayList( Day day, SessionSummary ss ) throws ParseException {
        
        List<SessionSummary> dayList = l30SessionSummary.get( day ) ;
        
        if( dayList == null ) {
            dayList = new ArrayList<SessionSummary>() ;
            l30SessionSummary.put( day, dayList ) ;
        }
        dayList.add( ss ) ;
        
        // If the session has spanned over to the next day, we also add this
        // session information to the next day.
        if( (ss.getStartTime().getTime() + ss.getDuration()*1000) > 
            day.getLastMillisecond() ) {
            addToDayList( (Day)day.next(), ss ) ;
        }
    }
    
    public void postSoftwareRemoteKeyEvent( Key key ) {
        
        KeyEvent event = new KeyEvent() ;
        event.setBtnType( key.getKeyType().toString() ) ;
        event.setBtnCode( key.getKeyCode() ) ; 
        
        asyncRemoteEventList.add( event ) ;
    }
    
    // --------------------- Main method ---------------------------------------

    public static void main( String[] args ) {
        log.debug( "Starting Spring Booot..." ) ;
        
        System.setProperty("java.awt.headless", "false");
        
        SpringApplication.run( SConsole.class, args ) ;

        log.debug( "Starting SConsole.." ) ;
        SConsole app = SConsole.getAppContext().getBean( SConsole.class ) ;
        app.initialize() ;
    }

}
