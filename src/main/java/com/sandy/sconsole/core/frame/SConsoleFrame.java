package com.sandy.sconsole.core.frame;

import java.awt.* ;
import java.awt.image.* ;

import javax.swing.* ;

import org.apache.log4j.* ;

import com.sandy.common.ui.* ;
import com.sandy.sconsole.core.screenlet.* ;

@SuppressWarnings( "serial" )
public class SConsoleFrame extends JFrame {
    
    @SuppressWarnings( "unused" )
    private static final Logger log = Logger.getLogger( SConsoleFrame.class ) ;
    
    private Container contentPane = null ;
    private ScreenletContainer screenletPanel = null ;
    
    private ScreenletLargePanel curLargeScreenletPanel = null ;
    private SConsoleDialog dialog = null ;
    
    public SConsoleFrame() {
        super() ;
        
        this.contentPane = super.getContentPane() ;
        this.screenletPanel = new ScreenletContainer( this ) ;
        this.dialog = new SConsoleDialog() ;
        
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
        
        if( SwingUtils.getScreenWidth() > 1920 ) {
            SwingUtils.centerOnScreen( this, 1920, 1080 ) ;
        }
        else {
            SwingUtils.setMaximized( this ) ;
        }
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

    public Object showDialog( AbstractDialogPanel panel ) {
        
        dialog.setPanel( panel ) ;
        dialog.setVisible( true ) ;
        dialog.setPanel( null ) ;
        
        return panel.getReturnValue() ;
    }
    
    void setCenterPanel( ScreenletLargePanel largePanel ) {
        
        if( curLargeScreenletPanel != null ) {
            contentPane.remove( curLargeScreenletPanel ) ;
        }
        
        curLargeScreenletPanel = largePanel ;
        contentPane.add( curLargeScreenletPanel, BorderLayout.CENTER ) ;
        contentPane.revalidate() ;
        contentPane.repaint() ;
    }

    public void handleScreenletSelectionEvent( String btnCode ) {
        switch( btnCode ) {
            case "ShowHide":
                toggleScreenletPanelVisibility() ;
                break ;
            default:
                screenletPanel.handleScreenletSelectionEvent( btnCode ) ;
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
}
