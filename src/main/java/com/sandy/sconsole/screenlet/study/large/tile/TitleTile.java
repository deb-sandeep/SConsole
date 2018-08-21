package com.sandy.sconsole.screenlet.study.large.tile;

import static com.sandy.sconsole.core.frame.UIConstant.SCREENLET_TITLE_FONT ;

import java.awt.BorderLayout ;
import java.awt.Color ;

import javax.swing.JLabel ;

import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;

@SuppressWarnings( "serial" )
public class TitleTile extends AbstractScreenletTile {
    
    private static Color FG_COLOR  = Color.decode( "#E8DF00" ) ;
    
    public TitleTile( ScreenletPanel mother ) {
        super( mother ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {

        JLabel label = getTemplateLabel() ;
        label.setForeground( FG_COLOR ) ;
        label.setFont( SCREENLET_TITLE_FONT ) ;
        label.setText( getScreenlet().getDisplayName() );
        
        add( label, BorderLayout.CENTER ) ;
    }
}
