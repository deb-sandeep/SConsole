package com.sandy.sconsole.screenlet.study.large.tile.control.dialog.renderer;

import java.awt.Component ;

import javax.swing.JList ;

import com.sandy.sconsole.dao.entity.master.Topic ;

@SuppressWarnings( "serial" )
public class TopicChangeListCellRenderer 
    extends AbstractChangeListCellRenderer<Topic> {

    @Override
    public Component getListCellRendererComponent( JList<? extends Topic> list,
                                                   Topic value, 
                                                   int index, 
                                                   boolean isSelected, 
                                                   boolean cellHasFocus ) {
        super.doBaseDecoration( list, value, index, isSelected, cellHasFocus ) ;
        setText( "  " + value.getTopicName() ) ;
        return this ;
    }
}
