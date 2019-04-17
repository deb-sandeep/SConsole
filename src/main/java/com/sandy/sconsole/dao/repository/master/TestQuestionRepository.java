package com.sandy.sconsole.dao.repository.master;

import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;
import org.springframework.data.repository.query.Param ;

import com.sandy.sconsole.api.jeetest.QBTopicInsight ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;

public interface TestQuestionRepository 
    extends CrudRepository<TestQuestion, Integer> {
    
    @Query(   "SELECT q "
            + "FROM TestQuestion q "
            + "WHERE "
            +   "q.subject.name IN :subjectNames" )
    List<TestQuestion> findForSubjects( @Param( "subjectNames" ) String[] subjectNames ) ;
    
    @Query(   "SELECT q "
            + "FROM TestQuestion q "
            + "WHERE "
            +   "q.topic.id IN :topicIds" )
    List<TestQuestion> findForTopics( @Param( "topicIds" ) Integer[] topicIds ) ;
    
    @Query(   "SELECT q "
            + "FROM TestQuestion q "
            + "WHERE "
            +   "q.book.id IN :bookIds" )
    List<TestQuestion> findForBooks( @Param( "bookIds" ) Integer[] bookIds ) ;
    
    @Query(   "SELECT q "
            + "FROM TestQuestion q "
            + "WHERE "
            +   "q.questionType IN :questionTypes" )
    List<TestQuestion> findForQuestionTypes( @Param( "questionTypes" ) String[] questionTypes ) ;
    
    @Query(   "SELECT q "
            + "FROM TestQuestion q "
            + "WHERE "
            +   "q.attempted = false" )
    List<TestQuestion> findUnattempted() ;
    
    @Query(   "SELECT q "
            + "FROM TestQuestion q "
            + "WHERE "
            +   "q.synched = false" )
    List<TestQuestion> findUnsynched() ;

    @Query( nativeQuery=true,
            value =   
              "select " 
            + "    tm.subject_name as subjectName, "
            + "    tm.topic_name as topicName, "
            + "    count( mqm.id ) as totalQuestions, "
            + "    sum( mqm.attempted ) as attemptedQuestions "
            + "from "
            + "    topic_master tm left join "
            + "    mocktest_question_master mqm "
            + "on "
            + "    tm.id = mqm.topic_id " 
            + "group by "
            + "    tm.topic_name " 
            + "order by "
            + "    tm.subject_name, "
            + "    tm.id asc" )
    public List<QBTopicInsight> getTopicBasedInsight() ;
    
}
