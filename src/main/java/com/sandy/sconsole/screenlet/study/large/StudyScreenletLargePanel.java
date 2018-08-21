package com.sandy.sconsole.screenlet.study.large;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;

import javax.swing.JLabel ;

import com.sandy.sconsole.core.screenlet.Screenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;

@SuppressWarnings( "serial" )
public class StudyScreenletLargePanel extends ScreenletLargePanel {

    private StudyScreenlet screenlet = null ;

    public StudyScreenletLargePanel( Screenlet screenlet ) {
        super( screenlet ) ;
        this.screenlet = ( StudyScreenlet )screenlet ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        JLabel label = new JLabel( screenlet.getDisplayName() ) ;
        label.setFont( new Font( "Courier", Font.BOLD, 60 ) ) ;
        label.setForeground( Color.YELLOW ) ;
        label.setHorizontalAlignment( JLabel.CENTER ) ;
        
        add( label, BorderLayout.CENTER ) ;
    }
}
