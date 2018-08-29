package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import static com.sandy.sconsole.core.frame.UIConstant.* ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;
import static com.sandy.sconsole.dao.entity.Session.* ;
import static javax.swing.SwingConstants.* ;

import java.awt.* ;

import javax.swing.* ;

import org.apache.log4j.* ;

import com.sandy.sconsole.core.frame.* ;
import com.sandy.sconsole.core.remote.* ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;

@SuppressWarnings( "serial" )
public class SessionTypeChangeDialog extends AbstractDialogPanel {
    
    static final Logger log = Logger.getLogger( SessionTypeChangeDialog.class ) ;
    
    private String sessionType = null ;
    
    private Icon exerciseIcon = null ;
    private Icon theoryIcon   = null ;
    private Icon lectureIcon  = null ;
    private Icon cancelIcon   = null ;
    
    private SessionControlTile control = null ;
    

    public SessionTypeChangeDialog( SessionControlTile control ) {
        super( "Session Type Change" ) ;
        
        this.control = control ;
        
        setUpUI() ;
        keyProcessor.disableAllKeys() ;
        keyProcessor.setKeyEnabled( true, FN_A, FN_B, FN_C, FN_CANCEL ) ;
        keyProcessor.setFnHandler( FN_A, new Handler( "Exercise" ) {
            public void handle() { setSessionType( TYPE_EXERCISE ) ; }
        } ) ;
        keyProcessor.setFnHandler( FN_B, new Handler( "Theory" ) {
            public void handle() { setSessionType( TYPE_THEORY ) ; }
        } ) ;
        keyProcessor.setFnHandler( FN_C, new Handler( "Lecture" ) {
            public void handle() { setSessionType( TYPE_LECTURE ) ; }
        } ) ;
        keyProcessor.setFnHandler( FN_CANCEL, new Handler( "" ) {
            public void handle() { setSessionType( null ) ; }
        } ) ;
    }
    
    private void setSessionType( String type ) {
        this.sessionType = type ;
        super.hideDialog() ;
        control.handleNewSessionTypeSelection( type ) ;
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
