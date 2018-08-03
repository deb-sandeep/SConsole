package com.sandy.sconsole;

import java.awt.Color ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.HashSet ;
import java.util.Set ;
import java.util.Timer ;
import java.util.TimerTask ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.ui.SConsoleFrame ;

public class SConsole {

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
    
    public static void main( String[] args ) {
        log.debug( "Starting SConsole.." ) ;
        new SConsoleFrame() ;
    }
}
