package com.sandy.sconsole.dao.repository.master;

import java.util.List ;

import org.springframework.data.jpa.repository.Query ;
import org.springframework.data.repository.CrudRepository ;
import org.springframework.data.repository.query.Param ;

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
            value = "select "
            + "    tm.id as topicId, "
            + "    tm.subject_name as subjectName, "
            + "    tm.topic_name as topicName, "
            + "    mqm.question_type as questionType, "
            + "    count( mqm.id ) as totalQuestions, "
            + "    sum( mqm.attempted ) as attemptedQuestions, "
            + "    count( tqb.id ) as assignedQuestions "
            + "from "
            + "    topic_master tm "
            + "    left join mocktest_question_master mqm "
            + "      on "
            + "            tm.id = mqm.topic_id "
            + "    left join test_question_binding tqb "
            + "      on "
            + "            tm.id = tqb.topic_id and "
            + "            mqm.id = tqb.question_id "
            + "group by "
            + "    tm.topic_name, "
            + "    mqm.question_type "
            + "order by "
            + "    tm.subject_name, "
            + "    tm.id asc " )
    public List<Object[]> getTopicBasedInsight() ;
    
    public TestQuestion findByHash( String hash ) ;
    
    @Query(   "SELECT q "
            + "FROM TestQuestion q "
            + "WHERE "
            +   "q.topic.id = :topicId AND "
            +   "q.id NOT IN ( " 
            +   "    SELECT " 
            +   "      tqb.question.id "
            +   "    FROM "
            +   "      TestQuestionBinding tqb " 
            +   ")" )
    public List<TestQuestion> findActiveQuestionsForTopic( 
                                       @Param( "topicId" ) Integer topicId ) ;
    
    @Query(   "SELECT q "
            + "FROM TestQuestion q "
            + "WHERE "
            +   "q.topic.id = :topicId AND "
            +   "q.book.id = :bookId AND "
            +   "q.questionRef like :qRef%" )
    public List<TestQuestion> findQuestionsWithQRef( 
                                       @Param( "topicId" ) Integer topicId ,
                                       @Param( "bookId" ) Integer bookId,
                                       @Param( "qRef" ) String qRef ) ;
}
