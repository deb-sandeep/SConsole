package com.sandy.sconsole.dao.entity;

import java.sql.* ;

import javax.persistence.* ;

import com.sandy.sconsole.dao.entity.master.* ;

@Entity
@Table( name = "problem_attempt" )
public class ProblemAttempt {
    
    public static final String OUTCOME_SKIP   = "Skip" ;
    public static final String OUTCOME_SOLVED = "Solved" ;
    public static final String OUTCOME_REDO   = "Redo" ;
    public static final String OUTCOME_PIGEON = "Pigeon" ;
    public static final String OUTCOME_IGNORE = "Ignore" ;
    public static final String OUTCOME_MOVE   = "Move" ;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    @ManyToOne
    @JoinColumn( name="session_id" )
    private Session session ;

    @ManyToOne
    @JoinColumn( name="problem_id" )
    private Problem problem ;
    
    private Timestamp startTime ;
    private Timestamp endTime ;
    private Integer duration ;
    private Integer projectedDuration = -1 ;
    private String outcome ;
    
    public Integer getId() { return id ; }
    public void setId( Integer id ) {
        this.id = id ;
    }

    public Session getSession() { return session ; } 
    public void setSession( Session session ) {
        this.session = session ;
    }

    public Problem getProblem() { return problem ; }
    public void setProblem( Problem problem ) {
        this.problem = problem ;
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
    
    public Integer getProjectedDuration() { return projectedDuration ; }
    public void setProjectedDuration( Integer projectedDuration ) {
        this.projectedDuration = projectedDuration ;
    }
    
    public String getOutcome() { return outcome ; }
    public void setOutcome( String outcome ) {
        this.outcome = outcome ;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer() ;
        buffer.append( "id        = " ).append( id              ).append( "\n" ) 
              .append( "session   = " ).append( session.getId() ).append( "\n" ) 
              .append( "problem   = " ).append( problem         ).append( "\n" ) 
              .append( "startTime = " ).append( startTime       ).append( "\n" ) 
              .append( "endTime   = " ).append( endTime         ).append( "\n" ) 
              .append( "duration  = " ).append( duration        ).append( "\n" ) 
              .append( "outcome   = " ).append( outcome         ).append( "\n" ) ;
        return buffer.toString() ;
    }
}
