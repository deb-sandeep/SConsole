package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.BookRepository ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.renderer.BookChangeListCellRenderer ;
import com.sandy.sconsole.screenlet.study.large.tile.control.state.ChangeState ;

@SuppressWarnings( "serial" )
public class BookChangeDialog extends AbstractListSelectionDialog<Book> {
    
    static final Logger log = Logger.getLogger( BookChangeDialog.class ) ;

    private BookRepository bookRepo = null ;
    
    private ChangeState changeState = null ;
    
    public BookChangeDialog( ChangeState changeState ) {
        super( "Choose book", new BookChangeListCellRenderer() ) ;
        this.changeState = changeState ;
        bookRepo = SConsole.getAppContext().getBean( BookRepository.class ) ;
    }

    @Override
    protected List<Book> getListItems() {
        List<Book> books = new ArrayList<Book>() ;
        
        Topic selectedTopic = changeState.getSessionInfo().session.getTopic() ;
        if( selectedTopic != null ) {
            List<Integer> bookIds = bookRepo.findProblemBooksForTopic( selectedTopic.getId() ) ;
            bookRepo.findAllById( bookIds ).forEach( e -> books.add( e )) ;
        }
        return books ;
    }

    @Override
    protected Book getDefaultSelectedEntity() {
        return changeState.getSessionInfo().session.getBook() ;
    }
    
    @Override
    public void handleSelectNavKey() {
        super.handleSelectNavKey() ;
        changeState.handleNewBookSelection( (Book)getReturnValue() ) ;
    }
}
