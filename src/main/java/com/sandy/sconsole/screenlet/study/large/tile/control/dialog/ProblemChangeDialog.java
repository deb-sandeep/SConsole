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
    
    private SessionControlTile controlTile = null ;
    
    public ProblemChangeDialog( SessionControlTile controlTile ) {
        super( "Choose problem", new ProblemChangeListCellRenderer() ) ;

        this.controlTile = controlTile ;
        problemRepo = SConsole.getAppContext().getBean( ProblemRepository.class ) ;
    }

    @Override
    protected List<Problem> getListItems() {
        List<Problem> problems = new ArrayList<>() ;
        
        Topic topic = controlTile.getChangeSelectionTopic() ;
        Book book = controlTile.getChangeSelectionBook() ;
        
        if( (topic != null) && (book != null) ) {
            problems = problemRepo.findUnsolvedProblems( topic.getId(), book.getId() ) ;
        }
        return problems ;
    }

    @Override
    protected Problem getDefaultSelectedEntity() {
        return controlTile.getChangeSelectionProblem() ;
    }
}
