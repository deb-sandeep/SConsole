package com.sandy.sconsole.dao.entity.master;

import javax.persistence.Column ;
import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.JoinColumn ;
import javax.persistence.ManyToOne ;
import javax.persistence.Table ;

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
    
    public Integer getId() { return id ; }
    public void setId( Integer id ) { this.id = id ; }
    
    public Subject getSubject() { return subject ; }
    public void setSubject( Subject subject ) { this.subject = subject ; }
    
    public String getTopicName() { return topicName ; }
    public void setTopicName( String topicName ) { this.topicName = topicName ; }

    public String getSection() { return section ; }
    public void setSection( String section ) { this.section = section ; }
}
