package com.sandy.sconsole.screenlet.study.small;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;

import javax.swing.JLabel ;

import com.sandy.sconsole.core.screenlet.Screenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletSmallPanel ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;

@SuppressWarnings( "serial" )
public class StudyScreenletSmallPanel extends ScreenletSmallPanel {
    
    private StudyScreenlet screenlet = null ;

    public StudyScreenletSmallPanel( Screenlet screenlet ) {
        super( screenlet ) ;
        this.screenlet = ( StudyScreenlet )screenlet ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        JLabel label = new JLabel( screenlet.getDisplayName() ) ;
        label.setFont( new Font( "Courier", Font.PLAIN, 20 ) ) ;
        label.setForeground( Color.YELLOW ) ;
        label.setHorizontalAlignment( JLabel.CENTER ) ;
        
        add( label, BorderLayout.CENTER ) ;
        
    }
}
