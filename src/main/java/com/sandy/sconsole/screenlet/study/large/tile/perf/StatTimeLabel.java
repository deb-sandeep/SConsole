package com.sandy.sconsole.screenlet.study.large.tile.perf;

import java.awt.Color ;
import java.awt.Font ;

import javax.swing.JLabel ;

import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.util.SConsoleUtil ;

@SuppressWarnings( "serial" )
class StatTimeLabel extends JLabel {

    private String header = null ;
    
    StatTimeLabel( Color fgColor, String header ) {
        setHorizontalAlignment( LEFT ) ;
        setVerticalAlignment( CENTER ) ;
        setOpaque( true ) ;
        setBackground( UIConstant.BG_COLOR ) ;
        setForeground( fgColor ) ;
        setFont( UIConstant.BASE_FONT.deriveFont( Font.PLAIN, 30 ) ) ;
        this.header = header ;
    }
    
    public void clear() {
        setText( "" ) ;
    }
    
    public void setDuration( int seconds ) {
        setText( this.header + SConsoleUtil.getElapsedTimeLabel( seconds, false ) ) ;
    }
}
