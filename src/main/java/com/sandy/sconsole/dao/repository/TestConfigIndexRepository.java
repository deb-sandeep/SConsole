package com.sandy.sconsole.dao.repository;

import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.TestConfigIndex ;

public interface TestConfigIndexRepository 
    extends CrudRepository<TestConfigIndex, Integer> {
    
    @Query( value =   
            "SELECT "
          + "    tci "
          + "FROM "
          + "    TestConfigIndex tci "
          + "WHERE "
          + "    tci.id NOT IN ( "
          + "       SELECT ta.testConfig.id "
          + "       FROM TestAttempt ta "
          + "    ) "
          + "ORDER BY "
          + "    tci.id ASC "
    )
    List<TestConfigIndex> findUnattemptedTests() ;
}
