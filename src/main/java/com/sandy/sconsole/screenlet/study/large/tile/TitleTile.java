package com.sandy.sconsole.screenlet.study.large.tile;

import static com.sandy.sconsole.core.frame.UIConstant.SCREENLET_TITLE_FONT ;

import java.awt.BorderLayout ;
import java.awt.Color ;

import javax.swing.JLabel ;

import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;

@SuppressWarnings( "serial" )
public class TitleTile extends AbstractScreenletTile {
    
    public TitleTile( ScreenletPanel mother ) {
        super( mother, false ) ;
        StudyScreenlet parent = ( StudyScreenlet )mother.getScreenlet() ;
        String subjectName = parent.getDisplayName() ;
        Color fgColor = parent.getSubjectColor( subjectName ) ;
        setUpUI( fgColor ) ;
    }
    
    private void setUpUI( Color fgColor ) {

        JLabel label = getTemplateLabel() ;
        label.setForeground( fgColor ) ;
        label.setFont( SCREENLET_TITLE_FONT ) ;
        label.setText( getScreenlet().getDisplayName() );
        
        add( label, BorderLayout.CENTER ) ;
    }
}
