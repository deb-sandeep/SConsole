package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.renderer.ProblemChangeListCellRenderer ;

@SuppressWarnings( "serial" )
public class ProblemSelectionDialog extends AbstractListSelectionDialog<Problem> {
    
    static final Logger log = Logger.getLogger( ProblemSelectionDialog.class ) ;
    
    public interface ProblemSelectionListener {
        public Topic getDefaultTopic() ;
        public Book getDefaultBook() ;
        public Problem getDefaultProblem() ;
        public void handleNewProblemSelection( Problem newProblem ) ;
    }

    private ProblemRepository problemRepo = null ;
    
    private ProblemSelectionListener changeListener = null ;
    
    public ProblemSelectionDialog( ProblemSelectionListener changeState ) {
        super( "Choose problem", new ProblemChangeListCellRenderer() ) ;
        this.changeListener = changeState ;
        problemRepo = SConsole.getAppContext().getBean( ProblemRepository.class ) ;
    }

    @Override
    protected List<Problem> getListItems() {
        List<Problem> problems = new ArrayList<>() ;
        
        Topic topic = changeListener.getDefaultTopic() ;
        Book book   = changeListener.getDefaultBook() ;
        
        if( (topic != null) && (book != null) ) {
            problems = problemRepo.findUnsolvedProblems( topic.getId(), book.getId() ) ;
        }
        return problems ;
    }

    @Override
    protected Problem getDefaultSelectedEntity() {
        return changeListener.getDefaultProblem() ;
    }
    
    @Override
    public void handleSelectNavKey() {
        super.handleSelectNavKey() ;
        changeListener.handleNewProblemSelection( (Problem)getReturnValue() ) ;
    }
}
