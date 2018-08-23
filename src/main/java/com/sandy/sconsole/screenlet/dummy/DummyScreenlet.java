package com.sandy.sconsole.screenlet.dummy;

import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;

import java.awt.* ;

import javax.swing.* ;

import org.apache.log4j.* ;

import com.sandy.sconsole.core.remote.* ;
import com.sandy.sconsole.core.screenlet.* ;

public class DummyScreenlet extends AbstractScreenlet {
    
    private static final Logger log = Logger.getLogger( DummyScreenlet.class ) ;
    private DummyDialog dialog = null ;

    public DummyScreenlet( String name ) {
        super( name ) ;
        dialog = new DummyDialog() ;
        kaMgr.disableAllKeys() ;
        kaMgr.enableKey( true, RUN_PLAYPAUSE, RUN_STOP ) ;
        kaMgr.enableFnKey( FN_A, new FnKeyHandler() {
            public void process() { handleFnA(); } 
        } );
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
    
    public void run() {
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
