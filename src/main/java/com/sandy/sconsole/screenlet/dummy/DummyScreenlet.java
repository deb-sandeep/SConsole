package com.sandy.sconsole.screenlet.dummy;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;

import javax.swing.JLabel ;
import javax.swing.SwingUtilities ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.AbstractScreenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.core.screenlet.ScreenletSmallPanel ;

public class DummyScreenlet extends AbstractScreenlet {
    
    private static final Logger log = Logger.getLogger( DummyScreenlet.class ) ;
    private DummyDialog dialog = null ;

    public DummyScreenlet( String name ) {
        super( name ) ;
        dialog = new DummyDialog() ;
    }
    
    public ScreenletSmallPanel createSmallPanel() {
        
        ScreenletSmallPanel panel = new ScreenletSmallPanel( this ) ;
        panel.setBackground( SConsole.BG_COLOR ) ;
        panel.setLayout( new BorderLayout() );
        
        JLabel label = new JLabel( super.getDisplayName() ) ;
        label.setFont( new Font( "Courier", Font.PLAIN, 20 ) ) ;
        label.setForeground( Color.YELLOW ) ;
        label.setHorizontalAlignment( JLabel.CENTER ) ;
        
        panel.add( label, BorderLayout.CENTER ) ;
        
        return panel ;
    }
    
    public ScreenletLargePanel createLargePanel() {

        ScreenletLargePanel panel = new ScreenletLargePanel( this ) ;
        panel.setBackground( Color.GRAY ) ;
        panel.setLayout( new BorderLayout() );
        
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
}
