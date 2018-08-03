package com.sandy.sconsole.ui;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Container ;

import javax.swing.JFrame ;

import com.sandy.common.ui.SwingUtils ;
import com.sandy.sconsole.ui.panels.SConsoleBasePanel ;
import com.sandy.sconsole.ui.panels.daytime.DayTimePanel ;

@SuppressWarnings( "serial" )
public class SConsoleFrame extends JFrame {
    
    private Container contentPane = null ;
    
    private SConsoleBasePanel dayTimePabel = new DayTimePanel() ;
    
    public SConsoleFrame() {
        super() ;
        
        contentPane = super.getContentPane() ;
        
        setUpUI() ;
        setVisible( true ) ;
    }
    
    private void setUpUI() {
        
        setUndecorated( true ) ;
        setResizable( false ) ;
        
        contentPane.setBackground( Color.BLACK ) ;
        contentPane.setLayout( new BorderLayout() ) ;
        
        contentPane.add( dayTimePabel, BorderLayout.CENTER ) ;
        
        SwingUtils.setMaximized( this ) ;
    }
}
