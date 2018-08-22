package com.sandy.sconsole ;

import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;
import java.util.Optional ;
import java.util.Timer ;
import java.util.TimerTask ;

import org.apache.log4j.Logger ;
import org.springframework.beans.BeansException ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.boot.SpringApplication ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;
import org.springframework.context.ApplicationContext ;
import org.springframework.context.ApplicationContextAware ;

import com.sandy.sconsole.core.SConsoleConfig ;
import com.sandy.sconsole.core.frame.SConsoleFrame ;
import com.sandy.sconsole.core.remote.RemoteKeyEventRouter ;
import com.sandy.sconsole.core.screenlet.Screenlet ;
import com.sandy.sconsole.core.util.DayTickListener ;
import com.sandy.sconsole.core.util.SecondTickListener ;
import com.sandy.sconsole.dao.entity.LastSession ;
import com.sandy.sconsole.dao.repository.LastSessionRepository ;
import com.sandy.sconsole.dao.repository.SessionRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;
import com.sandy.sconsole.dao.repository.master.SubjectRepository ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;
import com.sandy.sconsole.screenlet.daytime.DayTimeScreenlet ;
import com.sandy.sconsole.screenlet.dummy.DummyScreenlet ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;

@SpringBootApplication
public class SConsole implements ApplicationContextAware {

    private static final Logger log = Logger.getLogger( SConsole.class ) ;

    private static Timer              SEC_TIMER       = new Timer( "SEC_TIMER", true ) ;
    private static ApplicationContext APP_CTX         = null ;
    private static SConsole           APP             = null ;
    
    private static List<SecondTickListener> secondListeners = new ArrayList<>() ;
    private static List<DayTickListener>    dayListeners    = new ArrayList<>() ;

    static {
        SEC_TIMER.scheduleAtFixedRate( new TimerTask() {
            Calendar lastDate = null ;

            public void run() {
                
                Calendar now = Calendar.getInstance() ;
                
                for( SecondTickListener task : secondListeners ) {
                    task.secondTicked( now ) ;
                }

                if( lastDate == null ) {
                    lastDate = now ;
                }
                else {
                    if( now.get( Calendar.DAY_OF_YEAR ) != lastDate
                            .get( Calendar.DAY_OF_YEAR ) ) {

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
        secondListeners.add( task ) ;
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
    private RemoteKeyEventRouter keyEventRouter = null ;
    
    @Autowired
    private SessionRepository sessionRepository = null ;
    
    @Autowired
    private LastSessionRepository lastSessionRepository = null ;
    
    @Autowired
    private ProblemRepository problemRepository = null ;
    
    @Autowired
    private SubjectRepository subjectRepository = null ;
    
    @Autowired
    private TopicRepository topicRepository = null ;

    public SConsole() {
        APP = this ;
    }

    public void initialize() {
        registerScreenlets() ;
        this.frame = new SConsoleFrame( keyEventRouter ) ;
    }
    
    public void testJPA() {
        
        Optional<LastSession> opt = lastSessionRepository.findById( "IIT - Chemistry" ) ;
        LastSession session = ( opt.isPresent() ) ? opt.get() : null ;
        
        log.debug( session ) ;
    }
    
    @Override
    public void setApplicationContext( ApplicationContext applicationContext )
            throws BeansException {
        APP_CTX = applicationContext ;
    }

    private void registerScreenlets() {

        log.debug( "Registering screenlets" ) ;
        screenlets.add( new StudyScreenlet( "IIT - Physics" ).initialize() ) ;
        screenlets.add( new StudyScreenlet( "IIT - Chemistry" ).initialize() ) ;
        screenlets.add( new StudyScreenlet( "IIT - Maths" ).initialize() ) ;
        screenlets.add( new DayTimeScreenlet().initialize() ) ;
        screenlets.add( new DummyScreenlet( "Dummy" ).initialize() ) ;
    }

    public List<Screenlet> getScreenlets() {
        return this.screenlets ;
    }
    
    public ProblemRepository getProblemRepository() {
        return problemRepository ;
    }

    public SubjectRepository getSubjectRepository() {
        return subjectRepository;
    }

    public SessionRepository getSessionRepository() {
        return sessionRepository;
    }
    
    public TopicRepository getTopicRepository() {
        return topicRepository ;
    }
    
    public SConsoleFrame getFrame() {
        return this.frame ;
    }
    
    public RemoteKeyEventRouter getKeyRouter() {
        return this.keyEventRouter ;
    }
    
    // --------------------- Main method ---------------------------------------

    public static void main( String[] args ) {
        log.debug( "Starting Spring Booot..." ) ;
        SpringApplication.run( SConsole.class, args ) ;

        log.debug( "Starting SConsole.." ) ;
        SConsole app = SConsole.getAppContext().getBean( SConsole.class ) ;
        app.initialize() ;
        app.testJPA() ;
    }
}
