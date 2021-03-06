package com.sandy.sconsole.dao.entity.master;

import javax.persistence.* ;

@Entity
@Table( name = "problem_master" )
public class Problem {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    @ManyToOne
    private Topic topic ;
    
    @ManyToOne
    private Book book ;
    
    private String chapterId ;
    private String exerciseName ;
    private String problemTag ;
    private String problemType ;
    private Boolean solved = false ;
    private Boolean active = true ;
    private Boolean pigeoned = false ;
    private Boolean redo = false ;
    private Boolean skipped = false ;
    private Boolean starred = false ;
    private Boolean ignored = false ;
    private Integer revisionCount = 0 ;
    
    public Integer getId() { return id ; }
    public void setId( Integer id ) { this.id = id ; }
    
    public Topic getTopic() { return topic ; }
    public void setTopic( Topic topic ) { this.topic = topic ; }
    
    public Book getBook() { return book ; }
    public void setBook( Book book ) { this.book = book ; }
    
    public String getExerciseName() { return exerciseName ; }
    public void setExerciseName( String exerciseName ) {
        this.exerciseName = exerciseName ;
    }
    
    public String getProblemTag() { return problemTag ; }
    public void setProblemTag( String tag ) { this.problemTag = tag ; }
    
    public String getChapterId() { return chapterId ; }
    public void setChapterId( String id ) { this.chapterId = id ; }

    public Boolean getSolved() { return solved ; }
    public void setSolved( Boolean solved ) { this.solved = solved ; }
    
    public Boolean getActive() { return active ; }
    public void setActive( Boolean active ) { this.active = active ; }
    
    public Boolean getPigeoned() { return pigeoned ; }
    public void setPigeoned( Boolean pigeoned ) { this.pigeoned = pigeoned ; }
    
    public Boolean getIgnored() { return ignored ; }
    public void setIgnored( Boolean ignored ) { this.ignored = ignored ; }
    
    public Boolean getRedo() { return redo ; }
    public void setRedo( Boolean redo ) { this.redo = redo ; }
    
    public Boolean getSkipped() { return skipped ; }
    public void setSkipped( Boolean skipped ) { this.skipped = skipped ; }
    
    public Boolean getStarred() { return starred ; }
    public void setStarred( Boolean starred ) { this.starred = starred ; }
    
    public void setProblemType( String type ) { this.problemType = type ; }
    public String getProblemType() { return this.problemType ; }
    
    public Integer getRevisionCount() { return revisionCount ; }
    public void setRevisionCount( Integer revisionCount ) {
        this.revisionCount = revisionCount ;
    }
    
    @Override
    public boolean equals( Object obj ) {
        if( !(obj instanceof Problem) ) {
            return false ;
        }
        return ((Problem)obj).getId().equals( id ) ;
    }
    
    public String toString() {
        return id.toString() + "/" + 
               chapterId + "/" + 
               exerciseName + "/" + 
               problemTag ;
    }
}
