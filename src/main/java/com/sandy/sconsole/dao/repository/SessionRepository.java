package com.sandy.sconsole.dao.repository;

import java.sql.Timestamp ;
import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.Session ;

public interface SessionRepository extends CrudRepository<Session, Integer> {
    
    public interface SessionSummary {
        public String getDate() ;
        public String getSubject() ;
        public Timestamp getStartTime() ;
        public Integer getDuration() ;
    }
    
    @Query( nativeQuery=true,
            value = 
              "SELECT * " 
            + "FROM " 
            + "    session s "
            + "WHERE " 
            + "   ( s.start_time > CURDATE() AND s.end_time < DATE_ADD( CURDATE(), INTERVAL 1 DAY ) ) OR " 
            + "   ( s.end_time > CURDATE() AND s.end_time < DATE_ADD( CURDATE(), INTERVAL 1 DAY ) )" )
    public List<Session> getTodaySessions() ;
    
    @Query( nativeQuery=true,
            value=
              "SELECT "
            + "    cast( s.start_time as date ) as date, "
            + "    tm.subject_name as subject, "
            + "    s.start_time as startTime, "
            + "    s.duration as duration "
            + "FROM "
            + "    session s, "
            + "    topic_master tm "
            + "WHERE "
            + "    s.topic_id = tm.id AND "
            + "    s.start_time > DATE(DATE_SUB(NOW(), INTERVAL 31 DAY)) AND "
            + "    s.start_time < CURDATE() AND "
            + "    s.duration > 60 "
            + "ORDER BY "
            + "    s.start_time ASC "
    )
    public List<SessionSummary> getLast30DSessionSummary() ;
}
