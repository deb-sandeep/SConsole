package com.sandy.sconsole.core.frame;

import static com.sandy.sconsole.core.frame.UIConstant.* ;
import static javax.swing.ScrollPaneConstants.* ;

import java.awt.* ;
import java.util.List ;

import javax.swing.* ;

import org.apache.log4j.* ;

import com.sandy.common.ui.* ;
import com.sandy.sconsole.* ;
import com.sandy.sconsole.core.screenlet.* ;

@SuppressWarnings( "serial" )
public class ScreenletContainer extends JPanel {

    private static final Logger log = Logger.getLogger( ScreenletContainer.class ) ;
    
    private static final int SCREENLET_HEIGHT = 150 ;
    
    private SConsoleFrame frame          = null ;
    private JScrollPane   scrollPane     = new JScrollPane() ;
    private int           preferredWidth = 100 ;
    private Dimension     screenletDim   = null ;
    
    private List<Screenlet> screenlets = null ;
    private Screenlet       currentlyMaximizedScreenlet = null ;
    
    public ScreenletContainer( SConsoleFrame frame ) {
        super( true ) ;
        setUpUI() ;
        this.frame = frame ;
    }
    
    private void setUpUI() {

        scrollPane.setHorizontalScrollBarPolicy( HORIZONTAL_SCROLLBAR_NEVER ) ;
        scrollPane.setVerticalScrollBarPolicy( VERTICAL_SCROLLBAR_NEVER ) ;
        scrollPane.add( this ) ;
        
        preferredWidth = (int)(SwingUtils.getScreenWidth() * 0.15) ;
        screenletDim   = new Dimension( preferredWidth, SCREENLET_HEIGHT ) ;
        
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) ) ;
        setPreferredSize( new Dimension( preferredWidth, 100 ) ) ;
        setBackground( BG_COLOR ) ;
        
        this.screenlets = SConsole.getApp().getScreenlets() ;
        addScreenletSmallScreens() ;
    }
    
    private void addScreenletSmallScreens() {
        
        for( Screenlet screenlet : screenlets ) {
            log.debug( "Registering screenlet - " + screenlet.getDisplayName() ) ;
            
            ScreenletSmallPanel panel = screenlet.getSmallPanel() ;
            panel.setMinimumSize( screenletDim ) ;
            panel.setPreferredSize( screenletDim ) ;
            panel.setMaximumSize( screenletDim ) ;
            
            add( panel ) ;
        }
        add( Box.createVerticalGlue() ) ;
    }
    
    public void maximizeDefaultScreenlet() {
        if( !screenlets.isEmpty() ) {
            maximizeScreenlet( screenlets.get( 0 ) );
        }
    }
    
    private void maximizeScreenlet( Screenlet screenlet ) {
        
        if( currentlyMaximizedScreenlet == screenlet ) {
            log.debug( "Screenlet " + screenlet.getDisplayName() + 
                       " is already maximized." );
            return ;
        }
        
        if( currentlyMaximizedScreenlet != null ) {
            currentlyMaximizedScreenlet.isBeingMinimized() ;
        }
        
        currentlyMaximizedScreenlet = screenlet ;
        currentlyMaximizedScreenlet.isBeingMaximized() ;
        
        frame.setCenterPanel( currentlyMaximizedScreenlet.getLargePanel() ) ;
    }
    
    public void handleScreenletSelectionEvent( String screenId ) {
        
        int index = Integer.parseInt( screenId ) ;
        if( screenlets.size() >= index ) {
            Screenlet screenlet = screenlets.get( index-1 ) ;
            
            log.debug( "Maximizing screenlet " + screenlet.getDisplayName() ) ;
            maximizeScreenlet( screenlet ) ;
        }
    }
}
