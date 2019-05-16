package com.sandy.sconsole.dao.entity;

import java.sql.Timestamp ;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.Table ;

@Entity
@Table( name = "click_stream_event" )
public class ClickStreamEvent {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    private String eventId = null ;
    private String payload = null ;
    private Integer timeMarker = null ;
    private Timestamp creationTimestamp = null ;

    public Integer getId() {
        return id ;
    }
    public void setId( Integer id ) {
        this.id = id ;
    }
    
    public String getEventId() {
        return eventId ;
    }
    public void setEventId( String eventId ) {
        this.eventId = eventId ;
    }
    
    public String getPayload() {
        return payload ;
    }
    public void setPayload( String payload ) {
        this.payload = payload ;
    }
    
    public Integer getTimeMarker() {
        return timeMarker ;
    }
    public void setTimeMarker( Integer timeMarker ) {
        this.timeMarker = timeMarker ;
    }
    
    public Timestamp getCreationTimestamp() {
        return creationTimestamp ;
    }
    public void setCreationTimestamp( Timestamp creationTimestamp ) {
        this.creationTimestamp = creationTimestamp ;
    }
}
