package com.sandy.sconsole.dao.repository.master;

import java.util.* ;

import org.springframework.data.jpa.repository.* ;
import org.springframework.data.repository.* ;
import org.springframework.data.repository.query.* ;
import org.springframework.transaction.annotation.* ;

import com.sandy.sconsole.dao.entity.master.* ;

public interface ProblemRepository extends CrudRepository<Problem, Integer> {
    
    @Modifying
    @Transactional
    @Query( value="INSERT INTO " + 
                  "    processed_ex_meta (name, date) " + 
                  "VALUES " + 
                  "    (:name, NOW())",
            nativeQuery = true ) 
    void saveMetaExercise( @Param("name") String name ) ;
    
    @Query( value="SELECT " + 
                  "    CASE " + 
                  "        WHEN count(name)>0 " + 
                  "        THEN " + 
                  "            true " + 
                  "        ELSE " + 
                  "            false " + 
                  "    END " + 
                  "FROM " + 
                  "    processed_ex_meta " + 
                  "WHERE " + 
                  "    name=:name",
            nativeQuery=true )
    int getExMetaProcessCount( @Param("name") String exMeta ) ;
    
    List<Problem> findByBookIdAndTopicId( int bookId, int topicId ) ;
    
    @Query( "SELECT p "
          + "FROM Problem p "
          + "WHERE "
          +   "p.solved = false and "
          +   "p.pigeoned = false and "
          +   "p.active = true and "
          +   "p.topic.id = ?1 and "
          +   "p.book.id = ?2 "
          + "ORDER BY " 
          +   "p.id asc" )
    List<Problem> findUnsolvedProblems( int topicId, int bookId ) ;

    @Query( "SELECT count(p) "
            + "FROM Problem p "
            + "WHERE "
            +   "p.solved = false and "
            +   "p.pigeoned = false and "
            +   "p.active = true and "
            +   "p.topic.id = ?1 and "
            +   "p.book.id = ?2" )
    Integer findUnsolvedProblemCount( int topicId, int bookId ) ;
}
