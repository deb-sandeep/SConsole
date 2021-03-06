package com.sandy.sconsole.dao.entity;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.JoinColumn ;
import javax.persistence.ManyToOne ;
import javax.persistence.Table ;

import com.sandy.sconsole.dao.entity.master.Subject ;
import com.sandy.sconsole.dao.entity.master.Topic ;

@Entity
@Table( name = "problem_attempt_analysis" )
public class ProblemAttemptAnalysis {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    @ManyToOne
    @JoinColumn( name="subject_name" )
    private Subject subject = null ;
    
    @ManyToOne
    private Topic topic = null ;
    
    private String  problemType          = null ;
    private String  topicName            = null ;
    private Integer totalNumProblems     = 0 ;
    private Integer numProblemsAttempted = 0 ;
    private Integer avgTime              = 0 ;
    private Integer stddev               = 0 ;
    private Float   efficiency           = 0.0F ;
    private Integer fiftyPercentile    = 0 ;
    private Integer fiftyFivePercentile     = 0 ;
    private Integer sixtyPercentile    = 0 ;
    private Integer sixtyFivePercentile     = 0 ;
    private Integer seventyPercentile    = 0 ;
    private Integer seventyFivePercentile     = 0 ;
    private Integer eightyPercentile     = 0 ;
    private Integer eightyFivePercentile     = 0 ;
    private Integer ninetyPercentile    = 0 ;
    private Integer ninetyFivePercentile    = 0 ;
    
    public Integer getId() { return id ; }
    public void setId( Integer id ) { this.id = id ; }
    
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
    
    public String getProblemType() {
        return problemType ;
    }
    public void setProblemType( String type ) {
        this.problemType = type ;
    }
    
    public String getTopicName() {
        return topicName ;
    }
    public void setTopicName( String topicName ) {
        this.topicName = topicName ;
    }
    
    public Integer getTotalNumProblems() {
        return totalNumProblems ;
    }
    public void setTotalNumProblems( Integer totalNumProblems ) {
        this.totalNumProblems = totalNumProblems ;
    }
    
    public Integer getNumProblemsAttempted() {
        return numProblemsAttempted ;
    }
    public void setNumProblemsAttempted( Integer numProblemsAttempted ) {
        this.numProblemsAttempted = numProblemsAttempted ;
    }
    
    public Integer getAvgTime() {
        return avgTime ;
    }
    public void setAvgTime( Integer avgTime ) {
        this.avgTime = avgTime ;
    }
    
    public Integer getStddev() {
        return stddev ;
    }
    public void setStddev( Integer stddev ) {
        this.stddev = stddev ;
    }

    public Float getEfficiency() {
        return efficiency ;
    }
    public void setEfficiency( Float efficiency ) {
        this.efficiency = efficiency ;
    }
    
    public Integer getFiftyPercentile() {
        return fiftyPercentile ;
    }
    public void setFiftyPercentile( Integer fiftyPercentile ) {
        this.fiftyPercentile = fiftyPercentile ;
    }
    
    public Integer getFiftyFivePercentile() {
        return fiftyFivePercentile ;
    }
    public void setFiftyFivePercentile( Integer fiftyFivePercentile ) {
        this.fiftyFivePercentile = fiftyFivePercentile ;
    }
    
    public Integer getSixtyPercentile() {
        return sixtyPercentile ;
    }
    public void setSixtyPercentile( Integer sixtyPercentile ) {
        this.sixtyPercentile = sixtyPercentile ;
    }
    
    public Integer getSixtyFivePercentile() {
        return sixtyFivePercentile ;
    }
    public void setSixtyFivePercentile( Integer sixtyFivePercentile ) {
        this.sixtyFivePercentile = sixtyFivePercentile ;
    }
    
    public Integer getSeventyPercentile() {
        return seventyPercentile ;
    }
    public void setSeventyPercentile( Integer seventyPercentile ) {
        this.seventyPercentile = seventyPercentile ;
    }
    
    public Integer getSeventyFivePercentile() {
        return seventyFivePercentile ;
    }
    public void setSeventyFivePercentile( Integer seventyFivePercentile ) {
        this.seventyFivePercentile = seventyFivePercentile ;
    }
    
    public Integer getEightyPercentile() {
        return eightyPercentile ;
    }
    public void setEightyPercentile( Integer eightyPercentile ) {
        this.eightyPercentile = eightyPercentile ;
    }
    
    public Integer getEightyFivePercentile() {
        return eightyFivePercentile ;
    }
    public void setEightyFivePercentile( Integer eightyFivePercentile ) {
        this.eightyFivePercentile = eightyFivePercentile ;
    }
    
    public Integer getNinetyPercentile() {
        return ninetyPercentile ;
    }
    public void setNinetyPercentile( Integer ninetyPercentile ) {
        this.ninetyPercentile = ninetyPercentile ;
    }
    
    public Integer getNinetyFivePercentile() {
        return ninetyFivePercentile ;
    }
    public void setNinetyFivePercentile( Integer ninetyFivePercentile ) {
        this.ninetyFivePercentile = ninetyFivePercentile ;
    }

    public int getPercentileThreshold() {
        
        float efficiency = getEfficiency() ;
        
        if( efficiency < 50 ) {
            return 0 ;
        }
        else if( efficiency >= 50 && efficiency < 55 ) {
            return getFiftyPercentile() ;
        }
        else if( efficiency >= 55 && efficiency < 60 ) {
            return getFiftyFivePercentile() ;
        }
        else if( efficiency >= 60 && efficiency < 65 ) {
            return getSixtyPercentile() ;
        }
        else if( efficiency >= 65 && efficiency < 70 ) {
            return getSixtyFivePercentile() ;
        }
        else if( efficiency >= 70 && efficiency < 75 ) {
            return getSeventyPercentile() ;
        }
        else if( efficiency >= 75 && efficiency < 80 ) {
            return getSeventyFivePercentile() ;
        }
        else if( efficiency >= 80 && efficiency < 85 ) {
            return getEightyPercentile() ;
        }
        else if( efficiency >= 85 && efficiency < 90 ) {
            return getEightyFivePercentile() ;
        }
        else if( efficiency >= 85 && efficiency < 90 ) {
            return getNinetyPercentile() ;
        }
        return getNinetyFivePercentile() ;
    }
}
