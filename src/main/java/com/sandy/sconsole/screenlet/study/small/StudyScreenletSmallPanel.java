package com.sandy.sconsole.screenlet.study.small;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;

import javax.swing.JLabel ;

import com.sandy.sconsole.core.screenlet.ScreenletSmallPanel ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;

@SuppressWarnings( "serial" )
public class StudyScreenletSmallPanel extends ScreenletSmallPanel {
    

    public StudyScreenletSmallPanel( StudyScreenlet screenlet ) {
        super( screenlet ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        JLabel label = new JLabel( getScreenlet().getDisplayName() ) ;
        label.setFont( new Font( "Courier", Font.PLAIN, 20 ) ) ;
        label.setForeground( Color.YELLOW ) ;
        label.setHorizontalAlignment( JLabel.CENTER ) ;
        
        add( label, BorderLayout.CENTER ) ;
    }
}
