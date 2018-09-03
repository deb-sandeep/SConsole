package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import java.util.Calendar ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.core.util.SecondTickListener ;
import com.sandy.sconsole.dao.entity.LastSession ;
import com.sandy.sconsole.dao.entity.Session.SessionType ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionInformation ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.Btn1Type ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.Btn2Type ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.OutcomeButtonsState ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.PauseDialog ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.SessionTypeChangeDialog ;

/**
 * Transition In:
 * --------------
 *  HomeState    -> Key.PLAY
 *  ChangeState  -> Key.PLAY
 *  
 * 
 * Transition Out:
 * ---------------
 *  Key.STOP     -> HomeState
 *  
 * Self Transitions:
 * -----------------
 *  Key.FN_A      -> Mark problem as Solved
 *  Key.FN_B      -> Mark problem as Redo
 *  Key.FN_C      -> Mark problem as Pigeon
 *  Key.FN_D      -> Mark problem as Skip
 *  Key.PLAYPAUSE -> Show pause dialog / Resume
 *  
 * Programmatic Transitions:
 * -------------------------
 *  Key.PLAYPAUSE -> Resume session
 *  Key.STOP      -> Transition to HomeState
 */
public class PlayState extends BaseControlTileState 
    implements SecondTickListener {

    public static final String NAME = "Play" ;
    
    private SessionInformation si = null ;
    private boolean isRunning = false ;
    
    private PauseDialog pauseDialog = null ;
    
    private long runTime = 0 ;
    private long pauseTime = 0 ;
    
    public PlayState( SessionControlTile tile, StudyScreenletLargePanel screenletPanel ) {
        super( NAME, tile, screenletPanel ) ;
        
        addTransition( Key.FN_A, "Solved", this ) ;
        addTransition( Key.FN_B, "Redo",   this ) ;
        addTransition( Key.FN_C, "Pigeon", this ) ;
        addTransition( Key.FN_D, "Skip",   this ) ;
        addTransition( Key.FN_E, "*",      this ) ;
        addTransition( Key.FN_F, "Ignore", this ) ;
        
        addTransition( Key.PLAYPAUSE, this ) ;
        
        pauseDialog = new PauseDialog( this ) ;
    }

    @Override
    public void resetState() {
        si = null ;
        isRunning = false ;
        runTime = 0 ;
        pauseTime = 0 ;
    }

    @Override
    public boolean preActivate( Object payload, State fromState, Key key ) {
        
        // If this activation is not due to a self transition, then only do this
        if( fromState != this ) {
            if( (payload == null) || !(payload instanceof SessionInformation) ) {
                throw new IllegalArgumentException( 
                        "PlayState activation payload is null or is not of "
                        + "type SessionInformation" ) ;
            }
            
            // Note that the session information has been sanitized for starting
            // the session immediately.
            this.si = ( SessionInformation )payload ;
            
            // Save the session in the database. Post this point, the blank
            // session will contain a valid session id and any further saves
            // will end up updating the session instead of creating a new one
            sessionRepo.save( this.si.session ) ;
            lastSessionRepo.update( getSubjectName(), si.session.getId() ) ;
            
            // Start the timers and active self transitions
            SConsole.addSecTimerTask( this ) ;
            
            // Initialize the UI. 
            initializeUIAndTransitions( fromState ) ;
        }
        
        // Let all the triggers for activation be handled. In case this is
        // a play transition  from outside, the playpause method will detect
        // it and set the runstate to true.
        return true ;
    }
    
    private void initializeUIAndTransitions( State fromState ) {
        // Note that any invalidations will have been removed before the 
        // transition happened. 
        
        // Doesn't matter where the transition has come from, we have to
        // activate the pause and stop buttons
        tile.setBtn1UI( Btn1Type.PAUSE ) ;
        tile.setBtn2UI( Btn2Type.STOP ) ;
        enableTransition( Key.PLAYPAUSE, Key.STOP ) ;
        
        // If the session is an Exercise, we need to activate the problem
        // outcome controls and transitions
        if( si.session.getSessionType() == SessionType.EXERCISE ) {
            tile.setOutcomeButtonsState( OutcomeButtonsState.ACTIVE ) ;
        }
        
        // If the transition is happening from Change, replace the buttons 
        // pause and stop and clear any change trigger highlights
        if( fromState.getName().equals( ChangeState.NAME ) ) {
            tile.clearChangeUIHighlights() ;
        }
    }
    
    /* This method is called when the state machine is about to execute a 
     * transition from this state. We take this opportunity to do some
     * cleanup work - for example, remove ourselves from the list of second
     * timer task listeners.
     * 
     * NOTE that this method is also called during self transitions. Hence
     * before removing ourselves from the second listener list, we must check
     * if the next state is not a self transition.
     */
    @Override
    public void deactivate( State nextState, Key key ) {
        super.deactivate( nextState, key ) ;
        if( nextState != this ) {
            isRunning = false ;
            // TODO: Handle lap time.
            // TODO: Close the lap and session!
            SConsole.removeSecTimerTask( this ) ;
        }
    }

    /* This method is called whenever Key.RUN causes a transition. This may
     * be with one of the two intentions - Play or Resume. We decide which,
     * based on the current run state.
     */
    @Override
    public void handlePlayPauseResumeKey() {
        if( !isRunning ) {
            // Set the state to running, so that the next Key.PLAY transition
            // can be treated as intention to pause.
            this.isRunning = true ;
        }
        else {
            this.isRunning = false ;
            showDialog( pauseDialog ) ;
        }
    }

    // Mark problem as Solved
    @Override
    public void handleFnAKey() {
    }

    // Mark problem as Redo
    @Override
    public void handleFnBKey() {
    }

    // Mark problem as Pigeon
    @Override
    public void handleFnCKey() {
    }
    
    // Mark problem as Skip
    @Override
    public void handleFnDKey() {
    }
    
    // Mark problem as Star
    @Override
    public void handleFnEKey() {
    }

    // Mark problem as Ignore
    // TODO: Will need database changes.
    @Override
    public void handleFnFKey() {
    }

    /* TODO: Handle lap time
     *
     */
    @Override
    public void secondTicked( Calendar calendar ) {
        if( isRunning ) {
            runTime++ ;
            tile.updateSessionTimeLabel( runTime ) ;
        }
        else {
            pauseTime++ ;
        }
    }
}
