package com.sandy.sconsole.dao.repository;

import java.util.List ;

import javax.transaction.Transactional ;

import org.springframework.data.jpa.repository.Modifying ;
import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.TestQuestionBinding ;

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
}
