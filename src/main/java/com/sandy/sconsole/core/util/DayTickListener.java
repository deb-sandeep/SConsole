package com.sandy.sconsole.core.util;

import java.util.* ;

public interface DayTickListener {
    public void dayTicked( Calendar instance ) ;
    
    default public boolean isAsync() {
        return false ;
    }
}
