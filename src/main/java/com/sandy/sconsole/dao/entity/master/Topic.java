package com.sandy.sconsole.dao.entity.master;

import java.sql.Timestamp ;

import javax.persistence.* ;

@Entity
@Table( name = "topic_master" )
public class Topic {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    @ManyToOne
    @JoinColumn( name="subject_name" )
    private Subject subject ;
    
    @Column( name = "topic_name" )
    private String topicName ;
    
    private String section ;
    private Timestamp burnStart ;
    private Timestamp burnCompletion ;
    private Integer streamNumber ;
    private Boolean active = Boolean.FALSE ;
    
    public Integer getId() { return id ; }
    public void setId( Integer id ) { this.id = id ; }
    
    public Subject getSubject() { return subject ; }
    public void setSubject( Subject subject ) { this.subject = subject ; }
    
    public String getTopicName() { return topicName ; }
    public void setTopicName( String topicName ) { this.topicName = topicName ; }

    public String getSection() { return section ; }
    public void setSection( String section ) { this.section = section ; }
    
    public Timestamp getBurnStart() { return burnStart ; }
    public void setBurnStart( Timestamp burnStart ) {
        this.burnStart = burnStart ;
    }
    
    public Timestamp getBurnCompletion(){ return burnCompletion ; }
    public void setBurnCompletion( Timestamp ts ) {
        this.burnCompletion = ts ;
    }
    
    public Integer getStreamNumber() { return streamNumber ; }
    public void setStreamNumber( Integer streamNumber ) {
        this.streamNumber = streamNumber ;
    }
    
    public Boolean getActive() { return active ; }
    public void setActive( Boolean active ) {
        this.active = active ;
    }
    
    public String toString() {
        return id + " / " + subject.getName() + " / " + topicName ;
    }
    
    @Override
    public boolean equals( Object obj ) {
        if( obj instanceof Topic ) {
            return ((Topic)obj).id.equals( id ) ;
        }
        return false ;
    }
}
