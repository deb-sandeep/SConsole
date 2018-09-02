package com.sandy.sconsole.dao.entity;

import java.sql.* ;

import javax.persistence.* ;

import com.sandy.sconsole.dao.entity.master.* ;

@Entity
@Table( name = "session" )
public class Session {
    
    public static enum SessionType {
        LECTURE( "Lecture" ),
        EXERCISE( "Exercise" ),
        THEORY( "Theory" ) ;
        
        private String value = null ;
        
        private SessionType( String value ) {
            this.value = value ;
        }
        
        public String toString() {
            return this.value ;
        }
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    @Convert( converter = SessionTypeConverter.class )
    private SessionType sessionType ;
    
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
    private Integer duration = 0 ;
    private Integer absoluteDuration = 0 ;
    private Integer numSkipped = 0 ;
    private Integer numSolved = 0 ;
    private Integer numRedo = 0 ;
    private Integer numPigeon = 0 ;
    
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
    
    public SessionType getSessionType() { return sessionType ; }
    public void setSessionType( SessionType type ) {
        this.sessionType = type ;
    }
    
    public Book getBook() { return book ; }
    public void setBook( Book book ) {
        this.book = book ;
    }
    
    public Problem getLastProblem() { return lastProblem ; }
    public void setLastProblem( Problem problem ) {
        this.lastProblem = problem ;
    }
    
    public Integer getNumSkipped() { return numSkipped ; }
    public void setNumSkipped( Integer numSkipped ) {
        this.numSkipped = numSkipped ;
    }
    
    public Integer getNumSolved() { return numSolved ; }
    public void setNumSolved( Integer numSolved ) {
        this.numSolved = numSolved ;
    }
    
    public Integer getNumRedo() { return numRedo ; }
    public void setNumRedo( Integer numRedo ) {
        this.numRedo = numRedo ;
    }
    
    public Integer getNumPigeon() { return numPigeon ; }
    public void setNumPigeon( Integer numPigeon ) {
        this.numPigeon = numPigeon ;
    }
    
    public int incrementNumSkipped() { return ++numSkipped; }
    public int incrementNumSolved() { return ++numSolved; }
    public int incrementNumRedo() { return ++numRedo; }
    public int incrementNumPigeon() { return ++numPigeon; }
    
    public Session clone() {
        Session clone = new Session() ;
        clone.setId( id ) ;
        clone.setTopic( topic ) ;
        clone.setBook( book ) ;
        clone.setLastProblem( lastProblem ) ;
        clone.setSessionType( sessionType ) ;
        clone.setNumSkipped( numSkipped ) ;
        clone.setNumSolved( numSolved ) ;
        clone.setNumRedo( numRedo ) ;
        clone.setNumPigeon( numPigeon ) ;
        clone.setDuration( duration ) ;
        clone.setStartTime( startTime ) ;
        clone.setEndTime( endTime ) ;
        return clone ;
    }
    
    public String toString() {
        StringBuffer sbuf = new StringBuffer() ;
        sbuf.append( "   Id          = " ).append( getId()          ).append( "\n" )
            .append( "   Topic       = " ).append( getTopic()       ).append( "\n" )
            .append( "   Book        = " ).append( getBook()        ).append( "\n" )
            .append( "   LastProblem = " ).append( getLastProblem() ).append( "\n" )
            .append( "   SessionType = " ).append( getSessionType() ).append( "\n" )
            .append( "   NumSkipped  = " ).append( getNumSkipped()  ).append( "\n" )
            .append( "   NumSolved   = " ).append( getNumSolved()   ).append( "\n" )
            .append( "   NumRedo     = " ).append( getNumRedo()     ).append( "\n" )
            .append( "   NumPigeon   = " ).append( getNumPigeon()   ).append( "\n" )
            .append( "   Duration    = " ).append( getDuration()    ).append( "\n" ) ;
        
        
        sbuf.append( "   Start time  = " ) ;
        if( getStartTime() != null ) {
            sbuf.append( new Date( getStartTime().getTime() ) ) ;
        }
        else {
            sbuf.append( "null" ) ;
        }
        sbuf.append( "\n" ) ;
        
        sbuf.append( "   End time    = " ) ;
        if( getEndTime() != null ) {
            sbuf.append( new Date( getEndTime().getTime() ) ) ;
        }
        else {
            sbuf.append( "null" ) ;
        }
        sbuf.append( "\n" ) ;
        
        return sbuf.toString() ;
    }
}
