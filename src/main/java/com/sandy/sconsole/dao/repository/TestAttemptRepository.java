package com.sandy.sconsole.dao.repository;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.TestAttempt ;

public interface TestAttemptRepository 
    extends CrudRepository<TestAttempt, Integer> {
}
