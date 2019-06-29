package com.sandy.sconsole.dao.repository;

import java.util.List ;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.TestQuestionAttempt ;

public interface TestQuestionAttemptRepository 
    extends CrudRepository<TestQuestionAttempt, Integer> {
    
    List<TestQuestionAttempt> findAllByTestAttemptId( Integer testAttemptId ) ;
    
    TestQuestionAttempt findByTestAttemptIdAndTestQuestionId( 
                               Integer testAttemptId, Integer testQuestionId ) ;
}
