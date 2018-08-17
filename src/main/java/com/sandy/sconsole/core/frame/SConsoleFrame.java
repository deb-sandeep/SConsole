package com.sandy.sconsole.core.frame;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Container ;
import java.awt.Cursor ;
import java.awt.Point ;
import java.awt.Toolkit ;
import java.awt.image.BufferedImage ;

import javax.swing.JFrame ;

import org.apache.log4j.Logger ;

import com.sandy.common.ui.SwingUtils ;
import com.sandy.sconsole.api.remote.KeyPressEvent ;
import com.sandy.sconsole.core.remote.RemoteKeyEventProcessor ;
import com.sandy.sconsole.core.remote.RemoteKeyEventRouter;
import com.sandy.sconsole.core.remote.RemoteKeyReceiver;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;

@SuppressWarnings( "serial" )
public class SConsoleFrame extends JFrame
	implements RemoteKeyReceiver {
    
    @SuppressWarnings( "unused" )
    private static final Logger log = Logger.getLogger( SConsoleFrame.class ) ;
    
    private Container contentPane = null ;
    private ScreenletContainer screenletPanel = null ;
    
    private RemoteKeyEventRouter keyRouter = null ;
    private RemoteKeyEventProcessor keyProcessor = new RemoteKeyEventProcessor() ;
    private ScreenletLargePanel curLargeScreenletPanel = null ;
    private SConsoleDialog dialog = null ;
    
    public SConsoleFrame( RemoteKeyEventRouter keyRouter ) {
        super() ;
        
        this.contentPane = super.getContentPane() ;
        this.screenletPanel = new ScreenletContainer( this ) ;
        this.dialog = new SConsoleDialog() ;
        this.keyRouter = keyRouter ;
        
        this.keyRouter.registerRemoteKeyReceiver( this ) ;
        
        setUpUI() ;
        setVisible( true ) ;
        
        screenletPanel.maximizeDefaultScreenlet() ;
        this.toggleScreenletPanelVisibility() ;
    }
    
    private void setUpUI() {
        
        setUndecorated( true ) ;
        setResizable( false ) ;
        hideCursor() ;
        
        contentPane.setBackground( Color.BLACK ) ;
        contentPane.setLayout( new BorderLayout() ) ;
        
        contentPane.add( screenletPanel, BorderLayout.WEST ) ;
        
        SwingUtils.setMaximized( this ) ;
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
        
        if( event.getBtnType().equals( "ScreenletSelection" ) ) {
            handleScreenletSelectionKeyPress( event.getBtnCode() ) ;
        }
        else {
            keyProcessor.processKeyEvent( event ) ;
        }
    }
    
    public Object showDialog( AbstractDialogPanel panel ) {
        
        dialog.setPanel( panel ) ;
        keyRouter.registerRemoteKeyReceiver( dialog ) ;
        dialog.setVisible( true ) ;
        dialog.setPanel( null ) ;
        keyRouter.registerRemoteKeyReceiver( this ) ;
        
        return panel.getReturnValue() ;
    }
    
    private void handleScreenletSelectionKeyPress( String code ) {

        switch( code ) {
            case "ShowHide":
                toggleScreenletPanelVisibility() ;
                break ;
            default:
                screenletPanel.handleRemoteScreenSelectionEvent( code ) ;
                break ;
        }
    }
    
    private void toggleScreenletPanelVisibility() {
        
        BorderLayout layout = (BorderLayout)contentPane.getLayout() ;
        if( layout.getLayoutComponent( BorderLayout.WEST ) == null ) {
            contentPane.add( screenletPanel, BorderLayout.WEST ) ;
        }
        else {
            contentPane.remove( screenletPanel ) ;
        }
        contentPane.revalidate() ;
        contentPane.repaint() ;
    }

    void setCenterPanel( ScreenletLargePanel largePanel ) {
        
        if( curLargeScreenletPanel != null ) {
            contentPane.remove( curLargeScreenletPanel ) ;
        }
        
        curLargeScreenletPanel = largePanel ;
        keyProcessor.setKeyListener( curLargeScreenletPanel.getScreenlet() ) ;
        
        contentPane.add( curLargeScreenletPanel, BorderLayout.CENTER ) ;
        contentPane.revalidate() ;
        contentPane.repaint() ;
    }
}
