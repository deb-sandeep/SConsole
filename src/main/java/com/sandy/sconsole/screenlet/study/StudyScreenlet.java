package com.sandy.sconsole.screenlet.study;

import java.awt.Color ;

import com.sandy.sconsole.core.screenlet.AbstractScreenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.core.screenlet.ScreenletSmallPanel ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.small.StudyScreenletSmallPanel ;

public class StudyScreenlet extends AbstractScreenlet {
    
    public static final String IIT_PHYSICS = "IIT - Physics" ;
    public static final String IIT_CHEM    = "IIT - Chemistry" ;
    public static final String IIT_MATHS   = "IIT - Maths" ;
    
    private StudyScreenletSmallPanel smallPanel = null ;
    private StudyScreenletLargePanel largePanel = null ;
    
    public static Color getSubjectColor( String subjectName ) {
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
        smallPanel = new StudyScreenletSmallPanel( this ) ;
        return smallPanel ;
    }

    @Override
    protected ScreenletLargePanel createLargePanel() {
        largePanel = new StudyScreenletLargePanel( this ) ;
        return largePanel ;
    }
}
