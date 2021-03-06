package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import static com.sandy.sconsole.core.frame.UIConstant.BASE_FONT ;
import static javax.swing.SwingConstants.CENTER ;

import java.awt.* ;
import java.util.Calendar ;

import javax.swing.* ;
import javax.swing.border.LineBorder ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.frame.AbstractDialogPanel ;
import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.util.SecondTickListener ;
import com.sandy.sconsole.screenlet.study.large.tile.control.state.PlayState ;

@SuppressWarnings( "serial" )
public class PauseDialog extends AbstractDialogPanel
    implements SecondTickListener {
    
    static final Logger log = Logger.getLogger( PauseDialog.class ) ;

    private Icon playIcon = null ;
    private Icon stopIcon = null ;
    private JLabel timerLabel = null ;
    private boolean countTime = false ;
    
    private int pauseTime = 0 ;
    
    public PauseDialog( PlayState state ) {
        super( "Session Paused" ) ;

        loadIcons() ;
        setUpUI() ;
        
        keyProcessor.disableAllKeys() ;
        keyProcessor.enableKey( Key.PLAYPAUSE, Key.STOP ) ;
    }
    
    private void loadIcons() {
        playIcon = new ImageIcon( getClass().getResource( "/icons/play.png" ) ) ;
        stopIcon = new ImageIcon( getClass().getResource( "/icons/stop.png"   ) ) ;
    }
    
    private void setUpUI() {
        setPreferredSize( new Dimension( 700, 250 ) ) ;
        setLayout( new BorderLayout() ) ;
        setBackground( Color.GRAY.darker() ) ;
        setBorder( new LineBorder( Color.GRAY, 20 ) ) ;
        
        add( getTimerLabel(), BorderLayout.CENTER ) ;
        add( getButtonPanel(), BorderLayout.SOUTH ) ;
    }
    
    private JPanel getTimerLabel() {
        
        timerLabel = new JLabel() ;
        timerLabel.setFont( BASE_FONT.deriveFont( 120F ) ) ;
        timerLabel.setForeground( Color.CYAN ) ;
        timerLabel.setVerticalAlignment( SwingConstants.CENTER ) ;
        timerLabel.setHorizontalAlignment( SwingConstants.CENTER ) ;
        
        JPanel panel = new JPanel() ;
        panel.setBackground( Color.GRAY ) ;
        panel.setLayout( new BorderLayout() ) ;
        panel.add( timerLabel ) ;
        
        return panel ;
    }
    
    private JPanel getButtonPanel() {
        JPanel panel = new JPanel( new GridLayout( 1, 2 ) ) ;
        panel.add( getButton( playIcon ) ) ;
        panel.add( getButton( stopIcon ) ) ;
        return panel ;
    }
    
    private JPanel getButton( Icon icon ) {
        
        JLabel label = new JLabel() ;
        label.setHorizontalAlignment( CENTER ) ;
        label.setVerticalAlignment( CENTER ) ;
        label.setIcon( icon ) ;
        
        JPanel panel = new JPanel( new BorderLayout() ) ;
        panel.setBackground( Color.GRAY ) ;
        panel.setLayout( new BorderLayout() ) ;
        panel.add( label ) ;
        
        return panel ;
    }

    @Override 
    public void handlePlayPauseResumeKey() {
        super.hideDialog() ;
        SConsole.getApp().postSoftwareRemoteKeyEvent( Key.PLAYPAUSE ) ;
    } ;
    
    @Override public void handleStopKey() {
        super.hideDialog() ;
        SConsole.getApp().postSoftwareRemoteKeyEvent( Key.STOP ) ;
    } ;

    @Override
    public void isBeingMadeVisible() {
        pauseTime = 0 ;
        countTime = true ;
        timerLabel.setText( "00:00:00" ) ;
        SConsole.addSecTimerTask( this ) ;
    } ;
    
    @Override
    public void isBeingHidden() {
        countTime = false ;
        SConsole.removeSecTimerTask( this ) ;
    } ;
    
    @Override
    public void secondTicked( Calendar calendar ) {
        if( countTime ) {
            pauseTime++ ;
            timerLabel.setText( getElapsedTimeLabel() ) ;
        }
    }
    
    @Override
    public Object getReturnValue() {
        return null ;
    }
    
    private String getElapsedTimeLabel() {
        int secs    = (int)(pauseTime) % 60 ;
        int minutes = (int)((pauseTime / 60) % 60) ;
        int hours   = (int)(pauseTime / (60*60)) ;
        return String.format("%02d:%02d:%02d", hours, minutes, secs ) ;
    }
}
