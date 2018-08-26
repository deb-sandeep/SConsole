package com.sandy.sconsole.screenlet.dummy;

import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;
import static com.sandy.sconsole.core.screenlet.Screenlet.RunState.* ;

import java.awt.* ;

import javax.swing.* ;

import org.apache.log4j.* ;

import com.sandy.sconsole.* ;
import com.sandy.sconsole.api.remote.* ;
import com.sandy.sconsole.core.remote.* ;
import com.sandy.sconsole.core.screenlet.* ;

public class DummyScreenlet extends AbstractScreenlet {
    
    static final Logger log = Logger.getLogger( DummyScreenlet.class ) ;
    
    private DummyDialog dialog = null ;
    private RemoteKeyEventProcessor keyProcessor = null ;
    private RemoteController controller = null ;
    
    public DummyScreenlet( String name ) {
        super( name ) ;
        
        controller = SConsole.getAppContext()
                             .getBean( RemoteController.class ) ;
        
        keyProcessor = new RemoteKeyEventProcessor( "Dummy Screenlet", 
            new RemoteKeyListenerAdapter() {
                public void handlePlayPauseResumeKey() {
                    if( getRunState() == RUNNING ) { pause() ; }
                    else if( getRunState() == STOPPED ) { play() ; }
                    else if( getRunState() == PAUSED ) { resume() ; }
                }
                
                public void handleStopKey() {
                    stop() ;
                }
            }
        ) ;
        
        dialog = new DummyDialog() ;
        
        keyProcessor.disableAllKeys() ;
        keyProcessor.setKeyEnabled( true, RUN_PLAYPAUSE, RUN_STOP, FN_A ) ;
        keyProcessor.setFnHandler( FN_A, new Handler() {
            public void handle() { handleFnA(); } 
        } ) ;
    }
    
    @Override public void isBeingMaximized() {
        log.debug( "Dummy screenlet is being maximized. Taking control of keys." ) ;
        super.isBeingMaximized() ;
        controller.pushKeyProcessor( keyProcessor ) ;
    }

    @Override public void isBeingMinimized() {
        log.debug( "Dummy screenlet is being minimized. Releasing control of keys." ) ;
        super.isBeingMinimized() ;
        controller.popKeyProcessor() ;
    }

    public ScreenletSmallPanel createSmallPanel() {
        
        ScreenletSmallPanel panel = new ScreenletSmallPanel( this ) ;
        
        JLabel label = new JLabel( super.getDisplayName() ) ;
        label.setFont( new Font( "Courier", Font.PLAIN, 20 ) ) ;
        label.setForeground( Color.YELLOW ) ;
        label.setHorizontalAlignment( JLabel.CENTER ) ;
        
        panel.add( label, BorderLayout.CENTER ) ;
        
        return panel ;
    }
    
    public ScreenletLargePanel createLargePanel() {

        ScreenletLargePanel panel = new ScreenletLargePanel( this ) ;
        
        JLabel label = new JLabel( super.getDisplayName() ) ;
        label.setFont( new Font( "Courier", Font.BOLD, 60 ) ) ;
        label.setForeground( Color.YELLOW ) ;
        label.setHorizontalAlignment( JLabel.CENTER ) ;
        
        panel.add( label, BorderLayout.CENTER ) ;
        
        return panel ;
    }
    
    public void play() {
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                log.debug( "Running dummy screenlet" ) ;
                Object retVal = showDialog( dialog ) ;
                log.debug( "Return value = " + retVal ) ;
            }
        } ) ;
    }

    public void pause() {
        log.debug( "Pausing dummy screenlet" ) ;
    }
    
    public void resume() {
        log.debug( "Resuming dummy screenlet" ) ;
    }
    
    public void stop() {
        log.debug( "Stopping dummy screenlet" ) ;
    }

    public void handleFnA() {
        System.exit( -1 ) ;
    }
}
