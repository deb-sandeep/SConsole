package com.sandy.sconsole.core.remote ;

public class KeyActivationInfo {
    private Key key = null ;
    private Boolean active = false ;
    private String keyLabel = null ;
    
    public KeyActivationInfo( Key key, Boolean active, String keyLabel ) {
        this.key = key ;
        this.active = active ;
        this.keyLabel = keyLabel ;
    }

    public Key getKey() {
        return key ;
    }

    public void setKey( Key key ) {
        this.key = key ;
    }

    public Boolean getActive() {
        return active ;
    }

    public void setActive( Boolean active ) {
        this.active = active ;
    }

    public String getKeyLabel() {
        return keyLabel ;
    }

    public void setKeyLabel( String keyLabel ) {
        this.keyLabel = keyLabel ;
    }
}