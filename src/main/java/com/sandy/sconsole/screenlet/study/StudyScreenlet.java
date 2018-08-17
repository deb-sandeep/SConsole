package com.sandy.sconsole.screenlet.study;

import com.sandy.sconsole.core.screenlet.AbstractScreenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.core.screenlet.ScreenletSmallPanel ;

public class StudyScreenlet extends AbstractScreenlet {

    public StudyScreenlet( String displayName ) {
        super( displayName ) ;
    }

    @Override
    protected ScreenletSmallPanel createSmallPanel() {
        return null ;
    }

    @Override
    protected ScreenletLargePanel createLargePanel() {
        return null ;
    }
}
