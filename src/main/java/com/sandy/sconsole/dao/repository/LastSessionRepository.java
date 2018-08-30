package com.sandy.sconsole.dao.repository;

import org.springframework.data.jpa.repository.Modifying ;
import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;
import org.springframework.data.repository.query.Param ;
import org.springframework.transaction.annotation.Transactional ;

import com.sandy.sconsole.dao.entity.LastSession ;

public interface LastSessionRepository extends CrudRepository<LastSession, String> {
    
    @Modifying( clearAutomatically = true )
    @Transactional
    @Query( value = "INSERT INTO last_session (subject_name, session_id) " + 
                    "VALUES (?1, ?2) " + 
                    "ON DUPLICATE KEY UPDATE " + 
                    "   session_id = VALUES( session_id )", 
            nativeQuery = true )
    void update( String subjectName, int sessionId ) ;
    
    @Query( value = 
              "SELECT "
            + "    s.id "
            + "FROM "
            + "    session s, "
            + "    topic_master tm "
            + "WHERE "
            + "    s.topic_id = tm.id AND "
            + "    tm.subject_name = :subjectName AND "
            + "    s.id <> :sessionId "
            + "ORDER BY "
            + "    s.start_time desc "
            + "LIMIT 1 "
            , nativeQuery=true )
    Integer findSessionBefore( @Param("sessionId") Integer sessionId,
                               @Param("subjectName") String subjectName ) ;
    
    @Query( value = 
                "SELECT "
              + "    id "
              + "FROM "
              + "    session "
              + "WHERE "
              + "    topic_id = :topicId "
              + "ORDER BY "
              + "    start_time DESC "
              + "LIMIT 1 "
            , nativeQuery=true )
    Integer findLastSessionForTopic( @Param("topicId") Integer topicId ) ;

    @Query( value = 
            "SELECT "
          + "    ls "
          + "FROM "
          + "    LastSession ls "
          + "WHERE "
          + "    ls.subjectName = ?1 " )
    LastSession findLastSessionForSubject( String subject ) ;
}
