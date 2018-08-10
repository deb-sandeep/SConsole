package com.sandy.sconsole ;

import java.awt.Color ;
import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.HashSet ;
import java.util.List ;
import java.util.Set ;
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
import com.sandy.sconsole.core.frame.RemoteKeyEventRouter ;
import com.sandy.sconsole.core.frame.SConsoleFrame ;
import com.sandy.sconsole.core.screenlet.Screenlet ;
import com.sandy.sconsole.jpa.User ;
import com.sandy.sconsole.jpa.UserRepository ;
import com.sandy.sconsole.screenlet.daytime.DayTimeScreenlet ;

@SpringBootApplication
public class SConsole implements ApplicationContextAware {

    private static final Logger       log             = Logger
            .getLogger( SConsole.class ) ;

    public static Color               BG_COLOR        = Color.BLACK ;

    private static Timer              SEC_TIMER       = new Timer( "SEC_TIMER", true ) ;
    private static Set<TimerTask>     DAY_TIMER_TASKS = new HashSet<TimerTask>() ;
    private static ApplicationContext APP_CTX         = null ;
    private static SConsole           APP             = null ;

    static {
        addSecTimerTask( new TimerTask() {
            Calendar lastDate = null ;

            public void run() {
                if( lastDate == null ) {
                    lastDate = Calendar.getInstance() ;
                }
                else {
                    Calendar now = Calendar.getInstance() ;
                    if( now.get( Calendar.DAY_OF_YEAR ) != lastDate
                            .get( Calendar.DAY_OF_YEAR ) ) {

                        for( TimerTask task : DAY_TIMER_TASKS ) {
                            task.run() ;
                        }
                    }
                    lastDate = now ;
                }
            }
        } ) ;
    }

    public static void addSecTimerTask( TimerTask task ) {
        SEC_TIMER.scheduleAtFixedRate( task, new Date(), 1000 ) ;
    }

    public static void addDayTimerTask( TimerTask task ) {
        DAY_TIMER_TASKS.add( task ) ;
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
    private UserRepository userRepository = null ;

    public SConsole() {
        APP = this ;
    }

    public void initialize() {
        registerScreenlets() ;
        this.frame = new SConsoleFrame() ;
        this.frame.toggleScreenletPanelVisibility() ;
        
        keyEventRouter.registerFrame( frame ) ;
    }
    
    public void insertRecords() {
        
        User n = new User();
        n.setName( "Sandeep" ) ;
        n.setEmail( "deb.sandeep@gmail.com" ) ;
        userRepository.save(n);        
    }

    @Override
    public void setApplicationContext( ApplicationContext applicationContext )
            throws BeansException {
        APP_CTX = applicationContext ;
    }

    private void registerScreenlets() {

        log.debug( "Registering screenlets" ) ;
        screenlets.add( new DayTimeScreenlet().initialize() ) ;
    }

    public List<Screenlet> getScreenlets() {
        return this.screenlets ;
    }

    // --------------------- Main method ---------------------------------------

    public static void main( String[] args ) {
        log.debug( "Starting Spring Booot..." ) ;
        SpringApplication.run( SConsole.class, args ) ;

        log.debug( "Starting SConsole.." ) ;
        SConsole app = SConsole.getAppContext().getBean( SConsole.class ) ;
        app.initialize() ;
        app.insertRecords() ;
    }
}
