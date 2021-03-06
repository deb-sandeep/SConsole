package com.sandy.sconsole.screenlet.study.large.tile.control;

import java.util.Calendar ;
import java.util.Map ;

import org.apache.log4j.Logger ;

import com.sandy.common.bus.Event ;
import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.remote.RemoteController ;
import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.remote.KeyProcessor ;
import com.sandy.sconsole.core.screenlet.Screenlet.RunState ;
import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.core.statemc.TransitionRequest ;
import com.sandy.sconsole.core.util.DayTickListener ;
import com.sandy.sconsole.core.util.SecondTickListener ;
import com.sandy.sconsole.screenlet.study.TopicBurnInfo ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.control.state.BaseControlTileState ;
import com.sandy.sconsole.screenlet.study.large.tile.control.state.ChangeState ;
import com.sandy.sconsole.screenlet.study.large.tile.control.state.HomeState ;
import com.sandy.sconsole.screenlet.study.large.tile.control.state.PlayState ;

@SuppressWarnings( "serial" )
public class SessionControlTile extends SessionControlTileUI
    implements SecondTickListener, DayTickListener {
    
    private static final Logger log = Logger.getLogger( SessionControlTile.class ) ;
    
    class ControlTileKeyProcessor extends KeyProcessor {
        
        private long lastKeyReceivedTime = -1 ;

        // Note that the center tile panel card is replaced by projection 
        // card for the play state, where the user is expected to indicate a
        // projected completion time for the current problem being solved.
        @Override public String getCenterPanelCardName() {
            return currentState.getCenterPanelCardName() ;
        }
        
        @Override public String getName() {
            return "ControlTileKeyProcessor" ;
        }

        @Override public void processKey( Key key ) {
            
            log.debug( "StateMachine received key " + key ) ;
            
            TransitionRequest transition = null ; 
            State nextState = null ;
            
            lastKeyReceivedTime = System.currentTimeMillis() ;
            
            try {
                
                log.debug( "Current state = " + currentState.getName() ) ;
                transition = currentState.acceptKey( key ) ;
                
                if( transition == null ) {
                    // This key was consumed without the need for a state
                    // change. Nothing much to do in this case.
                }
                else {
                    nextState = transition.getNextState() ;
                    log.debug( "Next state = " + nextState.getName() ) ;
                    
                    log.debug( "Deactivating state " + currentState.getName() ) ;
                    currentState.deactivate( nextState, key ) ;
                    
                    log.debug( "Activating state = " + nextState.getName() ) ;
                    nextState.activate( transition.getPayload(), currentState, key ) ;
                    
                    currentState = ( BaseControlTileState )nextState ;
                    log.debug( "Current state is now = " + currentState.getName() ) ;
                }
            }
            catch( Exception e ) {
                log.error( "Error processing key " + key + " in state " + 
                           currentState.getName(), e ) ;
            }
        }

        @Override
        public Map<Key, String> getActivatedKeyInfo() {
            return currentState.getActivatedKeyInfo() ;
        }
        
        public void setLastKeyReceivedTime( long time ) {
            this.lastKeyReceivedTime = time ;
        }
        
        public long timeSinceLastKeyProcess() {
            if( lastKeyReceivedTime == -1 ) {
                return -1 ;
            }
            return (System.currentTimeMillis() - lastKeyReceivedTime)/1000 ;
        }
    }
    
    private HomeState homeState = null ;
    private PlayState playState = null ;
    private ChangeState changeState = null ;
    
    private BaseControlTileState currentState = null ;
    
    private State[] states = null ;
    
    private ControlTileKeyProcessor keyProcessor = null ;
    
    private RemoteController controller = null ;
    
    public SessionControlTile( StudyScreenletLargePanel parent ) {
        super( parent ) ;
        keyProcessor = new ControlTileKeyProcessor() ;
        controller = SConsole.getAppContext().getBean( RemoteController.class ) ;
        
        SConsole.addSecTimerTask( this ) ;
        SConsole.GLOBAL_EVENT_BUS
                .addSubscriberForEventTypes( this, true, 
                                             EventCatalog.TOPIC_BURN_CALIBRATED ) ;
        
        initializeStateMachine() ;
    }
    
    private void initializeStateMachine() {
        
        StudyScreenletLargePanel largePanel = (StudyScreenletLargePanel)parent ;
        
        homeState = new HomeState( this, largePanel ) ;
        playState = new PlayState( this, largePanel ) ;
        changeState = new ChangeState( this, largePanel ) ;
        currentState = homeState ;
        
        homeState.addTransition( Key.PLAYPAUSE, playState )
                 .addTransition( Key.FN_A, "Change", changeState ) ;
        
        changeState.addTransition( Key.PLAYPAUSE, playState )
                   .addTransition( Key.CANCEL, homeState )
                   .addTransition( Key.SELECT, homeState ) ;
        
        playState.addTransition( Key.STOP, homeState ) ;
        
        states = new State[]{ homeState, playState, changeState } ;
    }
    
    // Note that the state machine is started on maximization of the screenlet
    // and only if the screenlet is currently in the stopped state. This
    // ensures that the screenlet is refreshed with fresh data every time it
    // is visited in a neutral state.
    private void startStateMachine() {
        resetAllStates() ;
        currentState = homeState ;
        currentState.activate( null, null, null ) ;
    }
    
    private void resetAllStates() {
        for( State state : states ) {
            state.resetState() ;
        }
    }
    
    public SessionInformation getSessionInfo() {
        return homeState.getSessionInfo() ;
    }

    /**
     * This method is called when the StudyScreenlet to which this tile belongs
     * becomes maximized.
     * 
     * This being a control tile, we try to capture all the key presses. To
     * do this, we push a custom key processor.
     */
    @Override
    protected void screenletMaximized() {
        super.screenletMaximized() ;
        controller.pushKeyProcessor( keyProcessor ) ; 
        
        keyProcessor.setLastKeyReceivedTime( System.currentTimeMillis() ) ;
        
        if( getScreenlet().getRunState() == RunState.STOPPED ) {
            startStateMachine() ;
        }
    }
    
    @Override
    protected void screenletMinimized() {
        super.screenletMinimized() ;
        controller.popKeyProcessor() ;
    }
    
    @Override
    public void handleEvent( Event event ) {
        super.handleEvent( event ) ;
        
        if( event.getEventType() == EventCatalog.TOPIC_BURN_CALIBRATED ) {
            handleTopicBurnCalibration( (TopicBurnInfo)event.getValue() ) ;
        }
    }
    
    // A TOPIC_BURN_CALIBRATED event means that problem activations for
    // a topic have been changed through the control UI. If such an
    // event happens, we need to reflect the change realtime in the 
    // study control panel.
    //
    // Now, this is a complicated scenario and we have to handle many cases
    //
    // 1. The screenlet is not maximized (visible) - 
    //    We ignore the event. The screenlet will refresh itself with the
    //    latest changes the next time it gets maximized. 
    //
    // 2. The screenlet is not operating on the topic which was calibrated
    //    Again, we ignore the event. Not this screenlets business
    //
    // 3. The screenlet is operating on the topic which was calibrated
    //    We definitely need to handle this scenario. There are multiple
    //    sub-scenarios in this.
    //
    //  3.1 If we are currently in the middle of a session (run) or we 
    //      are changing, we can't do anything. Show a message to the user
    //      to stop the current operation and go to the home page.
    //
    //  3.2 If we are idling in the home state, we initiate a faux reload.
    private void handleTopicBurnCalibration( TopicBurnInfo bi ) {
        
        if( getScreenlet().isVisible() ) {
            SessionInformation si = getSessionInfo() ;
            if( si.session.getTopic().equals( bi.getTopic() ) ) {
                
                if( currentState == playState || 
                    currentState == changeState ) {
                    
                    showMessage( "Topic burn has been recalibrated. Please reload." ) ;
                }
                else {
                    startStateMachine() ;
                }
            }
        }
    }

    @Override
    public void secondTicked( Calendar calendar ) {
        
        if( getScreenlet().isVisible() && ( currentState == homeState ) ) {
            
            if( keyProcessor.timeSinceLastKeyProcess() >= 300 ) {
                log.debug( "\n\n5 minutes of inactivity detected." ) ;
                log.debug( "Reverting to calendar screenlet." ) ;
                try {
                    SConsole.getApp()
                            .getFrame()
                            .handleScreenletSelectionEvent( "1" ) ;
                }
                catch( Exception e ) {
                    log.error( "Could not process key", e ) ;
                }
            }
        }
    }

    /**
     * If a day change has been detected, we need to refresh certain 
     * information on the screenlet, for example the thermometer etc. Some
     * of these tiles are autonomous, for example the L30 operates on its
     * own, however there are other tiles which are contextual - for example
     * the thermometer which depends upon the current topic.
     * 
     * In this method, we instruct the current state to handle the day
     * change event as it deems fit.
     */
    @Override
    public void dayTicked( Calendar instance ) {
        currentState.handleDayChange() ;
    }
}
