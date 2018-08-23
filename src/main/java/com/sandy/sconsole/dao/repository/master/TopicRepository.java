package com.sandy.sconsole.dao.repository.master;

import java.util.* ;

import org.springframework.data.repository.* ;

import com.sandy.sconsole.dao.entity.master.* ;

public interface TopicRepository extends CrudRepository<Topic, Integer> {
    
    List<Topic> findAllBySubjectName( String subjectName ) ;
    Topic       findBySubjectNameAndTopicName( String subName, String topicName ) ;
}
