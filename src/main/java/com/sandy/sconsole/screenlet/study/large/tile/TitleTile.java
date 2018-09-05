package com.sandy.sconsole.screenlet.study.large.tile;

import static com.sandy.sconsole.core.frame.UIConstant.* ;

import java.awt.* ;

import javax.swing.* ;

import com.sandy.sconsole.core.screenlet.* ;
import com.sandy.sconsole.screenlet.study.* ;

@SuppressWarnings( "serial" )
public class TitleTile extends AbstractScreenletTile {
    
    public TitleTile( ScreenletPanel mother ) {
        
        super( mother, false ) ;
        StudyScreenlet parent = ( StudyScreenlet )mother.getScreenlet() ;
        String subjectName = parent.getDisplayName() ;
        
        Color fgColor = StudyScreenlet.getSubjectColor( subjectName ) ;
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
