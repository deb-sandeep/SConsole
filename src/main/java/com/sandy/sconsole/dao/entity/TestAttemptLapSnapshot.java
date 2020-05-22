package com.sandy.sconsole.dao.entity;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.Table ;

@Entity
@Table( name = "test_attempt_lap_snapshot" )
public class TestAttemptLapSnapshot {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    private Integer testAttemptId = null ;
    private Integer questionId = null ;
    private String  lapName = null ;
    private Integer timeSpent = 0 ;
    private String  attemptStatus = null ;

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
    
    public String getAttemptStatus() {
        return attemptStatus ;
    }
    public void setAttemptStatus( String attemptStatus ) {
        this.attemptStatus = attemptStatus ;
    }
    
    public Integer getTimeSpent() {
        return timeSpent ;
    }
    public void setTimeSpent( Integer timeSpent ) {
        this.timeSpent = timeSpent ;
    }
    
    public Integer getQuestionId() {
        return questionId ;
    }
    public void setQuestionId( Integer questionId ) {
        this.questionId = questionId ;
    }
    
    public String getLapName() {
        return lapName ;
    }
    public void setLapName( String lapName ) {
        this.lapName = lapName ;
    }
    
    public String toString() {
        return testAttemptId + "," + 
               questionId + "," + 
               lapName + "," + 
               timeSpent + "," + 
               attemptStatus ;
    }
}
