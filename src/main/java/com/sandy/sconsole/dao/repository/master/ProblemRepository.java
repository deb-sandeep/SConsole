package com.sandy.sconsole.dao.repository.master;

import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;
import org.springframework.data.repository.query.Param ;

import com.sandy.sconsole.dao.entity.master.Problem ;

public interface ProblemRepository extends CrudRepository<Problem, Integer> {
    
    @Query( value="select count(*) from processed_ex_meta where name=:name",
            nativeQuery=true )
    int isMetaExerciseProcessed( @Param("name") String exMeta ) ;
    
    List<Problem> findByBookIdAndTopicId( int bookId, int topicId ) ;
}
