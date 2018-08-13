package com.sandy.sconsole.core.remote;

import com.sandy.sconsole.api.remote.KeyPressEvent ;
import com.sandy.sconsole.core.remote.RemoteKeyListener.RunState ;

public class RemoteKeyEventProcessor {

    private RemoteKeyListener keyListener = null ;
    
    public RemoteKeyEventProcessor() {
    }
    
    public void setKeyListener( RemoteKeyListener listener ) {
        this.keyListener = listener ;
    }
    
    public void processKeyEvent( KeyPressEvent event ) {
        
        switch( event.getBtnType() ) {
            case "Run":
                processRunKey( event.getBtnCode() ) ;
                break ;
            case "NavControl":
                processNavKey( event.getBtnCode() ) ;
                break ;
            case "Function":
                keyListener.handleFunctionKey( event.getBtnCode() ) ;
                break ;
        }
    }
    
    private void processRunKey( String btnCode ) {

        if( btnCode.equals( "PlayPause" ) ) {
            switch( keyListener.getCurrentRunState() ) {
                case RUNNING:
                    keyListener.pause() ;
                    keyListener.setCurrentRunState( RunState.PAUSED ) ;
                    break ;
                case PAUSED:
                    keyListener.resume() ;
                    keyListener.setCurrentRunState( RunState.RUNNING ) ;
                    break ;
                case STOPPED:
                    keyListener.run() ;
                    keyListener.setCurrentRunState( RunState.RUNNING ) ;
                    break ;
            }
        }
        else if( btnCode.equals( "Stop" ) ) {
            if( keyListener.getCurrentRunState() != RunState.STOPPED ) {
                keyListener.stop() ;
                keyListener.setCurrentRunState( RunState.STOPPED ) ; 
            }
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
