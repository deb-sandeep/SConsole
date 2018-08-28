package com.sandy.sconsole.dao.repository.master;

import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.* ;

import com.sandy.sconsole.dao.entity.master.* ;

public interface BookRepository extends CrudRepository<Book, Integer> {

    Book findByBookShortName( String shortName ) ;
    
    @Query( value = "select " +
                    "   distinct( bm.id ) " + 
                    "from " + 
                    "    topic_master tm, " + 
                    "    book_master bm, " + 
                    "    problem_master pm " + 
                    "where " + 
                    "    tm.subject_name = bm.subject_name and " + 
                    "    tm.id = ?1 and " +
                    "    pm.topic_id = tm.id and " + 
                    "    pm.book_id = bm.id",
            nativeQuery = true
          )
    List<Integer> findProblemBooksForTopic( Integer topicId ) ;
}
