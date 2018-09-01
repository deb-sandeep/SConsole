package com.sandy.sconsole.core.remote;

import java.util.Date ;
import java.util.Map ;

import org.apache.commons.lang.StringUtils ;
import org.apache.log4j.Logger ;

public class KeyProcessor {

    static final Logger log = Logger.getLogger( KeyProcessor.class ) ;
    
    private String name = "<Unnamed Key Processor>" ;
    private Map<Key, KeyActivationInfo> activationMap = null ;
    private KeyListener listener = null ;
    private Date lastKeyReceivedTime = null ;
    
    public KeyProcessor( String name, KeyListener listener ) {
        if( name != null ) {
            this.name = name ;
        }
        if( listener == null ) {
            throw new IllegalArgumentException( "RemoteKeyListener can't be null" ) ;
        }
        this.listener = listener ;
        activationMap = Key.getDefaultKeyActivationMap() ;
    }
    
    public String getName() {
        return this.name ;
    }
    
    public void enableAllKeys() {
        for( KeyActivationInfo actInfo : activationMap.values() ) {
            actInfo.setActive( true ) ;
        }
    }
    
    public void disableAllKeys() {
        for( KeyActivationInfo actInfo : activationMap.values() ) {
            actInfo.setActive( false ) ;
            actInfo.setKeyLabel( null ) ;
        }
    }
    
    public void enableNavKeys() {
        enableKeyType( RemoteKeyType.NAV_CONTROL, true ) ;
    }
    
    public void disableNavKeys() {
        enableKeyType( RemoteKeyType.NAV_CONTROL, false ) ;
    }
    
    public void enableRunKeys() {
        enableKeyType( RemoteKeyType.RUN, true ) ;
    }
    
    public void disableRunKeys() {
        enableKeyType( RemoteKeyType.RUN, false ) ;
    }
    
    public void enableFnKeys() {
        enableKeyType( RemoteKeyType.FUNCTION, true ) ;
    }
    
    public void disableFnKeys() {
        enableKeyType( RemoteKeyType.FUNCTION, false ) ;
    }
    
    private void enableKeyType( RemoteKeyType keyType, boolean enable ) {
        for( Key key : Key.getsKeysOfType( keyType ) ) {
            if( enable ) {
                enableKey( key ) ;
            }
            else {
                disableKey( key ) ;
            }
        }
    }
    
    public void enableKey( Key key ) {
        enableKey( key, null ) ;
    }
    
    public void enableKey( Key key, String label ) {
        KeyActivationInfo actInfo = activationMap.get( key ) ;
        
        if( actInfo.getActive() ) {
            throw new IllegalStateException( "Key " + key + " is already activated." + 
                   "Please disable explicitly before enabling it again." ) ;
        }
        
        actInfo.setActive( true ) ;
        actInfo.setKeyLabel( label ) ;
    }
    
    public void disableKey( Key key ) {
        KeyActivationInfo actInfo = activationMap.get( key ) ;
        actInfo.setActive( false ) ;
        actInfo.setKeyLabel( null ) ;
    }
    
    public boolean isKeyEnabled( Key key ) {
        KeyActivationInfo actInfo = activationMap.get( key ) ;
        if( actInfo == null ) {
            return true ;
        }
        return actInfo.getActive() ;
    }

    public final void processRemoteKeyEvent( Key key ) {
        
        this.lastKeyReceivedTime = new Date() ;

        if( !isKeyEnabled( key ) ) {
            throw new IllegalStateException( "Key " + key + " disabled." ) ;
        }
        
        switch( key ) {
            case UP:
                listener.handleUpNavKey() ;
                break ;
            case LEFT:
                listener.handleLeftNavKey() ;
                break ;
            case RIGHT:
                listener.handleRightNavKey() ;
                break ;
            case DOWN:
                listener.handleDownNavKey() ;
                break ;
            case SELECT:
                listener.handleSelectNavKey() ;
                break ;
            case CANCEL:
                listener.handleCancelNavKey() ;
                break ;
            case PLAYPAUSE:
                listener.handlePlayPauseResumeKey() ;
                break ;
            case STOP:
                listener.handleStopKey() ;
                break ;
            case FN_A:
                listener.handleFnAKey() ;
                break ;
            case FN_B:
                listener.handleFnBKey() ;
                break ;
            case FN_C:
                listener.handleFnCKey() ;
                break ;
            case FN_D:
                listener.handleFnDKey() ;
                break ;
            case FN_E:
                listener.handleFnEKey() ;
                break ;
            case FN_F:
                listener.handleFnFKey() ;
                break ;
            case FN_G:
                listener.handleFnGKey() ;
                break ;
            case FN_H:
                listener.handleFnHKey() ;
                break ;
            case SCR_SEL_SHOWHIDE:
                throw new IllegalStateException( "Key processor should not"
                        + "be receiving screenlet show/hide key event." ) ;
        }
    }
    
    public String getDebugState() {
        StringBuffer buffer = new StringBuffer() ;
        buffer.append( "KeyProcessor = " + getName() + ". Activated keys:\n" ) ;
        
        logDebugState( RemoteKeyType.NAV_CONTROL, buffer ) ;
        logDebugState( RemoteKeyType.RUN, buffer ) ;
        logDebugState( RemoteKeyType.FUNCTION, buffer ) ;
        
        return buffer.toString() ;
    }
    
    private void logDebugState( RemoteKeyType keyType, StringBuffer buffer ) {
        Key[] keys = Key.getsKeysOfType( keyType ) ;
        for( Key key : keys ) {
            KeyActivationInfo actInfo = activationMap.get( key ) ;
            buffer.append( StringUtils.rightPad( key.toString(), 15 ) )
                  .append( "[" )
                  .append( StringUtils.rightPad( actInfo.getActive().toString(), 10 ) )
                  .append( "," )
                  .append( StringUtils.rightPad( actInfo.getKeyLabel(), 10 ) )
                  .append( "]" ) ;
                  
        }
    }
    
    public long getTimeSinceLastKeyReceived() {
        if( this.lastKeyReceivedTime == null ) {
            return -1 ;
        }
        return ( new Date().getTime() - lastKeyReceivedTime.getTime() )/1000 ;
    }

    public void clearLastKeyReceivedTime() {
        this.lastKeyReceivedTime = null ;
    }
}
