package com.sandy.sconsole.ui;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Container ;
import java.awt.Cursor ;
import java.awt.Point ;
import java.awt.Toolkit ;
import java.awt.image.BufferedImage ;

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
        hideCursor() ;
        
        contentPane.setBackground( Color.BLACK ) ;
        contentPane.setLayout( new BorderLayout() ) ;
        
        contentPane.add( dayTimePabel, BorderLayout.CENTER ) ;
        
        SwingUtils.setMaximized( this ) ;
    }
    
    // The logic has been copied from a stack exchange post.
    // https://stackoverflow.com/questions/1984071/how-to-hide-cursor-in-a-swing-application
    private void hideCursor() {
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit()
                                    .createCustomCursor( cursorImg, 
                                                         new Point(0, 0), 
                                                         "blank cursor" ) ;

        // Set the blank cursor to the JFrame.
        contentPane.setCursor( blankCursor ) ;        
    }
}
