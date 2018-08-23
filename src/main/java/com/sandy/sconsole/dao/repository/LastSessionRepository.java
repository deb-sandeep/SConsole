package com.sandy.sconsole.dao.repository;

import org.springframework.data.jpa.repository.* ;
import org.springframework.data.repository.* ;
import org.springframework.transaction.annotation.* ;

import com.sandy.sconsole.dao.entity.* ;

public interface LastSessionRepository extends CrudRepository<LastSession, String> {
    
    @Modifying( clearAutomatically = true )
    @Transactional
    @Query( value = "INSERT INTO last_session (subject_name, session_id) " + 
                    "VALUES (?1, ?2) " + 
                    "ON DUPLICATE KEY UPDATE " + 
                    "   session_id = VALUES( session_id )", 
            nativeQuery = true )
    void update( String subjectName, int sessionId ) ;
}
