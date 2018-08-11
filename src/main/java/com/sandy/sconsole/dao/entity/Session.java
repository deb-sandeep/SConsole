package com.sandy.sconsole.dao.entity;

import java.sql.Timestamp ;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.ManyToOne ;
import javax.persistence.Table ;

import com.sandy.sconsole.dao.entity.master.Topic ;

@Entity
@Table( name = "session" )
public class Session {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    @ManyToOne
    private Topic topic ;
    
    private String sessionType ;
    private Timestamp startTime ;
    private Timestamp endTime ;
    private Integer duration ;
    private Integer effectiveDuration ;
    
    public Integer getId() { return id ; }
    public void setId( Integer id ) { this.id = id ; }
    
    public Timestamp getStartTime() { return startTime ; }
    public void setStartTime( Timestamp startTime ) { this.startTime = startTime ; }
    
    public Timestamp getEndTime() { return endTime ; }
    public void setEndTime( Timestamp endTime ) { this.endTime = endTime ; }
    
    public Integer getDuration() { return duration ; }
    public void setDuration( Integer duration ) { this.duration = duration ; }
    
    public Integer getEffectiveDuration() { return effectiveDuration ; }
    public void setEffectiveDuration( Integer effectiveDuration ) {
        this.effectiveDuration = effectiveDuration ;
    }
    
    public Topic getTopic() { return topic ; }
    public void setTopic( Topic topic ) { this.topic = topic ; }
    
    public String getSessionType() { return sessionType ; }
    public void setSessionType( String sessionType ) {
        this.sessionType = sessionType ;
    }
}
