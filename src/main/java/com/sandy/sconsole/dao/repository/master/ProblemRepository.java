package com.sandy.sconsole.dao.repository.master;

import java.util.List ;

import org.springframework.data.jpa.repository.Modifying ;
import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;
import org.springframework.data.repository.query.Param ;
import org.springframework.transaction.annotation.Transactional ;

import com.sandy.sconsole.dao.entity.master.Problem ;

public interface ProblemRepository extends CrudRepository<Problem, Integer> {
    
    @Modifying
    @Transactional
    @Query( value="insert into processed_ex_meta (name, date) values ( :name, NOW() )",
            nativeQuery = true ) 
    void saveMetaExercise( @Param("name") String name ) ;
    
    @Query( value="select case when count(name)>0 then TRUE else FALSE end " + 
                  "from processed_ex_meta where name=:name",
            nativeQuery=true )
    int getExMetaProcessCount( @Param("name") String exMeta ) ;
    
    List<Problem> findByBookIdAndTopicId( int bookId, int topicId ) ;
    
    @Query( "SELECT p "
          + "FROM Problem p "
          + "WHERE "
          +   "p.solved = false and "
          +   "p.active = true and "
          +   "p.topic.id = ?1 and "
          +   "p.book.id = ?2 "
          + "ORDER BY " 
          +   "p.id asc" )
    List<Problem> findUnsolvedProblems( int topicId, int bookId ) ;
}
