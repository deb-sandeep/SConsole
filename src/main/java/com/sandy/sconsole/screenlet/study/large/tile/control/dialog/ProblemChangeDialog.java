package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.renderer.ProblemChangeListCellRenderer ;
import com.sandy.sconsole.screenlet.study.large.tile.control.state.ChangeState ;

@SuppressWarnings( "serial" )
public class ProblemChangeDialog extends AbstractListSelectionDialog<Problem> {
    
    static final Logger log = Logger.getLogger( ProblemChangeDialog.class ) ;

    private ProblemRepository problemRepo = null ;
    
    private ChangeState changeState = null ;
    
    public ProblemChangeDialog( ChangeState changeState ) {
        super( "Choose problem", new ProblemChangeListCellRenderer() ) ;
        this.changeState = changeState ;
        problemRepo = SConsole.getAppContext().getBean( ProblemRepository.class ) ;
    }

    @Override
    protected List<Problem> getListItems() {
        List<Problem> problems = new ArrayList<>() ;
        
        Session session = changeState.getSessionInfo().session ;
        
        Topic topic = session.getTopic() ;
        Book book   = session.getBook() ;
        
        if( (topic != null) && (book != null) ) {
            problems = problemRepo.findUnsolvedProblems( topic.getId(), book.getId() ) ;
        }
        return problems ;
    }

    @Override
    protected Problem getDefaultSelectedEntity() {
        return changeState.getSessionInfo().session.getLastProblem() ;
    }
    
    @Override
    public void handleSelectNavKey() {
        super.handleSelectNavKey() ;
        changeState.handleNewProblemSelection( (Problem)getReturnValue() ) ;
    }
}
