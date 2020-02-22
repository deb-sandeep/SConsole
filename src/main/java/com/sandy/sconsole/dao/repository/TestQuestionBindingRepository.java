package com.sandy.sconsole.dao.repository;

import java.util.Date ;
import java.util.List ;

import javax.transaction.Transactional ;

import org.springframework.data.jpa.repository.Modifying ;
import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.TestQuestionBinding ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.entity.master.Topic ;

public interface TestQuestionBindingRepository 
    extends CrudRepository<TestQuestionBinding, Integer> {
    
    public interface TopicTestQuestionCount {
        String getSubjectName() ;
        Integer getTopicId() ;
        String getTopicName() ;
        Integer getNumQuestions() ;
    }
    
    @Transactional
    @Modifying
    @Query( "DELETE from TestQuestionBinding tqb WHERE tqb.testConfig.id = ?1" )
    void deleteByTestConfigId( Integer testConfigId ) ;
    
    @Query( "SELECT tqb "
          + "FROM TestQuestionBinding tqb "
          + "WHERE tqb.testConfig.id = ?1 "
          + "ORDER BY "
          + "  tqb.subject.name ASC, "
          + "  tqb.sectionIndex ASC, "
          + "  tqb.sequence ASC"
    )
    List<TestQuestionBinding> findAllByTestConfigId( Integer id ) ;

    @Query(   "SELECT DISTINCT tqb.topic "
            + "FROM TestQuestionBinding tqb "
            + "WHERE tqb.testConfig.id = ?1 "
            + "ORDER BY "
            + "  tqb.subject.name ASC, "
            + "  tqb.topic.id ASC"
      )
    List<Topic> getTopicsForTest( Integer id ) ;
    
    @Query( "SELECT tqb.question "
          + "FROM "
          + "   TestQuestionBinding tqb "
          + "WHERE "
          + "   tqb.testConfig.id = ?1 "
          + "ORDER BY "
          + "   tqb.id ASC "
    )
    List<TestQuestion> getTestQuestionsForTestConfig( Integer testConfigId ) ;

    @Query( nativeQuery=true,
            value = 
              "SELECT "
            + "    tm.subject_name as subjectName, "
            + "    tm.id as topicId, "
            + "    tm.topic_name as topicName, "
            + "    count( tqb.question_id ) as numQuestions "
            + "FROM "
            + "    test_question_binding tqb, "
            + "    test_attempt ta, "
            + "    topic_master tm, "
            + "    mocktest_question_master mqm "
            + "WHERE "
            + "    tqb.topic_id = tm.id AND "
            + "    tqb.test_config_id = ta.test_id AND "
            + "    tqb.question_id = mqm.id AND "
            + "    mqm.attempted = 1 AND "
            + "    ta.date_attempted > ?1 "
            + "GROUP BY "
            + "    tm.subject_name, "
            + "    tm.id, "
            + "    tm.topic_name "
            + "ORDER BY "
            + "    tm.subject_name ASC, "
            + "    tm.id ASC "
    )
    List<TopicTestQuestionCount> getTestQuestionCountPerTopic( Date date ) ;
}
