package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import java.awt.Component ;

import javax.swing.JList ;

import com.sandy.sconsole.dao.entity.master.Book ;

@SuppressWarnings( "serial" )
public class BookChangeListCellRenderer 
    extends AbstractChangeListCellRenderer<Book> {

    @Override
    public Component getListCellRendererComponent( JList<? extends Book> list,
                                                   Book value, 
                                                   int index, 
                                                   boolean isSelected, 
                                                   boolean cellHasFocus ) {
        super.doBaseDecoration( list, value, index, isSelected, cellHasFocus ) ;
        setText( "  " + value.getBookShortName() ) ;
        return this ;
    }
}
