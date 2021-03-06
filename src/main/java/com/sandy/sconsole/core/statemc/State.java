package com.sandy.sconsole.core.statemc;

import java.util.HashMap ;
import java.util.Map ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.remote.KeyListener ;

public abstract class State implements KeyListener {
    
    private static final Logger log = Logger.getLogger( State.class ) ;

    class TransitionState {

        private String keyLabel = null ;
        private State targetState = null ;
        private boolean active = false ;
        
        public TransitionState( State tgtState ) {
            this( tgtState, null ) ;
        }
        
        public TransitionState( State tgtState, String keyLabel ) {
            this.targetState = tgtState ;
            this.keyLabel = keyLabel ;
        }
        
        public State getState() {
            return this.targetState ;
        }
        
        public String getKeyLabel() {
            return this.keyLabel ;
        }
        
        public boolean isActive() {
            return this.active ;
        }
        
        public void setActive( boolean active ) {
            this.active = active ;
        }
    }
    
    private Map<Key, TransitionState> transitionMap = null ;
    
    protected String stateName = null ;
    
    protected State( String stateName ) {
        this.stateName = stateName ;
        this.transitionMap = new HashMap<Key, TransitionState>() ;
    }
    
    public Map<Key, String> getActivatedKeyInfo() {
        
        Map<Key, String> activatedKeyMap = new HashMap<>() ;
        for( Key key : transitionMap.keySet() ) {
            TransitionState transition = transitionMap.get( key ) ;
            if( transition.isActive() ) {
                activatedKeyMap.put( key, transition.keyLabel ) ;
            }
        }
        
        return activatedKeyMap ;
    }
    
    public void resetState() {
        enableAllTransitions() ;
    }
    
    public State addTransition( Key key, State nextState ) {
        return addTransition( key, null, nextState ) ;
    }
    
    public State addTransition( Key key, String keyLabel, State nextState ) {
        if( transitionMap.containsKey( key ) ) {
            throw new IllegalStateException( "Key " + key + " is already "
                                      + " registered in the transition map." ) ;
        }
        TransitionState trnState = new TransitionState( nextState, keyLabel ) ;
        transitionMap.put( key, trnState ) ;
        return this ;
    }
    
    public void disableAllTransitions() {
        for( TransitionState transitions : transitionMap.values() ) {
            transitions.setActive( false ) ;
        }
    }
    
    public void enableAllTransitions() {
        for( TransitionState transitions : transitionMap.values() ) {
            transitions.setActive( true ) ;
        }
    }
    
    public void enableTransition( Key... keys ) {
        for( Key key : keys ) {
            if( !transitionMap.containsKey( key ) ) {
                throw new IllegalStateException( "Key " + key + " is absent in "
                        + "the transition map." ) ;
            }
            TransitionState trnState = transitionMap.get( key ) ;
            trnState.setActive( true ) ;
        }
    }
    
    public void disableTransition( Key... keys ) {
        for( Key key : keys ) {
            if( !transitionMap.containsKey( key ) ) {
                throw new IllegalStateException( "Key " + key + " is absent in "
                                               + "the transition map." ) ;
            }
            TransitionState trnState = transitionMap.get( key ) ;
            trnState.setActive( false ) ;
        }
    }
    
    /**
     * This method will be called before the next state's 
     * {@link #transitioningIn(State, Key)} * method is involved.
     */
    public void deactivate( State nextState, Key key ) {
        log.debug( "TransitionOut => " + 
                   stateName + " --" + key + "--> " + nextState.stateName ) ;
    }
    
    /**
     * This method will be called right before the from state's 
     * {@link #deactivate(State, Key)} * method is involved.
     */
    @SuppressWarnings( "incomplete-switch" )
    public void activate( Object payload, State fromState, Key key ) {
        
        log.debug( "Pre-activating state - " + getName() ) ;
        boolean processFurther = preActivate( payload, fromState, key ) ;
        
        if( fromState == null && key == null ) {
            return ;
        }
        
        log.debug( "TransitionIn => " + 
                    stateName + " <--" + key + "-- " + fromState.stateName ) ;
        
        if( !processFurther ) {
            log.debug( "Pre-activation logic prevented further dispath of key" ) ;
            return ;
        }
        
        switch( key ) {
            case UP        : handleUpNavKey()           ; break ;
            case LEFT      : handleLeftNavKey()         ; break ;
            case RIGHT     : handleRightNavKey()        ; break ;
            case DOWN      : handleDownNavKey()         ; break ;
            case SELECT    : handleSelectNavKey()       ; break ;
            case CANCEL    : handleCancelNavKey()       ; break ;
            case PLAYPAUSE : handlePlayPauseResumeKey() ; break ;
            case STOP      : handleStopKey()            ; break ;
            case FN_A      : handleFnAKey()             ; break ;
            case FN_B      : handleFnBKey()             ; break ;
            case FN_C      : handleFnCKey()             ; break ;
            case FN_D      : handleFnDKey()             ; break ;
            case FN_E      : handleFnEKey()             ; break ;
            case FN_F      : handleFnFKey()             ; break ;
            case FN_G      : handleFnGKey()             ; break ;
            case FN_H      : handleFnHKey()             ; break ;
            
            case SCR_SHOWHIDE:
                throw new IllegalStateException( "Key processor should not"
                        + "be receiving screenlet show/hide key event." ) ;
        }
    }
    
    public String getName() {
        return this.stateName ;
    }
    
    public final TransitionRequest acceptKey( Key key ) 
        throws IllegalStateException {
        
        log.debug( "State " + stateName + " is accepting key " + key ) ;
        TransitionState transition = transitionMap.get( key ) ;
        if( transition != null ) {
            if( !transition.isActive() ) {
                log.warn( "Transition is deactivated for key " + key ) ;
                throw new IllegalStateException( "Transition deactivated against key " + key ) ;
            }
            else {
                Object payload = getTransitionOutPayload( transition.getState(), key ) ;
                return new TransitionRequest( transition.getState(), payload ) ;
            }
        }
        
        if( transition == null ) {
            handleNonTransitionMappedKey( key ) ;
        }
        
        return null ;
    }
    
    @Override public void handleLeftNavKey() {}
    @Override public void handleRightNavKey() {}
    @Override public void handleUpNavKey() {}
    @Override public void handleDownNavKey() {}
    @Override public void handleSelectNavKey() {}
    @Override public void handleCancelNavKey() {}
    @Override public void handlePlayPauseResumeKey() {}
    @Override public void handleStopKey() { }
    @Override public void handleFnAKey() {}
    @Override public void handleFnBKey() {}
    @Override public void handleFnCKey() {}
    @Override public void handleFnDKey() {}
    @Override public void handleFnEKey() {}
    @Override public void handleFnFKey() {}
    @Override public void handleFnGKey() {}
    @Override public void handleFnHKey() {}
    
    /**
     * This function is called by the activate method before the key which 
     * caused the activation is processed. 
     * 
     * If this method returns a false, further processing of the key is ignored.
     */
    public boolean preActivate( Object payload, State fromState, Key key ) {
        return true ;
    }
    
    /**
     * This function is called before transitioning to the next state for 
     * collecting the payload. Subclasses can use the parameters to 
     * decide on the appropriate payload which will be passed to the 
     * next state.
     */
    public Object getTransitionOutPayload( State nextState, Key key ) {
        return null ;
    }
    
    public void handleNonTransitionMappedKey( Key key ) {
        // By default this function does nothing. Subclasses may choose to
        // override this function to provide functionality for key presses
        // which are not mapped to transition activities.
    }
}
