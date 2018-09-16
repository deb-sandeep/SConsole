package com.sandy.sconsole;

import com.sandy.sconsole.core.screenlet.Screenlet ;
import com.sandy.sconsole.core.util.Payload ;
import com.sandy.sconsole.dao.entity.ProblemAttempt ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.screenlet.study.TopicBurnInfo ;

public class EventCatalog {

    public static final int CORE_EVENT_RANGE_MIN = 100 ;
    public static final int CORE_EVENT_RANGE_MAX = 200 ;

    // =============== Core Events [Start] =====================================
    
    @Payload( type=Screenlet.class )
    public static final int SCREENLET_MAXIMIZED = 100 ;
    
    @Payload( type=Screenlet.class )
    public static final int SCREENLET_MINIMIZED = 101 ;
    
    @Payload( type=Screenlet.class )
    public static final int SCREENLET_RUN_STATE_CHANGED = 102 ;
    
    // --------------- Core Events [End] ---------------------------------------

    // =============== Session Events [Start] ==================================
    
    @Payload( type=Session.class )
    public static final int SESSION_STARTED = 5001 ;
    
    @Payload( type=Session.class )
    public static final int SESSION_ENDED = 5002 ;
    
    @Payload( type=Session.class )
    public static final int SESSION_PLAY_HEARTBEAT = 5003 ;
    
    @Payload( type=ProblemAttempt.class )
    public static final int PROBLEM_ATTEMPT_STARTED = 5004 ;
    
    @Payload( type=ProblemAttempt.class )
    public static final int PROBLEM_ATTEMPT_ENDED = 5005 ;
    
    @Payload( type=Topic.class )
    public static final int TOPIC_CHANGED = 5006 ;
    
    @Payload( type=TopicBurnInfo.class )
    public static final int BURN_INFO_REFRESHED = 5007 ;
    
    // --------------- Session Events [End] ------------------------------------

    // =============== API Generated Events [Start] ============================
    
    @Payload( type=TopicBurnInfo.class )
    public static final int TOPIC_BURN_CALIBRATED = 6001 ;
    
    // --------------- API Generated Events [End] ------------------------------
}
