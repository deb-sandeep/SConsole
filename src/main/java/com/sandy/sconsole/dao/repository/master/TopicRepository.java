package com.sandy.sconsole.dao.repository.master;

import java.util.List ;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.master.Topic ;

public interface TopicRepository extends CrudRepository<Topic, Integer> {
    
    List<Topic> findAllBySubjectName( String subjectName ) ;
}
