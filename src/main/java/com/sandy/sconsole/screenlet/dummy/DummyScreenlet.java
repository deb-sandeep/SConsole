package com.sandy.sconsole.screenlet.dummy;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;

import javax.swing.JLabel ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.AbstractScreenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.core.screenlet.ScreenletSmallPanel ;

public class DummyScreenlet extends AbstractScreenlet {

    public DummyScreenlet( String name ) {
        super( name ) ;
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
}
