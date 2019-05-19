package com.sandy.sconsole.dao.repository;

import java.util.List ;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.TestAttempt ;

public interface TestAttemptRepository 
    extends CrudRepository<TestAttempt, Integer> {
    
    List<TestAttempt> findAllByOrderByDateAttemptedDesc() ;
}
