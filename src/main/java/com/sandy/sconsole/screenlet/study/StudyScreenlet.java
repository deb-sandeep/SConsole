package com.sandy.sconsole.screenlet.study;

import com.sandy.sconsole.core.screenlet.AbstractScreenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.core.screenlet.ScreenletSmallPanel ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.small.StudyScreenletSmallPanel ;

public class StudyScreenlet extends AbstractScreenlet {

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
