package com.sandy.sconsole.dao.entity;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.Table ;

@Entity
@Table( name = "test_question_attempt" )
public class TestQuestionAttempt {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    Integer testAttemptId = null ;
    Integer testQuestionId = null ;
    String  attemptStatus = null ;
    String  answerProvided = null ;
    Boolean isCorrect = Boolean.FALSE ;
    String  rootCause = "" ;
    Integer score = 0 ;
    Integer timeSpent = 0 ;

    public Integer getId() {
        return id ;
    }
    public void setId( Integer id ) {
        this.id = id ;
    }
    
    public Integer getTestAttemptId() {
        return testAttemptId ;
    }
    public void setTestAttemptId( Integer testAttemptId ) {
        this.testAttemptId = testAttemptId ;
    }
    
    public Integer getTestQuestionId() {
        return testQuestionId ;
    }
    public void setTestQuestionId( Integer testQuestionId ) {
        this.testQuestionId = testQuestionId ;
    }
    
    public String getAttemptStatus() {
        return attemptStatus ;
    }
    public void setAttemptStatus( String attemptStatus ) {
        this.attemptStatus = attemptStatus ;
    }
    
    public String getAnswerProvided() {
        return answerProvided ;
    }
    public void setAnswerProvided( String answerProvided ) {
        this.answerProvided = answerProvided ;
    }
    
    public Boolean getIsCorrect() {
        return isCorrect ;
    }
    public void setIsCorrect( Boolean isCorrect ) {
        this.isCorrect = isCorrect ;
    }
    
    public String getRootCause() {
        return rootCause ;
    }
    public void setRootCause( String rootCause ) {
        this.rootCause = rootCause ;
    }

    public Integer getScore() {
        return score ;
    }
    public void setScore( Integer score ) {
        this.score = score ;
    }
    
    public Integer getTimeSpent() {
        return timeSpent ;
    }
    public void setTimeSpent( Integer timeSpent ) {
        this.timeSpent = timeSpent ;
    }
}
