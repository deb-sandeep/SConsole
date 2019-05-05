package com.sandy.sconsole.dao.entity;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.JoinColumn ;
import javax.persistence.ManyToOne ;
import javax.persistence.Table ;

import com.sandy.sconsole.dao.entity.master.Subject ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.entity.master.Topic ;

@Entity
@Table( name = "test_question_binding" )
public class TestQuestionBinding {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;

    @ManyToOne
    @JoinColumn( name="test_config_id" )
    private TestConfigIndex testConfig ;
    
    @ManyToOne
    @JoinColumn( name="subject_name" )
    private Subject subject ;
    
    @ManyToOne
    @JoinColumn( name="topic_id" )
    private Topic topic ;
    
    @ManyToOne
    @JoinColumn( name="question_id" )
    private TestQuestion question ;
    
    private Integer sequence = null ;
    
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
    
    public TestQuestion getQuestion() {
        return question ;
    }
    public void setQuestion( TestQuestion question ) {
        this.question = question ;
    }
    
    public Integer getSequence() {
        return sequence ;
    }
    public void setSequence( Integer sequence ) {
        this.sequence = sequence ;
    }
}
