package com.sandy.sconsole.screenlet.study.large.tile.control.dialog.renderer;

import static com.sandy.sconsole.core.frame.UIConstant.BASE_FONT ;

import java.awt.Color ;
import java.awt.Font ;

import javax.swing.JLabel ;
import javax.swing.JList ;
import javax.swing.ListCellRenderer ;
import javax.swing.border.EmptyBorder ;

@SuppressWarnings( "serial" )
public abstract class AbstractChangeListCellRenderer<T> extends JLabel
        implements ListCellRenderer<T> {

    protected static final Font LBL_FONT = BASE_FONT.deriveFont( 30F ) ;
    
    protected void doBaseDecoration( JList<? extends T> list,
                                     T value, 
                                     int index, 
                                     boolean isSelected, 
                                     boolean cellHasFocus ) {
        
        setFont( LBL_FONT ) ;
        setBorder( new EmptyBorder( 5, 0, 5, 0 ) );
        if( isSelected ) {
            setForeground( Color.CYAN ) ;
        }
        else {
            setForeground( Color.GRAY ) ;
        }
    }
}
