package com.sandy.sconsole.core.frame;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Container ;
import java.awt.Cursor ;
import java.awt.Point ;
import java.awt.Toolkit ;
import java.awt.image.BufferedImage ;
import java.io.File ;
import java.io.IOException ;
import java.lang.reflect.InvocationTargetException ;
import java.text.SimpleDateFormat ;
import java.util.Date ;

import javax.imageio.ImageIO ;
import javax.swing.JFrame ;
import javax.swing.SwingUtilities ;

import org.apache.log4j.Logger ;

import com.sandy.common.ui.SwingUtils ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionInformation ;

@SuppressWarnings( "serial" )
public class SConsoleFrame extends JFrame {
    
    static final Logger log = Logger.getLogger( SConsoleFrame.class ) ;
    
    static final SimpleDateFormat SDF = new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" ) ;
    
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
        
        if( SConsole.getConfig().isShowSwingApp() ) {
            setVisible( true ) ;
        }
        else {
            log.info( "[*** IMP ***] Application has been configured not to show the Swing UI" ) ;
        }
        
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
        
        if( SwingUtils.getScreenWidth() >= 1920 ) {
            int height = SwingUtils.getScreenHeight() >= 1080 ? 
                                   1080 : SwingUtils.getScreenHeight() ;
            this.setBounds( 0, 0, 1920, height ) ;
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
    
    public String captureScreenshot( SessionInformation si ) 
        throws InterruptedException, InvocationTargetException {
        
        File file = getImgSaveFile( si ) ;
        
        SwingUtilities.invokeAndWait( new Runnable() {
            @Override
            public void run() {
                BufferedImage img = new BufferedImage( getWidth(), 
                                                        getHeight(), 
                                                        BufferedImage.TYPE_INT_RGB ) ;
                paint( img.getGraphics() ) ;
                    
                try {
                    ImageIO.write( img, "png", file ) ;
                }
                catch( IOException e ) {
                    log.error( "Unable to save image - " + 
                    file.getAbsolutePath(), e ) ;
                }
            }
        } ) ;
        
        return file.getName() ;
    }
    
    private File getImgSaveFile( SessionInformation si ) {
        
        StringBuffer fileName = new StringBuffer( "screenshot_" ) ;
        fileName.append( SDF.format( new Date() ) ) ;
        
        if( si != null ) {
            fileName.append( "_" )
                    .append( si.session.getTopic().getSubject().getName() )
                    .append( "_" )
                    .append( si.session.getSessionType() )
                    .append( "_" )
                    .append( si.session.getTopic().getTopicName() ) ;
        }
        
        fileName.append( ".png" ) ;
        
        File file = new File( SConsole.SCREENSHOT_DIR, fileName.toString() ) ;
        return file ;
    }
}
