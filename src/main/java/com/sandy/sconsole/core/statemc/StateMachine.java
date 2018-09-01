package com.sandy.sconsole.core.statemc;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.remote.Key ;

public class StateMachine {

    private static final Logger log = Logger.getLogger( StateMachine.class ) ;
    
    private State startState = null ;
    private State currentState = null ;
    
    public State addStartState( State state ) {
        this.startState = state ;
        this.currentState = startState ;
        return this.startState ;
    }
    
    public void start() {
        this.currentState.activate( null, null ) ;
    }
    
    public void acceptKey( Key key ) {
        log.debug( "StateMachine received key " + key ) ;
        
        TransitionRequest trnReq = null ; 
        
        try {
            trnReq = currentState.acceptKey( key ) ;
            
            if( nextState != null ) {
                this.currentState.deactivate( nextState, key ) ;
                nextState.activate( this.currentState, key ) ;
                
                this.currentState = nextState ;
            }
        }
        catch( Exception e ) {
            log.error( "Error processing key " + key + " in state " + 
                       currentState.stateName, e ) ;
        }
        
    }
}
