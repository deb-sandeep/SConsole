package com.sandy.sconsole.core.statemc ;

public class TransitionRequest {

    private Object payload = null ;
    private State nextState = null ;
    
    public TransitionRequest( State nextState ) {
        this( nextState, null ) ;
    }
    
    public TransitionRequest( State nextState, Object payload ) {
        this.nextState = nextState ;
        this.payload = payload ;
    }
    
    public State getNextState() {
        return this.nextState ;
    }
    
    public Object getPayload() {
        return this.payload ;
    }
}
