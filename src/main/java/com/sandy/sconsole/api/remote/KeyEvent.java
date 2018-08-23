package com.sandy.sconsole.api.remote;

public class KeyEvent {
    
    private String btnType = null ;
    private String btnCode = null ;
    
    public String getKeyId() {
        return btnType + "@" + btnCode ;
    }
    
    public String getBtnType() {
        return btnType;
    }
    
    public void setBtnType( String btnType ) {
        this.btnType = btnType;
    }
    
    public String getBtnCode() {
        return btnCode;
    }
    
    public void setBtnCode( String btnCode ) {
        this.btnCode = btnCode;
    }
    
    public String toString() {
        return "KeyEvent [" + btnType + ", " + btnCode + "]" ;
    }
}
