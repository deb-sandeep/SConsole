package com.sandy.sconsole.screenlet.study;

import static com.sandy.sconsole.SConsole.* ;

import java.awt.* ;
import java.util.* ;

import com.sandy.sconsole.core.screenlet.* ;
import com.sandy.sconsole.dao.entity.* ;
import com.sandy.sconsole.dao.repository.* ;
import com.sandy.sconsole.screenlet.study.large.* ;
import com.sandy.sconsole.screenlet.study.small.* ;

public class StudyScreenlet extends AbstractScreenlet {
    
    private StudyScreenletSmallPanel smallPanel = null ;
    private StudyScreenletLargePanel largePanel = null ;
    
    private LastSessionRepository lastSessionRepo = null ;
    private Session lastSession = null ;
    
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
        smallPanel = new StudyScreenletSmallPanel( this ) ;
        return smallPanel ;
    }

    @Override
    protected ScreenletLargePanel createLargePanel() {
        largePanel = new StudyScreenletLargePanel( this ) ;
        return largePanel ;
    }
    
    @Override
    public void initScreenlet() {
        loadRepositories() ;
        loadLastSession() ;
        
        largePanel.populateLastSessionDetails( lastSession ) ;
    }
    
    private void loadRepositories() {
        lastSessionRepo = getAppContext().getBean( LastSessionRepository.class ) ;
    }
    
    private void loadLastSession() {
        Optional<LastSession> lsOpt = lastSessionRepo.findById( getDisplayName() ) ;
        if( lsOpt.isPresent() ) {
            lastSession = lsOpt.get().getSession() ;
        }
    }
}
