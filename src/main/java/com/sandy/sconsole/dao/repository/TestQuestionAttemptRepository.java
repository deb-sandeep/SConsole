package com.sandy.sconsole.dao.repository;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.TestQuestionAttempt ;

public interface TestQuestionAttemptRepository 
    extends CrudRepository<TestQuestionAttempt, Integer> {
}
