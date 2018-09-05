package com.sandy.sconsole.dao.repository;

import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.Session ;

public interface SessionRepository extends CrudRepository<Session, Integer> {
    
    @Query( "select s from Session s where s.startTime > CURRENT_DATE" )
    public List<Session> getTodaySessions() ;
}
