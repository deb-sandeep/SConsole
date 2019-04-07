package com.sandy.sconsole.dao.repository.master;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.master.TestQuestion ;

public interface TestQuestionRepository 
    extends CrudRepository<TestQuestion, Integer> {
}
