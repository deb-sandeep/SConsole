package com.sandy.sconsole.screenlet.study.large.tile;

import java.awt.BorderLayout ;
import java.awt.Font ;
import java.util.Calendar ;

import javax.swing.JLabel ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.core.util.SecondTickListener ;

import static com.sandy.sconsole.core.frame.UIConstant.* ;

@SuppressWarnings( "serial" )
public class TimeTile extends AbstractScreenletTile 
    implements SecondTickListener {

    private static Font FONT = BASE_FONT.deriveFont( 40F ) ;
    
    private JLabel label = null ;
    
    public TimeTile( ScreenletPanel mother ) {
        super( mother ) ;
        setUpUI() ;
        SConsole.addSecTimerTask( this ) ;
    }
    
    private void setUpUI() {
        label = getTemplateLabel() ;
        label.setFont( FONT ) ;
        add( label, BorderLayout.CENTER ) ;
    }

    @Override
    public void secondTicked( Calendar calendar ) {
        label.setText( DF_TIME_LG.format( calendar.getTime() ) ) ;
    }
}
