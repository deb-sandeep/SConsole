package com.sandy.sconsole.dao.entity;

import java.sql.* ;

import javax.persistence.* ;

import com.sandy.sconsole.dao.entity.master.* ;

@Entity
@Table( name = "session" )
public class Session {
    
    public static final String TYPE_LECTURE = "Lecture" ;
    public static final String TYPE_EXERCISE = "Exercise" ;
    public static final String TYPE_THEORY = "Theory" ;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    private String sessionType ;
    
    @ManyToOne
    @JoinColumn( name="topic_id" )
    private Topic topic ;
    
    @ManyToOne
    @JoinColumn( name="book_id" )
    private Book book ;
    
    @ManyToOne
    @JoinColumn( name="last_problem_id" )
    private Problem lastProblem ;
    
    private Timestamp startTime ;
    private Timestamp endTime ;
    private Integer duration ;
    private Integer absoluteDuration ;
    
    public Integer getId() { return id ; }
    public void setId( Integer id ) { 
        this.id = id ; 
    }
    
    public Timestamp getStartTime() { return startTime ; }
    public void setStartTime( Timestamp startTime ) { 
        this.startTime = startTime ; 
    }
    
    public Timestamp getEndTime() { return endTime ; }
    public void setEndTime( Timestamp endTime ) { 
        this.endTime = endTime ; 
    }
    
    public Integer getDuration() { return duration ; }
    public void setDuration( Integer duration ) { 
        this.duration = duration ; 
    }
    
    public Integer getAbsoluteDuration() { return absoluteDuration ; }
    public void setAbsoluteDuration( Integer effectiveDuration ) {
        this.absoluteDuration = effectiveDuration ;
    }
    
    public Topic getTopic() { return topic ; }
    public void setTopic( Topic topic ) { 
        this.topic = topic ; 
    }
    
    public String getSessionType() { return sessionType ; }
    public void setSessionType( String sessionType ) {
        this.sessionType = sessionType ;
    }
    
    public Book getBook() { return book ; }
    public void setBook( Book book ) {
        this.book = book ;
    }
    
    public Problem getLastProblem() { return lastProblem ; }
    public void setLastProblem( Problem lastProblem ) {
        this.lastProblem = lastProblem ;
    }
}
