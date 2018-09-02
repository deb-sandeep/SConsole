package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import static com.sandy.sconsole.core.frame.UIConstant.* ;
import static javax.swing.SwingConstants.CENTER ;

import java.awt.* ;

import javax.swing.* ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.frame.AbstractDialogPanel ;
import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.dao.entity.Session.SessionType ;
import com.sandy.sconsole.screenlet.study.large.tile.control.state.ChangeState ;

@SuppressWarnings( "serial" )
public class SessionTypeChangeDialog extends AbstractDialogPanel {
    
    static final Logger log = Logger.getLogger( SessionTypeChangeDialog.class ) ;
    
    private SessionType sessionType = null ;
    
    private Icon exerciseIcon = null ;
    private Icon theoryIcon   = null ;
    private Icon lectureIcon  = null ;
    private Icon cancelIcon   = null ;
    
    private ChangeState changeState = null ;

    public SessionTypeChangeDialog( ChangeState changeState ) {
        super( "Session Type Change" ) ;
        
        this.changeState = changeState ;
        setUpUI() ;
        keyProcessor.disableAllKeys() ;
        keyProcessor.enableKey( Key.FN_A, "Exercise" ) ;
        keyProcessor.enableKey( Key.FN_B, "Theory" ) ;
        keyProcessor.enableKey( Key.FN_C, "Lecture" ) ;
        keyProcessor.enableKey( Key.CANCEL ) ;
    }
    
    @Override
    public void handleFnAKey() { setSessionType( SessionType.EXERCISE ) ; }

    @Override
    public void handleFnBKey() { setSessionType( SessionType.THEORY ) ; }

    @Override
    public void handleFnCKey() { setSessionType( SessionType.LECTURE ) ; }

    @Override
    public void handleCancelNavKey() { setSessionType( null ) ; }

    private void setSessionType( SessionType type ) {
        this.sessionType = type ;
        super.hideDialog() ;
        changeState.handleNewSessionTypeSelection( type ) ;
    }
     
    private void setUpUI() {
        loadIcons() ;
        setPreferredSize( new Dimension( 760, 250 ) ) ;
        setLayout( new GridLayout( 1, 4 ) ) ;
        add( getFnButton( FN_A_COLOR, "Exercise [A]", exerciseIcon ) ) ;
        add( getFnButton( FN_B_COLOR, "Theory [B]",   theoryIcon ) ) ;
        add( getFnButton( FN_C_COLOR, "Lecture [C]",  lectureIcon ) ) ;
        add( getFnButton( BG_COLOR,   "Cancel",   cancelIcon ) ) ;
    }

    private void loadIcons() {
        exerciseIcon = new ImageIcon( getClass().getResource( "/icons/exercise.png" ) ) ;
        theoryIcon   = new ImageIcon( getClass().getResource( "/icons/theory.png"   ) ) ;
        lectureIcon  = new ImageIcon( getClass().getResource( "/icons/lecture.png"  ) ) ;
        cancelIcon   = new ImageIcon( getClass().getResource( "/icons/cancel.png"     ) ) ;
    }
    
    @Override
    public Object getReturnValue() {
        return this.sessionType ;
    }

    protected JPanel getFnButton( Color bgColor, String text, Icon icon ) {
        
        JLabel iconLabel = new JLabel() ;
        iconLabel.setIcon( icon ) ;
        iconLabel.setHorizontalAlignment( CENTER ) ;
        iconLabel.setVerticalAlignment( CENTER ) ;
        
        JLabel textLabel = new JLabel( text ) ;
        textLabel.setFont( FNBTN_FONT ) ;
        textLabel.setForeground( Color.WHITE ) ;
        textLabel.setHorizontalAlignment( CENTER ) ;
        textLabel.setVerticalAlignment( CENTER ) ;
        
        JPanel panel = new JPanel( new BorderLayout() ) ;
        panel.setBackground( bgColor ) ;
        panel.add( iconLabel ) ;
        panel.add( textLabel, BorderLayout.SOUTH ) ;
        panel.setBorder( BorderFactory.createLineBorder( BG_COLOR.brighter().brighter(), 15 ) ) ;
        
        return panel ;
    }
}
