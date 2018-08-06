package com.sandy.sconsole.dto;

public class ButtonPressEvent {

    private String btnType = null ;
    private String btnCode = null ;
    
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
}
