package com.sandy.sconsole.dao.entity;

import java.sql.Timestamp ;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.Table ;

@Entity
@Table( name = "session" )
public class Session {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    private String subject ;
    private String topic ;
    private Timestamp startTime ;
    private Timestamp endTime ;
    private Integer duration ;
    private Integer effectiveDuration ;
    
    public Integer getId() { return id ; }
    public void setId( Integer id ) { this.id = id ; }
    
    public String getSubject() { return subject ; }
    public void setSubject( String subject ) { this.subject = subject ; }
    
    public String getTopic() { return topic ; }
    public void setTopic( String topic ) { this.topic = topic ; }
    
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
}
