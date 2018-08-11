package com.sandy.sconsole.dao.repository.master;

import java.util.List ;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.master.Problem ;

public interface ProblemRepository extends CrudRepository<Problem, Integer> {
    
    List<Problem> findByBookIdAndTopicId( int bookId, int topicId ) ;
}
