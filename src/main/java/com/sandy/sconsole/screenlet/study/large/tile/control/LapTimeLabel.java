package com.sandy.sconsole.screenlet.study.large.tile.control;

import static com.sandy.sconsole.core.frame.UIConstant.BG_COLOR ;

import java.awt.Color ;
import java.awt.Graphics ;
import java.awt.Graphics2D ;
import java.awt.Paint ;

import javax.swing.JLabel ;

@SuppressWarnings( "serial" )
public class LapTimeLabel extends JLabel {
    
    private long projectedDuration = 0 ;
    private long currentLapTime = 0 ;

    public LapTimeLabel() {
        setForeground( Color.DARK_GRAY ) ;
        setHorizontalAlignment( CENTER ) ;
        setVerticalAlignment( CENTER ) ;
        setVisible( true ) ;
        setOpaque( false ) ;
    }
    
    public void reset() {
        projectedDuration = 0 ;
        currentLapTime = 0 ;
        repaint() ;
    }
    
    public void setLapTime( long seconds ) {
        
        currentLapTime = seconds ;
        setText( seconds < 0 ? "" : getElapsedTimeLabel( seconds, false ) ) ;        
    }
    
    public void setProjectedDuration( long seconds ) {
        projectedDuration = seconds ;
        repaint() ;
    }

    public void paintComponent( Graphics g ) {
        
      int width = getWidth() ;
      int height = getHeight() ;
      
      Graphics2D g2d = ( Graphics2D )g ;
      Paint oldPaint = g2d.getPaint() ;

      g2d.setPaint( BG_COLOR ) ;
      g2d.fillRect( 0, 0, width, height );

      if( projectedDuration > 0 ) {
          
          float pctCompletion = ((float)currentLapTime)/projectedDuration ;
          
          if( pctCompletion <= 0.8 ) {
              g2d.setPaint( Color.decode( "#2e7d32" ).darker() ) ;
          }
          else if( pctCompletion > 0.8 && pctCompletion <= 1 ) {
              g2d.setPaint( Color.decode( "#e65100" ).darker() ) ;
          }
          else {
              g2d.setPaint( Color.decode( "#dd2c00" ).darker() ) ;
          }
          
          int w = (int)(width * pctCompletion) ;
          w = ( w > width ) ? width : w ;
          
          g2d.fillRect( 0, 0, w, height ) ;
      }
      
      g2d.setPaint( oldPaint );
      super.paintComponent( g ) ;
    }

    private String getElapsedTimeLabel( long seconds, boolean longFormat ) {
        int secs    = (int)(seconds) % 60 ;
        int minutes = (int)((seconds / 60) % 60) ;
        int hours   = (int)(seconds / (60*60)) ;
        
        if( longFormat ) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs ) ;
        }
        return String.format("%02d:%02d", minutes, secs ) ;
    }
}
