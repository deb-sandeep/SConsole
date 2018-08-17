package com.sandy.sconsole.core.frame;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Container ;
import java.awt.Cursor ;
import java.awt.Dimension ;
import java.awt.Point ;
import java.awt.Toolkit ;
import java.awt.image.BufferedImage ;

import javax.swing.JDialog;

import org.apache.log4j.Logger ;

import com.sandy.common.ui.SwingUtils ;
import com.sandy.sconsole.api.remote.KeyPressEvent ;
import com.sandy.sconsole.core.remote.RemoteKeyEventProcessor ;
import com.sandy.sconsole.core.remote.RemoteKeyReceiver;

@SuppressWarnings( "serial" )
class SConsoleDialog extends JDialog
	implements RemoteKeyReceiver {
    
    @SuppressWarnings( "unused" )
    private static final Logger log = Logger.getLogger( SConsoleDialog.class ) ;
    
    private Container contentPane = null ;
    private RemoteKeyEventProcessor keyProcessor = new RemoteKeyEventProcessor() ;
    private AbstractDialogPanel panel = null ;
    
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
        
        contentPane.setBackground( Color.BLACK ) ;
        contentPane.setLayout( new BorderLayout() ) ;
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

    public void handleRemoteKeyEvent( KeyPressEvent event ) {
        if( !event.getBtnType().equals( "ScreenletSelection" ) ) {
            keyProcessor.processKeyEvent( event ) ;
        }
    }
    
    void setPanel( AbstractDialogPanel panel ) {
        
        if( this.panel != null ) {
            contentPane.remove( this.panel ) ;
            this.panel.setParentDialog( null ) ;
        }
        
        this.panel = panel ;
        this.keyProcessor.setKeyListener( panel ) ;
        
        if( this.panel != null ) {
            this.panel.setParentDialog( this ) ;
            contentPane.add( this.panel, BorderLayout.CENTER ) ;
            
            Dimension prefSize = this.panel.getPreferredSize() ;
            setSize( prefSize ) ;
            SwingUtils.centerOnScreen( this, (int)prefSize.getWidth(), 
                                             (int)prefSize.getHeight() ) ;
        }

    }
}
