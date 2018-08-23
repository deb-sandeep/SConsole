package com.sandy.sconsole.core.remote;

import org.apache.log4j.* ;

import com.sandy.sconsole.api.remote.* ;

public class RemoteKeyEventProcessor {

    private static final Logger log = Logger.getLogger( RemoteKeyEventProcessor.class ) ;
    
    private RemoteListener remoteListener = null ;
    
    public void setRemoteListener( RemoteListener listener ) {
        this.remoteListener = listener ;
    }
    
    public void processKeyEvent( KeyEvent event ) {

        if( this.remoteListener == null ) {
            log.warn( "No remote listener registered with processor. " + 
                      "Ignoring key event - " + event ) ;
            return ;
        }
        else if( !this.remoteListener.isKeyActive( event.getKeyId() ) ) {
            log.debug( "Key " + event.getKeyId() + " deactivated. Ignoring." ) ;
            return ;
        }
        
        log.debug( "Processing key " + event.getKeyId() ) ;
        switch( event.getBtnType() ) {
            case "Run":
                processRunKey( event.getBtnCode() ) ;
                break ;
            case "NavControl":
                processNavKey( event.getBtnCode() ) ;
                break ;
            case "Function":
                remoteListener.handleFunctionKey( event.getBtnCode() ) ;
                break ;
        }
    }
    
    private void processRunKey( String btnCode ) {

        if( btnCode.equals( "PlayPause" ) ) {
            remoteListener.processPlayPauseResumeKey() ;
        }
        else if( btnCode.equals( "Stop" ) ) {
            remoteListener.processStopKey() ;
        }
    }
    
    private void processNavKey( String btnCode ) {
        
        switch( btnCode ) {
            case "Up":
                remoteListener.handleUpNavKey() ;
                break ;
            case "Down":
                remoteListener.handleDownNavKey() ;
                break ;
            case "Right":
                remoteListener.handleRightNavKey() ;
                break ;
            case "Left":
                remoteListener.handleLeftNavKey() ;
                break ;
            case "Select":
                remoteListener.handleSelectNavKey() ;
                break ;
        }
    }
}
