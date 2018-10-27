package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.dao.entity.Session.SessionType ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTileUI.Btn2Type ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionInformation ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.* ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.BookSelectionDialog.BookSelectionListener ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.ProblemSelectionDialog.ProblemSelectionListener ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.SessionTypeSelectionDialog.SessionTypeSelectionListener ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.TopicSelectionDialog.TopicSelectionListener ;

public class ChangeState extends BaseControlTileState 
    implements SessionTypeSelectionListener, TopicSelectionListener, 
               BookSelectionListener, ProblemSelectionListener {

    static final Logger log = Logger.getLogger( ChangeState.class ) ;
    
    public static final String NAME = "Change" ;
    
    private SessionTypeSelectionDialog typeSelectionDialog    = null ;
    private TopicSelectionDialog       topicSelectionDialog   = null ;
    private BookSelectionDialog        bookSelectionDialog    = null ;
    private ProblemSelectionDialog     problemSelectionDialog = null ;
    
    public ChangeState( SessionControlTile tile, StudyScreenletLargePanel screenletPanel ) {
        super( NAME, tile, screenletPanel ) ;
        addTransition( Key.FN_A, "Type",    this ) ;
        addTransition( Key.FN_B, "Topic",   this ) ;
        addTransition( Key.FN_C, "Book",    this ) ;
        addTransition( Key.FN_D, "Problem", this ) ;

        typeSelectionDialog    = new SessionTypeSelectionDialog( this ) ;
        topicSelectionDialog   = new TopicSelectionDialog( this ) ;
        bookSelectionDialog    = new BookSelectionDialog( this ) ;
        problemSelectionDialog = new ProblemSelectionDialog( this ) ;
    }
    
    public SessionInformation getSessionInfo() {
        return this.si ;
    }
    
    public String getSubjectName() {
        return super.getSubjectName() ;
    }
    
    public Topic getDefaultTopic() {
        return this.si.session.getTopic() ;
    }
    
    public Book getDefaultBook() {
        return this.si.session.getBook() ;
    }
    
    public Problem getDefaultProblem() {
        return this.si.session.getLastProblem() ;
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
            this.si.session.setDuration( 0 ) ;
            this.si.session.setAbsoluteDuration( 0 ) ;
            
            highlightKeyPanelsAndActivateTransitions() ;
            
            return false ;
        }
        else {
            // If this is a self transition, let the key processor called the
            // demultiplexed function for this transition key press
            return true ;
        }
    }
    
    /**
     * This function is called before transitioning to the next state for 
     * collecting the payload. 
     * 
     * Change state can transition to either the Play state or Home state.
     * In case we are transitioning ot the play state, we pass the session 
     * information as the payload.
     * 
     * Also note that it is guaranteed that the PLAY transition will not be 
     * activated till the session information is valid and complete for
     * starting a session.
     */
    public Object getTransitionOutPayload( State nextState, Key key ) {
        if( nextState.getName().equals( PlayState.NAME ) ) {
            return this.si ;
        }
        else if( nextState.getName().equals( HomeState.NAME ) && 
                 key == Key.SELECT ) {
            // If we are returning to the home state, because the user has
            // selected the change, we show the control panel populated with 
            // the changed choices.
            return this.si ;
        }
        return null ;
    }
    
    private void highlightKeyPanelsAndActivateTransitions() {
        
        hideMessage() ;
        
        // Enabling play pause by default. 
        enableTransition( Key.PLAYPAUSE ) ;
        
        // 1. Session Type and Topic change transitions
        tile.typeLbl.setBackground( UIConstant.FN_A_COLOR ) ;
        tile.topicLbl.setBackground( UIConstant.FN_B_COLOR ) ;
        enableTransition( Key.FN_A, Key.FN_B ) ;
        
        // 2. Book and Problem change transitions
        if( this.si.session.getSessionType() == SessionType.EXERCISE ) {
            
            tile.bookLbl.setBackground( UIConstant.FN_C_COLOR ) ;
            tile.problemLbl.setBackground( UIConstant.FN_D_COLOR ) ;
            
            if( si.session.getTopic() == null ) {
                disableTransition( Key.FN_C, Key.FN_D ) ;
            }
            else {
                enableTransition( Key.FN_C ) ;
                if( si.session.getBook() == null ) {
                    disableTransition( Key.FN_D ) ;
                }
                else {
                    enableTransition( Key.FN_D ) ;
                }
            }
        }
        else {
            tile.bookLbl.setBackground( UIConstant.BG_COLOR ) ;
            tile.problemLbl.setBackground( UIConstant.BG_COLOR ) ;
            disableTransition( Key.FN_C, Key.FN_D ) ;
        }
        
        // 3. Cancel transition
        enableTransition( Key.CANCEL ) ;
        tile.setBtn2UI( Btn2Type.CANCEL ) ;
        
        // 4. Select transition
        enableTransition( Key.SELECT ) ;
        
        // 5. Play transition
        super.processPlayReadiness( si ) ;
    }

    /* This method is called when the state machine is about to execute a 
     * transition from this state. We take this opportunity to do some
     * cleanup work - for example, removing the change highlights.
     * 
     * NOTE that this method is also called during self transitions. Hence
     * before removing ourselves from the second listener list, we must check
     * if the next state is not a self transition.
     */
    @Override
    public void deactivate( State nextState, Key key ) {
        super.deactivate( nextState, key ) ;
        if( nextState != this ) {
            tile.clearChangeUIHighlights() ;
        }
    }

    /**
     * Called for changing SessionType
     */
    @Override
    public void handleFnAKey() {
        showDialog( typeSelectionDialog ) ;
    }
    
    /**
     * This method is called asynchronous by {@link SessionTypeSelectionDialog}
     * when the user finishes his interaction with the dialog.
     */
    public void handleNewSessionTypeSelection( SessionType type ) {
        
        if( type != null ) {
            si.session.setSessionType( type ) ;
            if( type != SessionType.EXERCISE ) {
                si.session.setBook( null ) ;
                si.session.setLastProblem( null ) ;
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
        showDialog( topicSelectionDialog ) ;
    }
    
    /**
     * This method is called asynchronous by {@link TopicSelectionDialog}
     * when the user finishes his interaction with the dialog.
     */
    public void handleNewTopicSelection( Topic topic ) {
        
        if( topic != null ) {
            si.session.setTopic( topic ) ;
            if( si.session.getSessionType() == SessionType.EXERCISE ) {
                // Get a list of books which has problems for the topic chosen
                List<Integer> bookIds = bookRepo.findProblemBooksForTopic( topic.getId() ) ;
                
                // Pick the first book and fetch problems for that book
                if( !bookIds.isEmpty() ) {
                    si.session.setBook( bookRepo.findById( bookIds.get( 0 ) ).get() ) ;
                    super.populateProblem( si ) ;
                }
            }
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
        showDialog( bookSelectionDialog ) ;
    }
    
    /**
     * This method is called asynchronous by {@link BookSelectionDialog}
     * when the user finishes his interaction with the dialog.
     */
    public void handleNewBookSelection( Book book ) {
        
        if( book != null ) {
            si.session.setBook( book ) ;
        }
        super.populateUIBasedOnSessionInfo( si ) ;
        highlightKeyPanelsAndActivateTransitions() ;
    }
    
    /**
     * Called for changing Problem. This will be called only if the session we
     * are dealing with is of an exercise type
     */
    @Override
    public void handleFnDKey() {
        showDialog( problemSelectionDialog ) ;
    }
    
    /**
     * This method is called asynchronous by {@link ProblemSelectionDialog}
     * when the user finishes his interaction with the dialog.
     */
    public void handleNewProblemSelection( Problem problem ) {
        
        if( problem != null ) {
            si.session.setLastProblem( problem ) ;
            int index = si.unsolvedProblems.indexOf( problem ) ;
            
            if( index > 0 ) {
                List<Problem> list = new ArrayList<Problem>() ;
                for( int i=0; i<index; i++ ) {
                    list.add( si.unsolvedProblems.get( i ) ) ;
                }
                si.unsolvedProblems.removeAll( list ) ;
                si.unsolvedProblems.addAll( list ) ;
            }
        }
        tile.setProblemLabel( problem ) ;
        highlightKeyPanelsAndActivateTransitions() ;
    }
    
}
