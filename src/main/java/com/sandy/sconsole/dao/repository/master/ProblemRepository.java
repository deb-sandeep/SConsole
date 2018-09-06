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
    
    @Modifying
    @Transactional
    @Query( value="UPDATE problem_master " + 
                  "SET active=?2 " + 
                  "WHERE id=?1 ",
            nativeQuery = true ) 
    void updateActivation( Integer problemId, Boolean active ) ;
    
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
            +   "p.topic.id = ?1 "
            + "ORDER BY " 
            +   "p.id asc" )
    List<Problem> findUnsolvedProblems( int topicId ) ;

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

    @Query(   "SELECT count(p) "
            + "FROM Problem p "
            + "WHERE "
            +   "p.solved = false and "
            +   "p.pigeoned = false and "
            +   "p.active = true and "
            +   "p.topic.id = ?1 and "
            +   "p.book.id = ?2" )
    Integer findUnsolvedProblemCount( int topicId, int bookId ) ;

    @Query( value=
              "SELECT "
            + "    id "
            + "FROM "
            + "    problem_master "
            + "WHERE "
            + "    topic_id = ?1 AND "
            + "    book_id = ?2 AND "
            + "    active = 1 AND "
            + "    solved = 0 AND "
            + "    pigeoned = 0 "
            + "ORDER BY "
            + "    id asc "
            + "LIMIT 1 "
           ,nativeQuery=true )
    Integer findNextUnsolvedProblem( Integer topicId, Integer bookId ) ;

    @Query(   "SELECT p "
            + "FROM Problem p "
            + "WHERE "
            +   "p.solved = false and "
            +   "p.topic.id = ?1" )
    List<Problem> findAllUnsolvedProblemsByTopicId( Integer topicId ) ;
}
