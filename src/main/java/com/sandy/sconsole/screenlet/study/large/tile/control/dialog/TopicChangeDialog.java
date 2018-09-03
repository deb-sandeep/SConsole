package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.renderer.TopicChangeListCellRenderer ;
import com.sandy.sconsole.screenlet.study.large.tile.control.state.ChangeState ;

@SuppressWarnings( "serial" )
public class TopicChangeDialog extends AbstractListSelectionDialog<Topic> {
    
    static final Logger log = Logger.getLogger( TopicChangeDialog.class ) ;

    private String subjectName = null ;
    private TopicRepository topicRepo = null ;
    
    private ChangeState changeState = null ;
    
    public TopicChangeDialog( ChangeState changeState ) {
        super( "Choose topic", new TopicChangeListCellRenderer() ) ;

        this.changeState = changeState;
        this.subjectName = changeState.getSubjectName() ;
        
        topicRepo = SConsole.getAppContext().getBean( TopicRepository.class ) ;
    }

    @Override
    protected List<Topic> getListItems() {
        return topicRepo.findAllBySubjectNameOrderByIdAsc( subjectName ) ;
    }

    @Override
    protected Topic getDefaultSelectedEntity() {
        return changeState.getSessionInfo().session.getTopic() ;
    }
    
    @Override
    public void handleSelectNavKey() {
        super.handleSelectNavKey() ;
        changeState.handleNewTopicSelection( (Topic)getReturnValue() ) ;
    }
}
