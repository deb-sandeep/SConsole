package com.sandy.sconsole.dao.repository.master;

import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.master.Topic ;

public interface TopicRepository extends CrudRepository<Topic, Integer> {
    
    List<Topic> findAllBySubjectNameOrderByIdAsc( String subjectName ) ;
    
    Topic findBySubjectNameAndTopicName( String subName, String topicName ) ;
    
    @Query( value = 
            "select t "
          + "from Topic t "
          + "where "
          + "    ( t.burnStart < CURRENT_DATE and "
          + "    t.burnCompletion > CURRENT_DATE ) or "
          + "    t.active = true "
          + "order by "
          + "    t.streamNumber asc, "
          + "    t.burnStart asc"
    )
    List<Topic> findActiveTopics() ;
}
