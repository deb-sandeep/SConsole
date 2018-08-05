package com.sandy.sconsole;

import java.awt.Color ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.HashSet ;
import java.util.Set ;
import java.util.Timer ;
import java.util.TimerTask ;

import org.apache.log4j.Logger ;
import org.springframework.beans.BeansException ;
import org.springframework.boot.SpringApplication ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;
import org.springframework.context.ApplicationContext ;
import org.springframework.context.ApplicationContextAware ;

import com.sandy.sconsole.ui.SConsoleFrame ;

@SpringBootApplication
public class SConsole implements ApplicationContextAware {

    private static final Logger log = Logger.getLogger( SConsole.class ) ;
    
    public static Color BG_COLOR = Color.BLACK ;
    
    private static Timer          SEC_TIMER       = new Timer( "SEC_TIMER", true ) ;
    private static Set<TimerTask> DAY_TIMER_TASKS = new HashSet<TimerTask>() ;
    
    static {
        addSecTimerTask( new TimerTask() {
            Calendar lastDate = null ;
            public void run() {
                if( lastDate == null ) {
                    lastDate = Calendar.getInstance() ;
                }
                else {
                    Calendar now = Calendar.getInstance() ;
                    if( now.get( Calendar.DAY_OF_YEAR ) != 
                        lastDate.get( Calendar.DAY_OF_YEAR ) ) {
                        
                        for( TimerTask task : DAY_TIMER_TASKS ) {
                            task.run() ;
                        }
                    }
                    lastDate = now ;
                }
            }
        } );
    }
    
    public static void addSecTimerTask( TimerTask task ) {
        SEC_TIMER.scheduleAtFixedRate( task, new Date(), 1000 ) ;
    }
    
    public static void addDayTimerTask( TimerTask task ) {
        DAY_TIMER_TASKS.add( task ) ;
    }
    
    private static ApplicationContext APP_CTX = null ;
    
    @Override
    public void setApplicationContext( ApplicationContext applicationContext )
            throws BeansException {
        APP_CTX = applicationContext ;
    }
    
    public static ApplicationContext getAppContext() {
        return APP_CTX ;
    }
    
    public static SConsoleConfig getConfig() {
        return ( SConsoleConfig )APP_CTX.getBean( "config" ) ;
    }

    public static void main( String[] args ) {
        log.debug( "Starting Spring Booot..." ) ;
        SpringApplication.run( SConsole.class, args ) ;

        log.debug( "Starting SConsole.." ) ;
        new SConsoleFrame() ;
    }
}
