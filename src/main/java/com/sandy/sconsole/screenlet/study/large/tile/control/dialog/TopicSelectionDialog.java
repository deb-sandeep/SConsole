package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;
import com.sandy.sconsole.screenlet.study.large.tile.control.dialog.renderer.TopicChangeListCellRenderer ;

@SuppressWarnings( "serial" )
public class TopicSelectionDialog extends AbstractListSelectionDialog<Topic> {
    
    static final Logger log = Logger.getLogger( TopicSelectionDialog.class ) ;
    
    public interface TopicSelectionListener {
        public void handleNewTopicSelection( Topic newTopic ) ;
        public String getSubjectName() ;
        public Topic getDefaultTopic() ;
    }

    private TopicRepository topicRepo = null ;
    
    private TopicSelectionListener changeListener = null ;
    
    public TopicSelectionDialog( TopicSelectionListener listener ) {
        
        super( "Choose topic", new TopicChangeListCellRenderer() ) ;

        this.changeListener = listener ; 
        
        topicRepo = SConsole.getAppContext().getBean( TopicRepository.class ) ;
    }

    @Override
    protected List<Topic> getListItems() {
        return topicRepo.findAllBySubjectNameOrderByIdAsc( changeListener.getSubjectName() ) ;
    }

    @Override
    protected Topic getDefaultSelectedEntity() {
        return changeListener.getDefaultTopic() ;
    }
    
    @Override
    public void handleSelectNavKey() {
        super.handleSelectNavKey() ;
        changeListener.handleNewTopicSelection( (Topic)getReturnValue() ) ;
    }
}
