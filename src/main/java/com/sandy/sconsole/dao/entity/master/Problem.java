package com.sandy.sconsole.dao.entity.master;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.ManyToOne ;
import javax.persistence.Table ;

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
}
