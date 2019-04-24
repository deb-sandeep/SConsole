package com.sandy.sconsole.core.util;

public class SConsoleUtil {

    public static String getElapsedTimeLabel( long seconds, boolean longFormat ) {
        
        int secs    = (int)(seconds) % 60 ;
        int minutes = (int)((seconds / 60) % 60) ;
        int hours   = (int)(seconds / (60*60)) ;
        
        if( longFormat ) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs ) ;
        }
        return String.format("%02d:%02d", minutes, secs ) ;
    }
}
