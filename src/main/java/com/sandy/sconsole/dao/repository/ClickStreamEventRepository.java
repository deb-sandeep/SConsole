package com.sandy.sconsole.dao.repository;

import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.ClickStreamEvent ;

public interface ClickStreamEventRepository 
    extends CrudRepository<ClickStreamEvent, Integer> {

    List<ClickStreamEvent> findAllByTestAttemptIdOrderById( Integer testAttemptId ) ;
    
    @Query( value = 
            "SELECT "
          + "    cse "
          + "FROM "
          + "    ClickStreamEvent cse "
          + "WHERE "
          + "    cse.testAttemptId = ?1 AND "
          + "    cse.eventId LIKE 'LAP_%' "
          + "ORDER BY "
          + "    cse.id ASC")
    List<ClickStreamEvent> findLapEvents( Integer testAttemptId ) ;
}
