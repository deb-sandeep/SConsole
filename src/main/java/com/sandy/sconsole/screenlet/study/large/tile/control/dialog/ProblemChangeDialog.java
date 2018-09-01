package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;

@SuppressWarnings( "serial" )
public class ProblemChangeDialog extends AbstractListSelectionDialog<Problem> {
    
    static final Logger log = Logger.getLogger( ProblemChangeDialog.class ) ;

    private ProblemRepository problemRepo = null ;
    
    private SessionControlTile control = null ;
    
    public ProblemChangeDialog( SessionControlTile controlTile ) {
        super( "Choose problem", new ProblemChangeListCellRenderer() ) ;

        this.control = controlTile ;
        problemRepo = SConsole.getAppContext().getBean( ProblemRepository.class ) ;
    }

    @Override
    protected List<Problem> getListItems() {
        List<Problem> problems = new ArrayList<>() ;
        
//        Topic topic = control.getChangeSelectionTopic() ;
//        Book book = control.getChangeSelectionBook() ;
//        
//        if( (topic != null) && (book != null) ) {
//            problems = problemRepo.findUnsolvedProblems( topic.getId(), book.getId() ) ;
//        }
        return problems ;
    }

    @Override
    protected Problem getDefaultSelectedEntity() {
//        return control.getChangeSelectionProblem() ;
        return null ;
    }
    
    @Override
    public void handleSelectNavKey() {
        super.handleSelectNavKey() ;
//        control.handleNewProblemSelection( (Problem)getReturnValue() ) ;
    }
}
