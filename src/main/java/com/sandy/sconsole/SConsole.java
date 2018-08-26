package com.sandy.sconsole ;

import java.util.* ;

import org.apache.log4j.* ;
import org.springframework.beans.* ;
import org.springframework.boot.* ;
import org.springframework.boot.autoconfigure.* ;
import org.springframework.context.* ;

import com.sandy.sconsole.core.* ;
import com.sandy.sconsole.core.frame.* ;
import com.sandy.sconsole.core.screenlet.* ;
import com.sandy.sconsole.core.util.* ;
import com.sandy.sconsole.screenlet.daytime.* ;
import com.sandy.sconsole.screenlet.dummy.* ;
import com.sandy.sconsole.screenlet.study.* ;

@SpringBootApplication
public class SConsole implements ApplicationContextAware {

    private static final Logger log = Logger.getLogger( SConsole.class ) ;

    private static Timer              SEC_TIMER       = new Timer( "SEC_TIMER", true ) ;
    private static ApplicationContext APP_CTX         = null ;
    private static SConsole           APP             = null ;
    
    private static List<SecondTickListener> secondListeners = new ArrayList<>() ;
    private static List<DayTickListener>    dayListeners    = new ArrayList<>() ;
    
    private static Object lock = new Object() ;

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

    public SConsole() {
        APP = this ;
    }

    public void initialize() {
        registerScreenlets() ;
        this.frame = new SConsoleFrame() ;
    }
    
    public void testJPA() {
    }
    
    @Override
    public void setApplicationContext( ApplicationContext applicationContext )
            throws BeansException {
        APP_CTX = applicationContext ;
    }

    private void registerScreenlets() {

        log.debug( "Registering screenlets" ) ;
        screenlets.add( new DayTimeScreenlet().initialize() ) ;
        screenlets.add( new StudyScreenlet( "IIT - Physics" ).initialize() ) ;
        screenlets.add( new StudyScreenlet( "IIT - Chemistry" ).initialize() ) ;
        screenlets.add( new StudyScreenlet( "IIT - Maths" ).initialize() ) ;
        screenlets.add( new DummyScreenlet( "Dummy" ).initialize() ) ;
    }

    public List<Screenlet> getScreenlets() {
        return this.screenlets ;
    }
    
    public SConsoleFrame getFrame() {
        return this.frame ;
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
