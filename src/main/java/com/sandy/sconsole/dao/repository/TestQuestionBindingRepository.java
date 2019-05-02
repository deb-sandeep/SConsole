package com.sandy.sconsole.dao.repository;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.TestQuestionBinding ;

public interface TestQuestionBindingRepository 
    extends CrudRepository<TestQuestionBinding, Integer> {
}
