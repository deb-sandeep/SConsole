package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import static com.sandy.sconsole.core.frame.UIConstant.* ;

import java.awt.* ;

import javax.swing.* ;
import javax.swing.border.* ;

import com.sandy.sconsole.dao.entity.master.* ;

@SuppressWarnings( "serial" )
public class TopicChangeListCellRenderer extends JLabel
        implements ListCellRenderer<Topic> {

    private static final Font LBL_FONT = BASE_FONT.deriveFont( 30F ) ;
    
    @Override
    public Component getListCellRendererComponent( JList<? extends Topic> list,
                                                   Topic value, 
                                                   int index, 
                                                   boolean isSelected, 
                                                   boolean cellHasFocus ) {
        setFont( LBL_FONT ) ;
        setText( "  " + value.getTopicName() ) ;
        setBorder( new EmptyBorder( 5, 0, 5, 0 ) );
        if( isSelected ) {
            setForeground( Color.CYAN ) ;
        }
        else {
            setForeground( Color.GRAY ) ;
        }
        
        return this ;
    }
}
