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
        si.session = null ;
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
        
        Optional<LastSession> lsOpt = lastSessionRepo.findById( getSubjectName() ) ;
        if( lsOpt.isPresent() ) {
            si.session = lsOpt.get().getSession().clone() ;
            log.debug( "Populating UI based on last session" ) ;
            log.debug( si.session.toString() ) ;
            
            // At this point the session still has information which is not
            // suited for the blank - like last lap details, session details,
            // outcome details etc. We use the information for display and
            // then dis-embowel them out.
            super.populateUIBasedOnSessionInfo( si ) ;
            
            // Disemboweling out the unnecessary details. Note that the following
            // attributes are not cleared - sessionType, topic, book, problem
            si.session.setId( null ) ;
            si.session.setStartTime( null ) ;
            si.session.setEndTime( null ) ;
            si.session.setDuration( 0 ) ;
            si.session.setAbsoluteDuration( 0 );
            si.session.setNumSkipped( 0 );
            si.session.setNumSolved( 0 );
            si.session.setNumRedo( 0 );
            si.session.setNumPigeon( 0 ) ;
            si.session.setNumIgnored( 0 ) ;
        }
        else {
            si.session = new Session() ;
            super.populateUIBasedOnSessionInfo( si ) ;
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
     * collecting the payload. 
     * 
     * Home state can transition to either the change state or play state.
     * In either case, we pass the session information as the payload on 
     * which the next states can operate upon.
     * 
     * Also note that it is guaranteed that the PLAY transition will not be 
     * activated till the session information is valid and complete for
     * starting a session.
     */
    public Object getTransitionOutPayload( State nextState, Key key ) {
        if( nextState.getName().equals( ChangeState.NAME ) || 
            nextState.getName().equals( PlayState.NAME ) ) {
            return this.si ;
        }
        return null ;
    }
}
