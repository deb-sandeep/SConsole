package com.sandy.sconsole.core.remote;

public abstract class Handler {
    
    private String btnHint = "" ;
    
    protected Handler( String btnHint ) {
        this.btnHint = btnHint ;
    }
    
    public String getBtnHint() {
        return this.btnHint ;
    }
    
    public abstract void handle() ;
}
