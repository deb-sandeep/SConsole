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
    
    public Timestamp getDateAttempted() {
        return dateAttempted ;
    }
    public void setDateAttempted( Timestamp dateAttempted ) {
        this.dateAttempted = dateAttempted ;
    }
}
