package com.sandy.sconsole.dao.repository;

import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.* ;

import com.sandy.sconsole.api.burn.HistoricBurnStat ;
import com.sandy.sconsole.dao.entity.* ;

public interface ProblemAttemptRepository extends CrudRepository<ProblemAttempt, Integer> {

    @Query( nativeQuery=true,
            value = "SELECT "
                    + "    cast( pa.start_time as date ) as date, "
                    + "    count( pa.problem_id ) as numQuestionsSolved, "
                    + "    sum(duration) as duration "
                    + "FROM "
                    + "    problem_attempt pa, "
                    + "    problem_master pm "
                    + "WHERE "
                    + "    pa.problem_id = pm.id and "
                    + "    pm.topic_id = ?1 and "
                    + "    ( pa.outcome = 'Solved' or pa.outcome = 'Ignore' ) "
                    + "GROUP BY "
                    + "    date" 
    )
    public List<HistoricBurnStat> getHistoricBurnStats( Integer topicId ) ;
    
   
    @Query( nativeQuery=true,
            value = 
              "SELECT "
            + "    COUNT( pa.id ) "
            + "FROM "
            + "    problem_attempt pa, "
            + "    session s "
            + "WHERE "
            + "    s.id = pa.session_id AND "
            + "    s.topic_id = ?1 AND "
            + "    ( pa.outcome = 'Solved' OR pa.outcome = 'Ignore' ) AND "
            + "    pa.start_time > current_date() "
    )
    public Integer getNumProblemsSolvedToday( Integer topicId ) ;
}
