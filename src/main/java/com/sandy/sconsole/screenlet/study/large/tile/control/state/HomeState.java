package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import java.util.Optional ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.dao.entity.LastSession ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.dao.entity.Session.SessionType ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.Btn1Type ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.Btn2Type ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.OutcomeButtonsState ;

public class HomeState extends BaseControlTileState {

    private static final Logger log = Logger.getLogger( HomeState.class ) ;
    
    private Session sessionBlank = null ;
    
    public HomeState( SessionControlTile tile ) {
        super( "Home", tile ) ;
    }
    
    @Override
    public void resetState() {
        this.sessionBlank = null ;
    }

    @Override
    public boolean preActivate( Object payload, State fromState, Key key ) {
        
        tile.cleanControlPanel() ;
        
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
            populateUILastSessionDetails( sessionBlank ) ;
            
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

    private void populateUILastSessionDetails( Session ls ) {
        
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
            tile.setProblemLabel( ls.getLastProblem() ) ;
            
            tile.updateNumSkippedLabel( ls.getNumSkipped() ) ;
            tile.updateNumSolvedLabel( ls.getNumSolved() ) ;
            tile.updateNumRedoLabel( ls.getNumRedo() ) ;
            tile.updateNumPigeonLabel( ls.getNumPigeon() ) ;
            
            tile.setOutcomeButtonsState( OutcomeButtonsState.INACTIVE ) ;
        }
        else {
            tile.setOutcomeButtonsState( OutcomeButtonsState.HIDDEN ) ;
        }
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
        
        tile.setBtn2( Btn2Type.CHANGE ) ;
    }
}
