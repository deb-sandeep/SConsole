package com.sandy.sconsole.screenlet.fragmentation;

import java.awt.BorderLayout ;

import com.sandy.sconsole.core.screenlet.Screenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.FragmentationTile ;

@SuppressWarnings( "serial" )
public class FragmentationLargePanel extends ScreenletLargePanel {
    
    private FragmentationTile fragTile = null ;

    public FragmentationLargePanel( Screenlet parent ) {
        super( parent ) ;
        fragTile = new FragmentationTile( this, true ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        add( fragTile, BorderLayout.CENTER ) ;
    }

    public void highlightSubject( String subjectName ) {
        fragTile.highlightSubject( subjectName ) ;
    }
}
