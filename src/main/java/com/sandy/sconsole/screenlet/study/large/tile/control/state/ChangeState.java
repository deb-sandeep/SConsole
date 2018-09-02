package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import javax.swing.SwingUtilities ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.remote.RemoteController ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.dao.entity.Session.SessionType ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.Btn2Type ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionInformation ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.* ;

public class ChangeState extends BaseControlTileState {

    private static final Logger log = Logger.getLogger( ChangeState.class ) ;
    
    public static final String NAME = "Change" ;
    
    private RemoteController controller = null ;
    
    private BookChangeDialog        bookChangeDialog    = null ;
    private TopicChangeDialog       topicChangeDialog   = null ;
    private ProblemChangeDialog     problemChangeDialog = null ;
    private SessionTypeChangeDialog typeChangeDialog    = null ;
    
    private SessionInformation si = null ;
    
    public ChangeState( SessionControlTile tile, StudyScreenletLargePanel screenletPanel ) {
        super( NAME, tile, screenletPanel ) ;
        addTransition( Key.FN_A, "Type",    this ) ;
        addTransition( Key.FN_B, "Topic",   this ) ;
        addTransition( Key.FN_C, "Book",    this ) ;
        addTransition( Key.FN_D, "Problem", this ) ;

        controller = ctx.getBean( RemoteController.class ) ;

        typeChangeDialog    = new SessionTypeChangeDialog( this ) ;
        topicChangeDialog   = new TopicChangeDialog( this ) ;
    }
    
    public SessionInformation getSessionInfo() {
        return this.si ;
    }
    
    @Override
    public void resetState() {
        this.si = null ;
        disableTransition( Key.FN_A, Key.FN_B, Key.FN_C, Key.FN_D ) ;
    }

    // We can be assured that the UI of the panels are appropriately 
    // rendered - invalid ones are highlighted etc. 
    @Override
    public boolean preActivate( Object payload, State fromState, Key key ) {
        
        // If this activation is not due to a self transition, then only do this
        if( fromState != this ) {
            if( (payload == null) || !(payload instanceof SessionInformation) ) {
                throw new IllegalArgumentException( 
                        "ChangeState activation payload is null or is not of "
                        + "type SessionInformation" ) ;
            }
            
            this.si = ( SessionInformation )payload ;
            
            highlightKeyPanelsAndActivateTransitions() ;
            
            return false ;
        }
        else {
            // If this is a self transition, let the key processor called the
            // demultiplexed function for this transition key press
            return true ;
        }
    }
    
    private void highlightKeyPanelsAndActivateTransitions() {
        
        // 1. Session Type and Topic change transitions
        tile.typeLbl.setBackground( UIConstant.FN_A_COLOR ) ;
        tile.topicLbl.setBackground( UIConstant.FN_B_COLOR ) ;
        enableTransition( Key.FN_A, Key.FN_B ) ;
        
        // 2. Book and Problem change transitions
        if( this.si.sessionBlank.getSessionType() == SessionType.EXERCISE ) {
            tile.bookLbl.setBackground( UIConstant.FN_C_COLOR ) ;
            tile.problemLbl.setBackground( UIConstant.FN_D_COLOR ) ;
            enableTransition( Key.FN_C, Key.FN_D ) ;
        }
        else {
            tile.bookLbl.setBackground( UIConstant.BG_COLOR ) ;
            tile.problemLbl.setBackground( UIConstant.BG_COLOR ) ;
            disableTransition( Key.FN_C, Key.FN_D ) ;
        }
        
        // 3. Cancel transition
        enableTransition( Key.CANCEL ) ;
        tile.setBtn2UI( Btn2Type.CANCEL ) ;
        
        // 4. Play transition
        super.processPlayReadiness( si ) ;
    }

    /**
     * Called for changing SessionType
     */
    @Override
    public void handleFnAKey() {
        log.debug( "FN_A key received in change state. Changing session type." ) ;
        
        controller.pushKeyProcessor( typeChangeDialog.getKeyProcessor() ) ;
        
        SwingUtilities.invokeLater( new Runnable() { public void run() {
            SConsole.getApp()
                    .getFrame()
                    .showDialog( typeChangeDialog ) ;
        }});
    }
    
    /**
     * This method is called asynchronous by {@link SessionTypeChangeDialog}
     * when the user finishes his interaction with the dialog.
     */
    public void handleNewSessionTypeSelection( SessionType type ) {
        
        log.debug( "New session type chosen = " + type ) ;
        
        if( type != null ) {
            si.sessionBlank.setSessionType( type ) ;
            si.sessionBlank.setAbsoluteDuration( 0 ) ;
            si.sessionBlank.setDuration( 0 ) ;
            
            if( type != SessionType.EXERCISE ) {
                si.sessionBlank.setBook( null ) ;
                si.sessionBlank.setLastProblem( null ) ;
            }
        }
        super.populateUIBasedOnSessionInfo( si ) ;
        highlightKeyPanelsAndActivateTransitions() ;
    }
    
    /**
     * Called for changing Topic
     */
    @Override
    public void handleFnBKey() {
        log.debug( "FN_B key received in change state. Changing topic." ) ;
        
        controller.pushKeyProcessor( topicChangeDialog.getKeyProcessor() ) ;
        
        SwingUtilities.invokeLater( new Runnable() { public void run() {
            SConsole.getApp()
                    .getFrame()
                    .showDialog( topicChangeDialog ) ;
        }});
    }
    
    /**
     * This method is called asynchronous by {@link TopicChangeDialog}
     * when the user finishes his interaction with the dialog.
     */
    public void handleNewTopicSelection( Topic topic ) {
        
        log.debug( "New topic chosen = " + topic ) ;
        
        if( topic != null ) {
            si.sessionBlank.setTopic( topic ) ;
            si.sessionBlank.setAbsoluteDuration( 0 ) ;
            si.sessionBlank.setDuration( 0 ) ;
        }
        super.populateUIBasedOnSessionInfo( si ) ;
        highlightKeyPanelsAndActivateTransitions() ;
    }
    
    /**
     * Called for changing Book. This will be called only if the session we
     * are dealing with is of an exercise type
     */
    @Override
    public void handleFnCKey() {
        log.debug( "FN_C key received in change state. Changing book" ) ;
    }
    
    /**
     * Called for changing Problem. This will be called only if the session we
     * are dealing with is of an exercise type
     */
    @Override
    public void handleFnDKey() {
        log.debug( "FN_D key received in change state. Changing problem" ) ;
    }
}
