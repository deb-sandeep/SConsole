package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import java.util.List ;
import java.util.Optional ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.dao.entity.LastSession ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.dao.entity.Session.SessionType ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.Btn1Type ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.Btn2Type ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.OutcomeButtonsState ;

public class HomeState extends BaseControlTileState {

    private static final Logger log = Logger.getLogger( HomeState.class ) ;
    
    private Session sessionBlank = null ;
    private List<Problem> unsolvedProblems = null ;
    
    public HomeState( SessionControlTile tile, StudyScreenletLargePanel screenletPanel ) {
        super( "Home", tile, screenletPanel ) ;
    }
    
    @Override
    public void resetState() {
        this.sessionBlank = null ;
        this.unsolvedProblems = null ;
    }

    @Override
    public boolean preActivate( Object payload, State fromState, Key key ) {
        
        tile.cleanControlPanel() ;
        hideMessage() ;
        
        // Deactivate the play transition out. We will enable if only if
        // the critical session data is available and valid.
        super.disableTransition( Key.PLAYPAUSE ) ;
        
        // Every time we transition to the home screen, we populate a fresh
        // session blank - prepopulated with the details of the last session.
        // This way, the user can start a new session with the details of the
        // previous one.
        
        Optional<LastSession> lsOpt = lastSessionRepo.findById( getSubject() ) ;
        if( lsOpt.isPresent() ) {
            sessionBlank = lsOpt.get().getSession().clone() ;
            
            // At this point the session still has information which is not
            // suited for the blank - like last lap details, session details,
            // outcome details etc. We use the information for display and
            // then dis-embowel them out.
            populateUIBasedOnLastSessionDetails( sessionBlank ) ;
            
            // Disemboweling out the unnecessary details. Note that the following
            // attributes are not cleared - sessionType, topic, book, problem
            sessionBlank.setId( null ) ;
            sessionBlank.setStartTime( null ) ;
            sessionBlank.setEndTime( null ) ;
            sessionBlank.setDuration( 0 ) ;
            sessionBlank.setAbsoluteDuration( 0 );
            sessionBlank.setNumSkipped( 0 );
            sessionBlank.setNumSolved( 0 );
            sessionBlank.setNumRedo( 0 );
            sessionBlank.setNumPigeon( 0 );
        }
        else {
            sessionBlank = new Session() ;
        }
        
        log.debug( "Validating session details and activating play button" ) ;
        validateSessionDetailsAndActivateTransitions() ;
        
        return false ;
    }

    private void populateUIBasedOnLastSessionDetails( Session ls ) {
        
        log.debug( "Populating last session details" ) ;
        log.debug( ls ) ;
        
        SessionType sessionType = ls.getSessionType() ;
        
        log.debug( "Copying values of session type, topic and session time" ) ;
        tile.setSessionTypeIcon( sessionType ) ;
        tile.setTopicLabel( ls.getTopic() ) ;
        tile.updateSessionTimeLabel( ls.getDuration() ) ;
        
        if( sessionType == SessionType.EXERCISE ) {
            
            log.debug( "Last sesion was an exercise. Populate exercise details." );
            tile.setBookLabel( ls.getBook() ) ;
            tile.updateNumSkippedLabel( ls.getNumSkipped() ) ;
            tile.updateNumSolvedLabel( ls.getNumSolved() ) ;
            tile.updateNumRedoLabel( ls.getNumRedo() ) ;
            tile.updateNumPigeonLabel( ls.getNumPigeon() ) ;
            
            populateProblem( ls.getTopic(), ls.getBook(), ls.getLastProblem() ) ;
            
            tile.setOutcomeButtonsState( OutcomeButtonsState.INACTIVE ) ;
        }
        else {
            tile.setOutcomeButtonsState( OutcomeButtonsState.HIDDEN ) ;
        }
    }
    
    private void populateProblem( Topic topic, Book book, Problem lastProblem ) {
        
        unsolvedProblems = super.loadUnsolvedProblems( topic, book, lastProblem ) ;
        if( !unsolvedProblems.isEmpty() ) {
            sessionBlank.setLastProblem( unsolvedProblems.get( 0 ) ) ;
        }
        else {
            sessionBlank.setLastProblem( null ) ;
        }
        
        tile.updateNumProblemsLeftInBookLabel( unsolvedProblems.size() ) ;
        tile.setProblemLabel( sessionBlank.getLastProblem() ) ;
    }
    
    private void validateSessionDetailsAndActivateTransitions() {
        
        boolean readyToPlay = true ;
        
        if( sessionBlank.getSessionType() == null ) {
            readyToPlay = false ;
            tile.invalidateSessionTypePanel() ;
        }
        
        if( sessionBlank.getTopic() == null ) {
            readyToPlay = false ;
            tile.invalidateTopicPanel() ;
        }
        
        if( sessionBlank.getBook() == null ) {
            readyToPlay = false ;
            tile.invalidateBookPanel() ; 
        }
        
        if( sessionBlank.getLastProblem() == null ) {
            readyToPlay = false ;
            tile.invalidateProblemPanel() ; 
        }
        
        if( readyToPlay ) {
            tile.setBtn1( Btn1Type.PLAY ) ;
            super.enableTransition( Key.PLAYPAUSE ) ;
        }
        else {
            showMessage( "Play will be enabled after the required attributes (" + 
                         "highlighted with red border) are changed." ) ;
        }
        
        tile.setBtn2( Btn2Type.CHANGE ) ;
    }
}
