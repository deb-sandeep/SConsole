package com.sandy.sconsole.util;

public class ResponseMsg {

    private String msg = null ;
    
    public static final ResponseMsg SUCCESS = new ResponseMsg( "Success" ) ;
    
    public ResponseMsg( String msg ) {
        this.msg = msg ;
    }

    public String getMsg() {
        return msg ;
    }

    public void setMsg( String msg ) {
        this.msg = msg ;
    }
}
