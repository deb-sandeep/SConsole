package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;

@SuppressWarnings( "serial" )
public class TopicChangeDialog extends AbstractListSelectionDialog<Topic> {
    
    static final Logger log = Logger.getLogger( TopicChangeDialog.class ) ;

    private String subjectName = null ;
    private TopicRepository topicRepo = null ;
    
    private SessionControlTile control = null ;
    
    public TopicChangeDialog( SessionControlTile controlTile ) {
        super( "Choose topic", new TopicChangeListCellRenderer() ) ;

        this.control = controlTile ;
        this.subjectName = controlTile.getScreenlet().getDisplayName() ;
        
        topicRepo = SConsole.getAppContext().getBean( TopicRepository.class ) ;
    }

    @Override
    protected List<Topic> getListItems() {
        return topicRepo.findAllBySubjectNameOrderByIdAsc( subjectName ) ;
    }

    @Override
    protected Topic getDefaultSelectedEntity() {
//        return control.getChangeSelectionTopic() ;
        return null ;
    }
    
    @Override
    public void handleSelectNavKey() {
        super.handleSelectNavKey() ;
//        control.handleNewTopicSelection( (Topic)getReturnValue() ) ;
    }
}
