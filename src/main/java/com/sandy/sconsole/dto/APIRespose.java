package com.sandy.sconsole.dto;

public class APIRespose {

    private String message = null ;
    
    public APIRespose( String msg ) {
        this.message = msg ;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }
}
