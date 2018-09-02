package com.sandy.sconsole.screenlet.study.large.tile.control.dialog.renderer;

import java.awt.Component ;

import javax.swing.JList ;

import com.sandy.sconsole.dao.entity.master.Problem ;

@SuppressWarnings( "serial" )
public class ProblemChangeListCellRenderer 
    extends AbstractChangeListCellRenderer<Problem> {

    @Override
    public Component getListCellRendererComponent( JList<? extends Problem> list,
                                                   Problem value, 
                                                   int index, 
                                                   boolean isSelected, 
                                                   boolean cellHasFocus ) {
        
        super.doBaseDecoration( list, value, index, isSelected, cellHasFocus ) ;
        setText( "Ch-" + 
                 value.getChapterId() + 
                 "/" + 
                 value.getExerciseName() + 
                 " - " + 
                 value.getProblemTag() ) ;
        return this ;
    }
}
