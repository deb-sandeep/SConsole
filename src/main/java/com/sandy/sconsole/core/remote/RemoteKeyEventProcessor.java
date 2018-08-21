package com.sandy.sconsole.core.remote;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.api.remote.KeyPressEvent ;

public class RemoteKeyEventProcessor {

    private static final Logger log = Logger.getLogger( RemoteKeyEventProcessor.class ) ;
    
    private RemoteKeyListener keyListener = null ;
    
    public RemoteKeyEventProcessor() {
    }
    
    public void setKeyListener( RemoteKeyListener listener ) {
        this.keyListener = listener ;
    }
    
    public void processKeyEvent( KeyPressEvent event ) {

        if( this.keyListener == null ) {
            log.warn( "No key listener registered with processor. " + 
                      "Ignoring key event - " + event ) ;
            return ;
        }
        
        switch( event.getBtnType() ) {
            case "Run":
                if( this.keyListener.shouldProcessRunEvents() ) {
                    processRunKey( event.getBtnCode() ) ;
                }
                break ;
            case "NavControl":
                if( this.keyListener.shouldProcessNavEvents() ) {
                    processNavKey( event.getBtnCode() ) ;
                }
                break ;
            case "Function":
                if( this.keyListener.shouldProcessFnEvents() ) {
                    keyListener.handleFunctionKey( event.getBtnCode() ) ;
                }
                break ;
        }
    }
    
    private void processRunKey( String btnCode ) {

        if( btnCode.equals( "PlayPause" ) ) {
            keyListener.processPlayPauseResumeKey() ;
        }
        else if( btnCode.equals( "Stop" ) ) {
            keyListener.processStopKey() ;
        }
    }
    
    private void processNavKey( String btnCode ) {
        
        switch( btnCode ) {
            case "Up":
                keyListener.handleUpNavKey() ;
                break ;
            case "Down":
                keyListener.handleDownNavKey() ;
                break ;
            case "Right":
                keyListener.handleRightNavKey() ;
                break ;
            case "Left":
                keyListener.handleLeftNavKey() ;
                break ;
            case "Select":
                keyListener.handleSelectNavKey() ;
                break ;
        }
    }
}
