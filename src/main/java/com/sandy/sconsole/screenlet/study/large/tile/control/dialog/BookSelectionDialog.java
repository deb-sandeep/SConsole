package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.BookRepository ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.renderer.BookChangeListCellRenderer ;

@SuppressWarnings( "serial" )
public class BookSelectionDialog extends AbstractListSelectionDialog<Book> {
    
    static final Logger log = Logger.getLogger( BookSelectionDialog.class ) ;
    
    public interface BookSelectionListener {
        public Topic getDefaultTopic() ;
        public Book getDefaultBook() ;
        public void handleNewBookSelection( Book newBook ) ;
    }

    private BookRepository bookRepo = null ;
    
    private BookSelectionListener changeListener = null ;
    
    public BookSelectionDialog( BookSelectionListener changeState ) {
        super( "Choose book", new BookChangeListCellRenderer() ) ;
        this.changeListener = changeState ;
        bookRepo = SConsole.getAppContext().getBean( BookRepository.class ) ;
    }

    @Override
    protected List<Book> getListItems() {
        List<Book> books = new ArrayList<Book>() ;
        
        Topic selectedTopic = changeListener.getDefaultTopic() ;
        if( selectedTopic != null ) {
            List<Integer> bookIds = bookRepo.findProblemBooksForTopic( selectedTopic.getId() ) ;
            bookRepo.findAllById( bookIds ).forEach( e -> books.add( e )) ;
        }
        return books ;
    }

    @Override
    protected Book getDefaultSelectedEntity() {
        return changeListener.getDefaultBook() ;
    }
    
    @Override
    public void handleSelectNavKey() {
        super.handleSelectNavKey() ;
        changeListener.handleNewBookSelection( (Book)getReturnValue() ) ;
    }
}
