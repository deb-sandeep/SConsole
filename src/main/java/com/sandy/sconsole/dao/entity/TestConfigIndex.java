package com.sandy.sconsole.dao.entity;

import java.sql.Timestamp ;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.Table ;

import org.hibernate.annotations.Formula ;

@Entity
@Table( name = "test_config_index" )
public class TestConfigIndex {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;

    private Integer numPhyQuestions = 0 ;
    private Integer numChemQuestions = 0 ;
    private Integer numMathQuestions = 0 ;
    
    // Note that duration is in minutes.
    private Integer duration = 0 ;
    
    private Timestamp creationDate         = null ;
    private Timestamp lastUpdateDate       = null ;
    
    @Formula( "(SELECT count(*) FROM test_attempt ta WHERE ta.test_id = id)" )
    private Boolean attempted = false ;
    
    public Integer getId() {
        return id ;
    }
    public void setId( Integer id ) {
        this.id = id ;
    }
    
    public Integer getNumPhyQuestions() {
        return numPhyQuestions ;
    }
    public void setNumPhyQuestions( Integer numPhyQuestions ) {
        this.numPhyQuestions = numPhyQuestions ;
    }
    
    public Integer getNumChemQuestions() {
        return numChemQuestions ;
    }
    public void setNumChemQuestions( Integer numChemQuestions ) {
        this.numChemQuestions = numChemQuestions ;
    }
    
    public Integer getNumMathQuestions() {
        return numMathQuestions ;
    }
    public void setNumMathQuestions( Integer numMathQuestions ) {
        this.numMathQuestions = numMathQuestions ;
    }
    
    public Integer getDuration() {
        return duration ;
    }
    public void setDuration( Integer duration ) {
        this.duration = duration ;
    }
    
    public Timestamp getCreationDate() {
        return creationDate ;
    }
    public void setCreationDate( Timestamp time ) {
        this.creationDate = time ;
    }
    
    public Timestamp getLastUpdateDate() {
        return lastUpdateDate ;
    }
    public void setLastUpdateDate( Timestamp time ) {
        this.lastUpdateDate = time ;
    }

    public Boolean getAttempted() {
        return attempted ;
    }
    public void setAttempted( Boolean attempted ) {
        this.attempted = attempted ;
    }
}
