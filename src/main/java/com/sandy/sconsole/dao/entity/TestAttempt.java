package com.sandy.sconsole.dao.entity;

import java.sql.Timestamp ;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.JoinColumn ;
import javax.persistence.ManyToOne ;
import javax.persistence.Table ;

@Entity
@Table( name = "test_attempt" )
public class TestAttempt {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;

    @ManyToOne
    @JoinColumn( name="test_id" )
    private TestConfigIndex testConfig ;
    
    private Integer score = 0 ;
    private Integer timeTaken = 0 ; // In minutes
    
    private Integer numCorrectAnswers = 0 ;
    private Integer numWrongAnswers = 0 ;
    private Integer numNotVisited = 0 ;
    private Integer numNotAnswered = 0 ;
    private Integer numAttempted = 0 ;
    private Integer numMarkedForReview = 0 ;
    private Integer numAnsAndMarkedForReview = 0 ;
    
    private Timestamp dateAttempted = null ;
    
    public Integer getId() {
        return id ;
    }
    public void setId( Integer id ) {
        this.id = id ;
    }
    
    public TestConfigIndex getTestConfig() {
        return testConfig ;
    }
    public void setTestConfig( TestConfigIndex testConfig ) {
        this.testConfig = testConfig ;
    }
    
    public Integer getScore() {
        return score ;
    }
    public void setScore( Integer score ) {
        this.score = score ;
    }
    
    public Integer getTimeTaken() {
        return timeTaken ;
    }
    public void setTimeTaken( Integer timeTaken ) {
        this.timeTaken = timeTaken ;
    }
    
    public Timestamp getDateAttempted() {
        return dateAttempted ;
    }
    public void setDateAttempted( Timestamp dateAttempted ) {
        this.dateAttempted = dateAttempted ;
    }
    
    public Integer getNumCorrectAnswers() {
        return numCorrectAnswers ;
    }
    public void setNumCorrectAnswers( Integer numCorrectAnswers ) {
        this.numCorrectAnswers = numCorrectAnswers ;
    }
    
    public Integer getNumWrongAnswers() {
        return numWrongAnswers ;
    }
    public void setNumWrongAnswers( Integer numWrongAnswers ) {
        this.numWrongAnswers = numWrongAnswers ;
    }
    
    public Integer getNumNotVisited() {
        return numNotVisited ;
    }
    public void setNumNotVisited( Integer numNotVisited ) {
        this.numNotVisited = numNotVisited ;
    }
    
    public Integer getNumNotAnswered() {
        return numNotAnswered ;
    }
    public void setNumNotAnswered( Integer numNotAnswered ) {
        this.numNotAnswered = numNotAnswered ;
    }
    
    public Integer getNumAttempted() {
        return numAttempted ;
    }
    public void setNumAttempted( Integer numAttempted ) {
        this.numAttempted = numAttempted ;
    }
    
    public Integer getNumMarkedForReview() {
        return numMarkedForReview ;
    }
    public void setNumMarkedForReview( Integer numMarkedForReview ) {
        this.numMarkedForReview = numMarkedForReview ;
    }
    
    public Integer getNumAnsAndMarkedForReview() {
        return numAnsAndMarkedForReview ;
    }
    public void setNumAnsAndMarkedForReview( Integer numAnsAndMarkedForReview ) {
        this.numAnsAndMarkedForReview = numAnsAndMarkedForReview ;
    }
}
