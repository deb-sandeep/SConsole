package com.sandy.sconsole.screenlet.study;

import java.awt.Color ;

import com.sandy.sconsole.core.screenlet.AbstractScreenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.core.screenlet.ScreenletSmallPanel ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.small.StudyScreenletSmallPanel ;

public class StudyScreenlet extends AbstractScreenlet {
    
    public Color getSubjectColor( String subjectName ) {
        if( subjectName.contains( "Physics" ) ) {
            return Color.GREEN.darker().darker() ;
        }
        else if( subjectName.contains( "Chemistry" ) ) {
            return Color.YELLOW.darker().darker() ;
        }
        else if( subjectName.contains( "Maths" ) ) {
            return Color.RED.darker().darker() ;
        }
        return Color.GRAY ;
    }

    public StudyScreenlet( String subjectName ) {
        super( subjectName ) ;
    }

    @Override
    protected ScreenletSmallPanel createSmallPanel() {
        return new StudyScreenletSmallPanel( this ) ;
    }

    @Override
    protected ScreenletLargePanel createLargePanel() {
        return new StudyScreenletLargePanel( this ) ;
    }
}
