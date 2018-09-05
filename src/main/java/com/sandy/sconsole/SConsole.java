package com.sandy.sconsole ;

import java.util.* ;

import org.apache.log4j.Logger ;
import org.springframework.beans.BeansException ;
import org.springframework.boot.SpringApplication ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;
import org.springframework.context.ApplicationContext ;
import org.springframework.context.ApplicationContextAware ;

import com.sandy.common.bus.EventBus ;
import com.sandy.sconsole.core.SConsoleConfig ;
import com.sandy.sconsole.core.frame.SConsoleFrame ;
import com.sandy.sconsole.core.screenlet.Screenlet ;
import com.sandy.sconsole.core.util.DayTickListener ;
import com.sandy.sconsole.core.util.SecondTickListener ;
import com.sandy.sconsole.screenlet.daytime.DayTimeScreenlet ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;

@SpringBootApplication
public class SConsole implements ApplicationContextAware {

    private static final Logger log = Logger.getLogger( SConsole.class ) ;

    private static Timer              SEC_TIMER       = new Timer( "SEC_TIMER", true ) ;
    private static ApplicationContext APP_CTX         = null ;
    private static SConsole           APP             = null ;
    
    private static List<SecondTickListener> secondListeners = new ArrayList<>() ;
    private static List<DayTickListener>    dayListeners    = new ArrayList<>() ;
    
    private static Object lock = new Object() ;
    
    public static EventBus GLOBAL_EVENT_BUS = new EventBus() ;
    
    public static class GlobalEvent {
        public static final int SESSION_SAVED = 5000 ;
    }

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
