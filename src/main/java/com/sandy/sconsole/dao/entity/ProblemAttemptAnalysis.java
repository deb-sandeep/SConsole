package com.sandy.sconsole.dao.entity;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.JoinColumn ;
import javax.persistence.ManyToOne ;
import javax.persistence.Table ;

import com.sandy.sconsole.dao.entity.master.Subject ;
import com.sandy.sconsole.dao.entity.master.Topic ;

@Entity
@Table( name = "problem_attempt_analysis" )
public class ProblemAttemptAnalysis {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    @ManyToOne
    @JoinColumn( name="subject_name" )
    private Subject subject = null ;
    
    @ManyToOne
    private Topic topic = null ;
    
    private String  problemType          = null ;
    private String  topicName            = null ;
    private Integer totalNumProblems     = 0 ;
    private Integer numProblemsAttempted = 0 ;
    private Integer avgTime              = 0 ;
    private Integer seventyPercentile    = 0 ;
    private Integer eightyPercentile     = 0 ;
    private Float   efficiency           = 0.0F ;
    
    public Integer getId() { return id ; }
    public void setId( Integer id ) { this.id = id ; }
    
    public Subject getSubject() {
        return subject ;
    }
    public void setSubject( Subject subject ) {
        this.subject = subject ;
    }
    
    public Topic getTopic() {
        return topic ;
    }
    public void setTopic( Topic topic ) {
        this.topic = topic ;
    }
    
    public String getProblemType() {
        return problemType ;
    }
    public void setProblemType( String type ) {
        this.problemType = type ;
    }
    
    public String getTopicName() {
        return topicName ;
    }
    public void setTopicName( String topicName ) {
        this.topicName = topicName ;
    }
    
    public Integer getTotalNumProblems() {
        return totalNumProblems ;
    }
    public void setTotalNumProblems( Integer totalNumProblems ) {
        this.totalNumProblems = totalNumProblems ;
    }
    
    public Integer getNumProblemsAttempted() {
        return numProblemsAttempted ;
    }
    public void setNumProblemsAttempted( Integer numProblemsAttempted ) {
        this.numProblemsAttempted = numProblemsAttempted ;
    }
    
    public Integer getAvgTime() {
        return avgTime ;
    }
    public void setAvgTime( Integer avgTime ) {
        this.avgTime = avgTime ;
    }
    
    public Integer getSeventyPercentile() {
        return seventyPercentile ;
    }
    public void setSeventyPercentile( Integer seventyPercentile ) {
        this.seventyPercentile = seventyPercentile ;
    }
    
    public Integer getEightyPercentile() {
        return eightyPercentile ;
    }
    public void setEightyPercentile( Integer eightyPercentile ) {
        this.eightyPercentile = eightyPercentile ;
    }
    
    public Float getEfficiency() {
        return efficiency ;
    }
    public void setEfficiency( Float efficiency ) {
        this.efficiency = efficiency ;
    }
}
