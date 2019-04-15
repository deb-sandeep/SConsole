package com.sandy.sconsole.dao.entity.master;

import java.sql.Timestamp ;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.JoinColumn ;
import javax.persistence.ManyToOne ;
import javax.persistence.Table ;

@Entity
@Table( name = "mocktest_question_master" )
public class TestQuestion {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id = -1 ;

    @ManyToOne
    @JoinColumn( name="subject_name" )
    private Subject subject ;
    
    @ManyToOne
    @JoinColumn( name="topic_id" )
    private Topic topic ;
    
    @ManyToOne
    @JoinColumn( name="book_id" )
    private Book book ;
    
    private String    hash                 = null ;
    private String    targetExam           = "MAIN" ;
    private String    questionType         = "SCA" ;
    private String    questionRef          = null ;
    private Integer   lateralThinkingLevel = 2 ;
    private Integer   projectedSolveTime   = 120 ;
    private String    questionText         = null ;
    private String    questionFormattedText= null ;
    private String    answerText           = null ;
    private Boolean   synched              = Boolean.FALSE ;
    private Boolean   attempted            = Boolean.FALSE ;
    private Timestamp creationTime         = null ;
    private Timestamp lastUpdateTime       = null ;
    
    public Integer getId() {
        return id ;
    }
    
    public void setId( Integer id ) {
        this.id = id ;
    }
    
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
    
    public Book getBook() {
        return book ;
    }
    
    public void setBook( Book book ) {
        this.book = book ;
    }
    
    public String getHash() {
        return hash ;
    }
    
    public void setHash( String hash ) {
        this.hash = hash ;
    }
    
    public String getTargetExam() {
        return targetExam ;
    }
    
    public void setTargetExam( String targetExam ) {
        this.targetExam = targetExam ;
    }
    
    public String getQuestionType() {
        return questionType ;
    }
    
    public void setQuestionType( String questionType ) {
        this.questionType = questionType ;
    }
    
    public String getQuestionRef() {
        return questionRef ;
    }
    
    public void setQuestionRef( String questionRef ) {
        this.questionRef = questionRef ;
    }
    
    public Integer getLateralThinkingLevel() {
        return lateralThinkingLevel ;
    }
    
    public void setLateralThinkingLevel( Integer lateralThinkingLevel ) {
        this.lateralThinkingLevel = lateralThinkingLevel ;
    }
    
    public Integer getProjectedSolveTime() {
        return projectedSolveTime ;
    }
    
    public void setProjectedSolveTime( Integer projectedSolveTime ) {
        this.projectedSolveTime = projectedSolveTime ;
    }
    
    public String getQuestionText() {
        return questionText ;
    }
    
    public void setQuestionText( String questionText ) {
        this.questionText = questionText ;
    }
    
    public Timestamp getCreationTime() {
        return creationTime ;
    }
    
    public void setCreationTime( Timestamp creationTime ) {
        this.creationTime = creationTime ;
    }
    
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime ;
    }
    
    public void setLastUpdateTime( Timestamp lastUpdateTime ) {
        this.lastUpdateTime = lastUpdateTime ;
    }

    public String getQuestionFormattedText() {
        return questionFormattedText ;
    }

    public void setQuestionFormattedText( String questionFormattedText ) {
        this.questionFormattedText = questionFormattedText ;
    }

    public String getAnswerText() {
        return answerText ;
    }

    public void setAnswerText( String answerText ) {
        this.answerText = answerText ;
    }

    public Boolean getSynched() {
        return synched ;
    }

    public void setSynched( Boolean synched ) {
        this.synched = synched ;
    }

    public Boolean getAttempted() {
        return attempted ;
    }

    public void setAttempted( Boolean attempted ) {
        this.attempted = attempted ;
    }
}
