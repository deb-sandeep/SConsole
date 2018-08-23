package com.sandy.sconsole.core.frame;

import java.awt.* ;
import java.awt.image.* ;

import javax.swing.* ;
import javax.swing.border.* ;

import org.apache.log4j.* ;

import com.sandy.common.ui.* ;
import com.sandy.sconsole.api.remote.* ;
import com.sandy.sconsole.core.remote.* ;

@SuppressWarnings( "serial" )
class SConsoleDialog extends JDialog
	implements RemoteKeyReceiver {
    
    private static final Logger log = Logger.getLogger( SConsoleDialog.class ) ;
    
    public static final Font  TITLE_FONT   = new Font( "Courier", Font.PLAIN, 25 ) ;
    public static final Color TITLE_COLOR  = Color.decode( "#DFDBBB" ) ;
    public static final Color BORDER_COLOR = Color.decode( "#807E72" ) ;
    public static final Color BG_COLOR     = Color.decode( "#4A4942" ) ;
    
    private Container contentPane = null ;
    private RemoteKeyEventProcessor keyProcessor = new RemoteKeyEventProcessor() ;
    private AbstractDialogPanel panel = null ;
    
    private JPanel bodyPanel = null ;
    private JLabel titleLabel = null ;
    
    public SConsoleDialog() {
        super() ;
        this.contentPane = super.getContentPane() ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        setUndecorated( true ) ;
        setResizable( false ) ;
        setModal( true ) ;
        hideCursor() ;
        
        titleLabel = getTitleLabel() ;
        
        bodyPanel = new JPanel() ;
        bodyPanel.setLayout( new BorderLayout() ) ;
        bodyPanel.setBackground( BG_COLOR ) ;
        bodyPanel.setBorder( new LineBorder( BORDER_COLOR ) ) ;
        bodyPanel.add( titleLabel, BorderLayout.NORTH ) ;
        
        contentPane.setBackground( BG_COLOR ) ;
        contentPane.setLayout( new BorderLayout() ) ;
        contentPane.add( bodyPanel, BorderLayout.CENTER ) ;
    }
    
    private JLabel getTitleLabel() {
        JLabel label = new JLabel() ;
        label.setForeground( TITLE_COLOR ) ;
        label.setBackground( BG_COLOR ) ;
        label.setHorizontalAlignment( SwingConstants.CENTER ) ; 
        label.setVerticalAlignment( SwingConstants.CENTER ) ;
        label.setFont( TITLE_FONT ) ;
        
        return label ;
    }
    
    // The logic has been copied from a stack exchange post.
    // https://stackoverflow.com/questions/1984071/how-to-hide-cursor-in-a-swing-application
    private void hideCursor() {
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit()
                                    .createCustomCursor( cursorImg, 
                                                         new Point(0, 0), 
                                                         "blank cursor" ) ;
        // Set the blank cursor to the JFrame.
        contentPane.setCursor( blankCursor ) ;
    }

    public void handleRemoteKeyEvent( KeyEvent event ) {
        if( !event.getBtnType().equals( "ScreenletSelection" ) ) {
            log.debug( "Dialog has received the key - " + event ) ;
            keyProcessor.processKeyEvent( event ) ;
        }
    }
    
    void setPanel( AbstractDialogPanel panel ) {
        
        if( this.panel != null ) {
            bodyPanel.remove( this.panel ) ;
            titleLabel.setText( "" ) ;
            this.panel.setParentDialog( null ) ;
        }
        
        this.panel = panel ;
        this.keyProcessor.setRemoteListener( panel ) ;
        
        if( this.panel != null ) {
            
            this.panel.setParentDialog( this ) ;
            bodyPanel.add( this.panel, BorderLayout.CENTER ) ;
            titleLabel.setText( this.panel.getName() ) ;
            
            Dimension prefSize = this.panel.getPreferredSize() ;
            setSize( prefSize ) ;
            SwingUtils.centerOnScreen( this, (int)prefSize.getWidth(), 
                                             (int)prefSize.getHeight() ) ;
        }

    }
}
