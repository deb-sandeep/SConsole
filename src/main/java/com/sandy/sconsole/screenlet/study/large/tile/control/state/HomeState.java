package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import java.util.Optional ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.dao.entity.LastSession ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.Btn2Type ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionInformation ;

public class HomeState extends BaseControlTileState {

    private static final Logger log = Logger.getLogger( HomeState.class ) ;
    
    public static final String NAME = "Home" ;
    
    private SessionInformation si = new SessionInformation() ;
    
    public HomeState( SessionControlTile tile, StudyScreenletLargePanel screenletPanel ) {
        super( NAME, tile, screenletPanel ) ;
    }
    
    @Override
    public void resetState() {
        si.sessionBlank = null ;
        si.unsolvedProblems = null ;
    }

    @Override
    public boolean preActivate( Object payload, State fromState, Key key ) {
        
        tile.cleanControlPanel() ;
        hideMessage() ;
        
        // Note that by default all transitions are deactivated to start with.
        
        // Every time we transition to the home screen, we populate a fresh
        // session blank - prepopulated with the details of the last session.
        // This way, the user can start a new session with the details of the
        // previous one.
        
        Optional<LastSession> lsOpt = lastSessionRepo.findById( getSubject() ) ;
        if( lsOpt.isPresent() ) {
            si.sessionBlank = lsOpt.get().getSession().clone() ;
            
            // At this point the session still has information which is not
            // suited for the blank - like last lap details, session details,
            // outcome details etc. We use the information for display and
            // then dis-embowel them out.
            super.populateUIBasedOnSessionInfo( si ) ;
            
            // Disemboweling out the unnecessary details. Note that the following
            // attributes are not cleared - sessionType, topic, book, problem
            si.sessionBlank.setId( null ) ;
            si.sessionBlank.setStartTime( null ) ;
            si.sessionBlank.setEndTime( null ) ;
            si.sessionBlank.setDuration( 0 ) ;
            si.sessionBlank.setAbsoluteDuration( 0 );
            si.sessionBlank.setNumSkipped( 0 );
            si.sessionBlank.setNumSolved( 0 );
            si.sessionBlank.setNumRedo( 0 );
            si.sessionBlank.setNumPigeon( 0 );
        }
        else {
            si.sessionBlank = new Session() ;
        }
        
        log.debug( "Validating session details and activating play button" ) ;
        highlightKeyPanelsAndActivateTransitions() ;
        
        return false ;
    }

    private void highlightKeyPanelsAndActivateTransitions() {
        
        super.processPlayReadiness( si ) ;
        
        tile.setBtn2UI( Btn2Type.CHANGE ) ;
        super.enableTransition( Key.FN_A ) ;
    }

    /**
     * This function is called before transitioning to the next state for 
     * collecting the payload. Subclasses can use the parameters to 
     * decide on the appropriate payload which will be passed to the 
     * next state.
     */
    public Object getTransitionOutPayload( State nextState, Key key ) {
        if( nextState.getName().equals( ChangeState.NAME ) ) {
            return this.si ;
        }
        return null ;
    }
}
