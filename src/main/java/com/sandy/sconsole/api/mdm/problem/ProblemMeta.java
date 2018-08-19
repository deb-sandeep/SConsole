package com.sandy.sconsole.api.mdm.problem;

public class ProblemMeta {
    
    private String subjectName   = null ;
    private String topicName     = null ;
    private String bookName      = null ;
    private String chapterId     = null ;
    
    private String exerciseName    = null ;
    private String exerciseSubName = null ;
    private String problemTag      = null ;
    
    public String getSubjectName() {
        return subjectName ;
    }
    
    public void setSubjectName( String subjectName ) {
        this.subjectName = subjectName ;
    }
    
    public String getTopicName() {
        return topicName ;
    }
    
    public void setTopicName( String topicName ) {
        this.topicName = topicName ;
    }
    
    public String getBookName() {
        return bookName ;
    }
    
    public void setBookName( String bookName ) {
        this.bookName = bookName ;
    }
    
    public String getChapterId() {
        return chapterId ;
    }
    
    public void setChapterId( String id ) {
        this.chapterId = id ;
    }
    
    public String getExerciseName() {
        return exerciseName ;
    }
    
    public void setExerciseName( String exerciseName ) {
        this.exerciseName = exerciseName ;
    }
    
    public String getExerciseSubName() {
        return exerciseSubName ;
    }
    
    public void setExerciseSubName( String exerciseSubName ) {
        this.exerciseSubName = exerciseSubName ;
    }
    
    public String getProblemTag() {
        return problemTag ;
    }

    public void setProblemTag( String problemTag ) {
        this.problemTag = problemTag ;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer() ;
        buffer.append( subjectName ).append( "@" )
              .append( topicName ).append( "@" )
              .append( bookName ).append( "@" )
              .append( chapterId ).append( "@" )
              .append( exerciseName ).append( "@" ) ;
        
        if( exerciseSubName != null ) {
            buffer.append( exerciseSubName ).append( "@" ) ;
        }
        
        buffer.append( problemTag ) ;
        return buffer.toString() ;
    }
}
