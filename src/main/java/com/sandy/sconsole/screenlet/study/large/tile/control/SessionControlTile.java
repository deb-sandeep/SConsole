package com.sandy.sconsole.screenlet.study.large.tile.control;

import java.util.Map ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.remote.RemoteController ;
import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.remote.KeyProcessor ;
import com.sandy.sconsole.core.screenlet.Screenlet.RunState ;
import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.core.statemc.TransitionRequest ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.control.state.* ;

@SuppressWarnings( "serial" )
public class SessionControlTile extends SessionControlTileUI {
    
    private static final Logger log = Logger.getLogger( SessionControlTile.class ) ;
    
    class ControlTileKeyProcessor extends KeyProcessor {

        @Override public String getName() {
            return "ControlTileKeyProcessor" ;
        }

        @Override public void processKey( Key key ) {
            
            log.debug( "StateMachine received key " + key ) ;
            TransitionRequest transition = null ; 
            State nextState = null ;
            
            try {
                transition = currentState.acceptKey( key ) ;
                nextState = transition.getNextState() ;
                
                currentState.deactivate( nextState, key ) ;
                nextState.activate( transition.getPayload(), currentState, key ) ;
                
                currentState = nextState ;
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
    }
    
    private HomeState homeState = null ;
    private PlayState playState = null ;
    private ChangeState changeState = null ;
    
    private State currentState = null ;
    
    private State[] states = null ;
    
    private ControlTileKeyProcessor keyProcessor = null ;
    
    private RemoteController controller = null ;
    
    public SessionControlTile( StudyScreenletLargePanel parent ) {
        super( parent ) ;
        keyProcessor = new ControlTileKeyProcessor() ;
        controller = SConsole.getAppContext().getBean( RemoteController.class ) ;
        
        initializeStateMachine() ;
    }
    
    private void initializeStateMachine() {
        
        StudyScreenletLargePanel largePanel = (StudyScreenletLargePanel)parent ;
        
        homeState = new HomeState( this, largePanel ) ;
        playState = new PlayState( this, largePanel ) ;
        changeState = new ChangeState( this, largePanel ) ;
        
        homeState.addTransition( Key.PLAYPAUSE, playState )
                 .addTransition( Key.FN_A, "Change", changeState ) ;
        
        changeState.addTransition( Key.PLAYPAUSE, playState )
                   .addTransition( Key.CANCEL, homeState ) ;
        
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
        
        if( getScreenlet().getRunState() == RunState.STOPPED ) {
            startStateMachine() ;
        }
    }
    
    @Override
    protected void screenletMinimized() {
        super.screenletMinimized() ;
        controller.popKeyProcessor() ;
    }
    
    /**
     * This method can be called upon to programatically feed transition triggers
     * to the state machine. 
     * 
     * By default, the key presses by user are used to drive the state machine,
     * however there might arrise occasional cases (like resuming or stopping
     * from a pause screen) which might necessitate driving the state machine
     * programatically.
     */
    public void feedIntoStateMachine( Key key ) {
        this.keyProcessor.processKey( key ) ;
    }
}
