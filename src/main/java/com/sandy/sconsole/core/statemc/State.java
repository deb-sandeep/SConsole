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
        private boolean active = true ;
        
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
    
    public void resetState() {
        enableAllTransitions() ;
    }
    
    public void addTransition( Key key, State nextState ) {
        addTransition( key, null, nextState ) ;
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
    
    public void enableTransition( Key key ) {
        if( !transitionMap.containsKey( key ) ) {
            throw new IllegalStateException( "Key " + key + " is absent in "
                    + "the transition map." ) ;
        }
        transitionMap.get( key ).setActive( true ) ;
    }
    
    public void disableTransition( Key key ) {
        if( !transitionMap.containsKey( key ) ) {
            throw new IllegalStateException( "Key " + key + " is absent in "
                                           + "the transition map." ) ;
        }
        transitionMap.get( key ).setActive( false ) ;
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
    public void activate( Object payload, State fromState, Key key ) {
        
        if( fromState == null && key == null ) {
            log.debug( "Transitioning in " + stateName + " as the state state." ) ;
            return ;
        }
        
        log.debug( "TransitionIn => " + 
                   stateName + " <--" + key + "-- " + fromState.stateName ) ;
        
        boolean processFurther = preActivate( payload, fromState, key ) ;
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
            
            case SCR_SEL_SHOWHIDE:
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
        if( transition == null ) {
            log.error( "No transitions against key " + key ) ;
            throw new IllegalStateException( "No transition registered " + 
                                             "against key " + key ) ;
        }
        else if( !transition.isActive() ) {
            log.warn( "Transition is deactivated for key " + key ) ;
            throw new IllegalStateException( "Transition deactivated against key " + key ) ;
        }
        
        Object payload = getTransitionOutPayload( transition.getState(), key ) ;
        
        return new TransitionRequest( transition.getState(), payload ) ;
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
}
