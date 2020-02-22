package com.sandy.sconsole.dao.repository;

import java.util.Date ;
import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.TestQuestionAttempt ;

public interface TestQuestionAttemptRepository 
    extends CrudRepository<TestQuestionAttempt, Integer> {
    
    public interface IncorrectTestAnswerRC {
        Integer getTopicId() ;
        Integer getTestQuestionId() ;
        String  getRootCause() ;
    }
    
    List<TestQuestionAttempt> findAllByTestAttemptId( Integer testAttemptId ) ;
    
    TestQuestionAttempt findByTestAttemptIdAndTestQuestionId( 
                               Integer testAttemptId, Integer testQuestionId ) ;

    @Query( nativeQuery=true,
            value = 
          "SELECT "
        + "    tm.id as topicId, "
        + "    tqa.test_question_id as testQuestionId, "
        + "    tqa.root_cause as rootCause "
        + "FROM "
        + "    test_question_binding tqb, "
        + "    topic_master tm, "
        + "    test_question_attempt tqa, "
        + "    test_attempt ta "
        + "WHERE "
        + "    tqb.topic_id = tm.id AND "
        + "    tqb.test_config_id = ta.test_id AND "
        + "    tqa.test_question_id = tqb.question_id AND "
        + "    tqa.is_correct = 0 AND " 
        + "    ta.date_attempted > ?1 "
        + "ORDER BY "
        + "    tm.subject_name ASC, "
        + "    tm.id ASC "
    )
    List<IncorrectTestAnswerRC> getIncorrectTestAnswersRC( Date horizonDate ) ;
}
