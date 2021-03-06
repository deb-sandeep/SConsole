package com.sandy.sconsole.dao.entity;

import static com.sandy.sconsole.SConsole.* ;

import java.sql.Timestamp ;
import java.util.Date ;

import javax.persistence.* ;

import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;

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
        
        public static SessionType decode( String input ) {
            if( input.equals( LECTURE.toString() ) ) {
                return LECTURE ;
            }
            else if( input.equals( EXERCISE.toString() ) ) {
                return EXERCISE ;
            }
            else if( input.equals( THEORY.toString() ) ) {
                return THEORY ;
            }
            throw new IllegalArgumentException( "Illegal session type = " + input ) ;
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
    private Integer numIgnored = 0 ;
    
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
    
    public Integer getNumIgnored() { return numIgnored ; }
    public void setNumIgnored( Integer numIgnored ) {
        this.numIgnored = numIgnored ;
    }
    
    public int incrementNumSkipped() { return ++numSkipped; }
    public int incrementNumSolved() { return ++numSolved; }
    public int incrementNumRedo() { return ++numRedo; }
    public int incrementNumPigeon() { return ++numPigeon; }
    public int incrementNumIgnored() { return ++numIgnored; }
    
    public boolean executedToday() {
        
        long sStart = startTime.getTime() ;
        long sEnd   = endTime.getTime() ;
        
        if( ( sStart >= TODAY_FIRST_MIL && sStart <= TODAY_LAST_MIL ) ||
            ( sEnd   >= TODAY_FIRST_MIL && sEnd   <= TODAY_LAST_MIL ) ) {
            return true ;
        }
        return false ;
    }
    
    public Timestamp getTodayStartTime() {
        
        if( !executedToday() ) { return null ; }
        return ( startTime.getTime() < TODAY_FIRST_MIL ) ? TODAY_FIRST_TS : startTime ;
    }
    
    public Timestamp getTodayEndTime() {
        
        if( !executedToday() ) { return null ; }
        return ( endTime.getTime() > TODAY_LAST_MIL ) ? TODAY_LAST_TS : endTime ;
    }
    
    public int getTodayDuration() {
        
        if( !executedToday() ) { return 0 ; }
        
        long sessionStartMil = ( startTime.getTime() < TODAY_FIRST_MIL ) ? 
                                 TODAY_FIRST_MIL : 
                                 startTime.getTime() ;
        
        long sessionEndMil = ( endTime.getTime() > TODAY_LAST_MIL ) ? 
                               TODAY_LAST_MIL : 
                               endTime.getTime() ;
        
        return (int)( sessionEndMil - sessionStartMil ) / 1000 ;
    }
    
    public String getSubjectName() {
        return getTopic().getSubject().getName() ;
    }
    
    public Session clone() {
        Session clone = new Session() ;
        clone.setId( id ) ;
        clone.setTopic( topic ) ;
        clone.setBook( book ) ;
        clone.setLastProblem( lastProblem ) ;
        clone.setSessionType( sessionType ) ;
        clone.setNumSolved( numSolved ) ;
        clone.setNumRedo( numRedo ) ;
        clone.setNumPigeon( numPigeon ) ;
        clone.setNumSkipped( numSkipped ) ;
        clone.setNumIgnored( numIgnored ) ;
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
            .append( "   NumSolved   = " ).append( getNumSolved()   ).append( "\n" )
            .append( "   NumRedo     = " ).append( getNumRedo()     ).append( "\n" )
            .append( "   NumPigeon   = " ).append( getNumPigeon()   ).append( "\n" )
            .append( "   NumSkipped  = " ).append( getNumSkipped()  ).append( "\n" )
            .append( "   NumIgnored  = " ).append( getNumIgnored()  ).append( "\n" )
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
    
    @Override
    public boolean equals( Object obj ) {
        if( !(obj instanceof Session) ) {
            return false ;
        }
        return ((Session)obj).getId() == this.getId() ;
    }
}
