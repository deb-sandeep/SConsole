package com.sandy.sconsole.screenlet.study.large.tile.perf;

import static com.sandy.sconsole.core.frame.UIConstant.BG_COLOR ;

import java.awt.Color ;
import java.awt.Font ;
import java.awt.Graphics ;
import java.awt.Graphics2D ;
import java.awt.Paint ;

import javax.swing.JLabel ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.util.SConsoleUtil ;

@SuppressWarnings( "serial" )
class PerfProgressBar extends JLabel {
    
    static final Logger log = Logger.getLogger( PerfProgressBar.class ) ;
    
    private int average = 0 ;
    private int pctile80 = 0 ;
    private int runDuration = 0 ;
    
    float secondsPerPixel = 0 ;
    
    public PerfProgressBar() {
        setForeground( Color.DARK_GRAY ) ;
        setHorizontalAlignment( CENTER ) ;
        setVerticalAlignment( CENTER ) ;
        setVisible( true ) ;
        setOpaque( false ) ;
        setForeground( Color.GRAY ) ;
        setFont( UIConstant.BASE_FONT.deriveFont( Font.PLAIN, 50 ) ) ;
    }
    
    public void setMarkers( int average, int pctile80 ) {
        this.average = average ;
        this.pctile80 = pctile80 ;
    }
    
    public void updateRunDuration( int duration ) {
        
        this.runDuration = duration ;
        if( this.runDuration == 0 ) {
            setText( "" ) ;
        }
        else {
            setText( SConsoleUtil.getElapsedTimeLabel( runDuration, false ) ) ;
        }
        super.repaint() ;
    }

    public void paintComponent( Graphics g ) {
        
        int width = getWidth() ;
        int height = getHeight() ;
        
        Graphics2D g2d = ( Graphics2D )g ;
        Paint oldPaint = g2d.getPaint() ;

        g2d.setPaint( BG_COLOR ) ;
        g2d.fillRect( 0, 0, width, height ) ;
        
        secondsPerPixel = calculateSecondsPerPixel() ;
        
        if( runDuration <= average ) {
            g2d.setPaint( Color.decode( "#2e7d32" ).darker() ) ;
        }
        else if( runDuration > average && runDuration <= pctile80 ) {
            g2d.setPaint( Color.decode( "#e65100" ).darker() ) ;
        }
        else {
            g2d.setPaint( Color.decode( "#dd2c00" ).darker().darker() ) ;
        }
        
        if( runDuration > 0 ) {
            int w = (int)( runDuration * secondsPerPixel ) ;
            g2d.fillRect( 0, 0, w, height ) ;
        }
        
        paintMarker( g2d, average, Color.GREEN.darker() ) ;
        paintMarker( g2d, pctile80, Color.RED ) ;
        
        g2d.setPaint( oldPaint );
        super.paintComponent( g ) ;
    }
    
    private void paintMarker( Graphics2D g2d, int value, Color color ) {

        if( value > 0 ) {
            int x = (int)( value * secondsPerPixel ) ;
            g2d.setPaint( color ) ;
            g2d.drawRect( x, 0, 2, getHeight()-1 ) ;
        }
    }
    
    private float calculateSecondsPerPixel() {
        
        int width = getWidth() ;
        int maxValue = Math.max( pctile80, runDuration ) ;
        
        maxValue = ( maxValue == 0 ) ? 300 : maxValue + 5 ;
        return ((float)width)/maxValue ;
    }
}
