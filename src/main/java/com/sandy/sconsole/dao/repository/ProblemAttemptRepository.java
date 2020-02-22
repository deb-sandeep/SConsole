package com.sandy.sconsole.dao.repository;

import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.* ;

import com.sandy.sconsole.api.burncalibration.HistoricBurnStat ;
import com.sandy.sconsole.api.jeetest.revision.RevisionQuestion ;
import com.sandy.sconsole.core.util.DayValue ;
import com.sandy.sconsole.dao.entity.* ;

public interface ProblemAttemptRepository extends CrudRepository<ProblemAttempt, Integer> {
    
    @Query( nativeQuery=true,
            value = 
              "SELECT "
            + "    pm.id as questionId, "
            + "    tm.subject_name as subjectName, "
            + "    tm.topic_name as topicName, "
            + "    bm.book_short_name as bookShortName, "
            + "    pm.exercise_name as exerciseName, "
            + "    pm.problem_type as problemType, "
            + "    pm.problem_tag as problemTag, "
            + "    pa.outcome as outcome, "
            + "    pa.duration as duration, "
            + "    pa.start_time as startTime, "
            + "    pm.starred as starred, "
            + "    pm.revision_count as revisionCount, "
            + "    pm.chapter_id as chapterId "
            + "FROM "
            + "    problem_attempt pa, "
            + "    problem_master pm, "
            + "    book_master bm, "
            + "    topic_master tm "
            + "WHERE "
            + "    ( ( pa.outcome = 'Solved' and pa.duration > 240 ) OR "
            + "      ( pa.outcome in ('Redo','Pigeon') ) OR "
            + "      pm.starred = 1 ) "
            + "    AND "
            + "    pa.problem_id = pm.id AND "
            + "    pm.topic_id = tm.id AND "
            + "    pm.book_id = bm.id AND "
            + "    pm.revision_count <= ?1 AND "
            + "    tm.subject_name = ?2 AND "
            + "    tm.id = ?3 AND "
            + "    bm.id = ?4 "
            + "ORDER BY "
            + "    tm.topic_name ASC, "
            + "    bm.book_short_name ASC, "
            + "    pm.id ASC "
    )
    public List<RevisionQuestion> getRevisionQuestions( Integer revCountThreshold,
                                                        String subjectName,
                                                        Integer topicId,
                                                        Integer bookId) ;

    @Query( nativeQuery=true,
            value = 
              "SELECT "
            + "    pm.id as questionId, "
            + "    tm.subject_name as subjectName, "
            + "    tm.topic_name as topicName, "
            + "    bm.book_short_name as bookShortName, "
            + "    pm.exercise_name as exerciseName, "
            + "    pm.problem_type as problemType, "
            + "    pm.problem_tag as problemTag, "
            + "    pa.outcome as outcome, "
            + "    pa.duration as duration, "
            + "    pa.start_time as startTime, "
            + "    pm.starred as starred, "
            + "    pm.revision_count as revisionCount, "
            + "    pm.chapter_id as chapterId "
            + "FROM "
            + "    problem_attempt pa, "
            + "    problem_master pm, "
            + "    book_master bm, "
            + "    topic_master tm "
            + "WHERE "
            + "    ( ( pa.outcome = 'Solved' and pa.duration > 240 ) OR "
            + "      ( pa.outcome in ('Redo','Pigeon') ) OR "
            + "      pm.starred = 1 ) "
            + "    AND "
            + "    pa.problem_id = pm.id AND "
            + "    pm.topic_id = tm.id AND "
            + "    pm.book_id = bm.id AND "
            + "    pm.revision_count <= ?1 AND "
            + "    tm.subject_name = ?2 AND "
            + "    tm.id = ?3 "
            + "ORDER BY "
            + "    tm.topic_name ASC, "
            + "    bm.book_short_name ASC, "
            + "    pm.id ASC "
    )
    public List<RevisionQuestion> getRevisionQuestions( Integer revCountThreshold,
                                                        String subjectName,
                                                        Integer topicId ) ;

    @Query( nativeQuery=true,
            value = 
              "SELECT "
            + "    pm.id as questionId, "
            + "    tm.subject_name as subjectName, "
            + "    tm.topic_name as topicName, "
            + "    bm.book_short_name as bookShortName, "
            + "    pm.exercise_name as exerciseName, "
            + "    pm.problem_type as problemType, "
            + "    pm.problem_tag as problemTag, "
            + "    pa.outcome as outcome, "
            + "    pa.duration as duration, "
            + "    pa.start_time as startTime, "
            + "    pm.starred as starred, "
            + "    pm.revision_count as revisionCount, "
            + "    pm.chapter_id as chapterId "
            + "FROM "
            + "    problem_attempt pa, "
            + "    problem_master pm, "
            + "    book_master bm, "
            + "    topic_master tm "
            + "WHERE "
            + "    ( ( pa.outcome = 'Solved' and pa.duration > 240 ) OR "
            + "      ( pa.outcome in ('Redo','Pigeon') ) OR "
            + "      pm.starred = 1 ) "
            + "    AND "
            + "    pa.problem_id = pm.id AND "
            + "    pm.topic_id = tm.id AND "
            + "    pm.book_id = bm.id AND "
            + "    pm.revision_count <= ?1 AND "
            + "    tm.subject_name = ?2 AND "
            + "    bm.id = ?3 "
            + "ORDER BY "
            + "    tm.topic_name ASC, "
            + "    bm.book_short_name ASC, "
            + "    pm.id ASC "
    )
    public List<RevisionQuestion> getRevisionQuestionsByBook( Integer revCountThreshold,
                                                              String subjectName,
                                                              Integer bookId ) ;

    @Query( nativeQuery=true,
            value = 
              "SELECT "
            + "    pm.id as questionId, "
            + "    tm.subject_name as subjectName, "
            + "    tm.topic_name as topicName, "
            + "    bm.book_short_name as bookShortName, "
            + "    pm.exercise_name as exerciseName, "
            + "    pm.problem_type as problemType, "
            + "    pm.problem_tag as problemTag, "
            + "    pa.outcome as outcome, "
            + "    pa.duration as duration, "
            + "    pa.start_time as startTime, "
            + "    pm.starred as starred, "
            + "    pm.revision_count as revisionCount, "
            + "    pm.chapter_id as chapterId "
            + "FROM "
            + "    problem_attempt pa, "
            + "    problem_master pm, "
            + "    book_master bm, "
            + "    topic_master tm "
            + "WHERE "
            + "    ( ( pa.outcome = 'Solved' and pa.duration > 240 ) OR "
            + "      ( pa.outcome in ('Redo','Pigeon') ) OR "
            + "      pm.starred = 1 ) "
            + "    AND "
            + "    pa.problem_id = pm.id AND "
            + "    pm.topic_id = tm.id AND "
            + "    pm.book_id = bm.id AND "
            + "    pm.revision_count <= ?1 AND "
            + "    tm.subject_name = ?2 "
            + "ORDER BY "
            + "    tm.topic_name ASC, "
            + "    bm.book_short_name ASC, "
            + "    pm.id ASC "
    )
    public List<RevisionQuestion> getRevisionQuestions( Integer revCountThreshold,
                                                        String subjectName ) ;

    @Query( nativeQuery=true,
            value =   
              "SELECT "
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
    
    @Query( value =   
            "SELECT "
          + "    pa "
          + "FROM "
          + "    ProblemAttempt pa, "
          + "    Problem p "
          + "WHERE "
          + "    pa.problem.id = p.id and "
          + "    p.topic.id = ?1 and "
          + "    pa.outcome in ( 'Solved', 'Redo', 'Pigeon' ) "
          + "ORDER BY "
          + "    pa.startTime DESC" 
    )
    public List<ProblemAttempt> findByTopicId( Integer topicId ) ;
    
    @Query( value =   
              "SELECT "
            + "    pa "
            + "FROM "
            + "    ProblemAttempt pa, "
            + "    Problem p "
            + "WHERE "
            + "    pa.problem.id = p.id and "
            + "    p.topic.id = ?1 and "
            + "    pa.outcome in ( 'Solved', 'Redo', 'Pigeon' ) and "
            + "    p.problemType = ?2 "
            + "ORDER BY "
            + "    pa.startTime DESC" 
    )
    public List<ProblemAttempt> findByTopicIdAndProblemType( Integer topicId, 
                                                             String problemType ) ;
    
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
    
    @Query( nativeQuery=true, 
            value = 
              "SELECT "
            + "    CAST( s.start_time as DATE ) as date, "
            + "    sum( s.absolute_duration )/3600 as value "
            + "FROM "
            + "    session s, "
            + "    topic_master tm "
            + "WHERE "
            + "    s.topic_id = tm.id AND "
            + "    tm.subject_name = ?1 AND "
            + "    s.start_time > DATE(DATE_SUB(NOW(), INTERVAL 30 DAY)) "
            + "GROUP BY "
            + "    date "
            + "ORDER BY "
            + "    s.start_time ASC"
    )
    public List<DayValue> getLast30DaysTimeSpent( String subjectName ) ;

    @Query( nativeQuery=true, 
            value = 
            "SELECT "
            + "    CAST( pa.start_time as DATE ) as date, "
            + "    count( pa.id ) as value "
            + "FROM "
            + "    problem_attempt pa, "
            + "    session s "
            + "WHERE "
            + "    pa.session_id = s.id AND "
            + "    pa.start_time > DATE(DATE_SUB(NOW(), INTERVAL 30 DAY)) AND "
            + "    s.topic_id = ?1 AND "
            + "    s.session_type = 'Exercise' AND "
            + "    ( pa.outcome = 'Solved' OR pa.outcome = 'Ignore' ) "
            + "GROUP BY "
            + "    date "
            + "ORDER BY "
            + "    pa.start_time ASC "
    )
    public List<DayValue> getLast30DaysProblemsSolved( Integer topicId ) ;

    @Query( nativeQuery=true,
            value = 
            "SELECT "
            + "    CAST( s.start_time as DATE ) as date, "
            + "    sum( s.duration )/3600 as value "
            + "FROM "
            + "    session s "
            + "WHERE "
            + "    s.start_time > DATE(DATE_SUB(NOW(), INTERVAL 30 DAY)) "
            + "GROUP BY "
            + "    date "
            + "ORDER BY "
            + "    s.start_time ASC "
    )
    public List<DayValue> getLast30DaysTimeSpent() ;
}
