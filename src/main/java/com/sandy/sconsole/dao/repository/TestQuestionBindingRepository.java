package com.sandy.sconsole.dao.repository;

import java.util.List ;

import javax.transaction.Transactional ;

import org.springframework.data.jpa.repository.Modifying ;
import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.TestQuestionBinding ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.entity.master.Topic ;

public interface TestQuestionBindingRepository 
    extends CrudRepository<TestQuestionBinding, Integer> {
    
    @Transactional
    @Modifying
    @Query( "DELETE from TestQuestionBinding tqb WHERE tqb.testConfig.id = ?1" )
    void deleteByTestConfigId( Integer testConfigId ) ;
    
    @Query( "SELECT tqb "
          + "FROM TestQuestionBinding tqb "
          + "WHERE tqb.testConfig.id = ?1 "
          + "ORDER BY "
          + "  tqb.subject.name ASC, "
          + "  tqb.sequence ASC"
    )
    List<TestQuestionBinding> findAllByTestConfigId( Integer id ) ;

    @Query(   "SELECT DISTINCT tqb.topic "
            + "FROM TestQuestionBinding tqb "
            + "WHERE tqb.testConfig.id = ?1 "
            + "ORDER BY "
            + "  tqb.subject.name ASC, "
            + "  tqb.topic.id ASC"
      )
    List<Topic> getTopicsForTest( Integer id ) ;
    
    @Query( "SELECT tqb.question "
          + "FROM "
          + "   TestQuestionBinding tqb "
          + "WHERE "
          + "   tqb.testConfig.id = ?1 "
          + "ORDER BY "
          + "   tqb.id ASC "
    )
    List<TestQuestion> getTestQuestionsForTestConfig( Integer testConfigId ) ;
}
