package com.sandy.sconsole.api.jeetest.exam;

public class GraceScoreInput {

    private Integer testAttemptId = null ;
    private Integer testQuestionId = null ;
    private String  preGraceAttemptState = null ;
    private Integer preGraceScore = null ;
    private String  postGraceAttemptState = null ;
    private Integer postGraceScore = null ;
    
    public Integer getTestAttemptId() {
        return testAttemptId ;
    }
    public void setTestAttemptId( Integer testAttemptId ) {
        this.testAttemptId = testAttemptId ;
    }
    
    public Integer getTestQuestionId() {
        return testQuestionId ;
    }
    public void setTestQuestionId( Integer testQuestionId ) {
        this.testQuestionId = testQuestionId ;
    }
    
    public String getPreGraceAttemptState() {
        return preGraceAttemptState ;
    }
    public void setPreGraceAttemptState( String preGraceAttemptState ) {
        this.preGraceAttemptState = preGraceAttemptState ;
    }
    
    public Integer getPreGraceScore() {
        return preGraceScore ;
    }
    public void setPreGraceScore( Integer preGraceScore ) {
        this.preGraceScore = preGraceScore ;
    }
    
    public String getPostGraceAttemptState() {
        return postGraceAttemptState ;
    }
    public void setPostGraceAttemptState( String postGraceAttemptState ) {
        this.postGraceAttemptState = postGraceAttemptState ;
    }
    
    public Integer getPostGraceScore() {
        return postGraceScore ;
    }
    public void setPostGraceScore( Integer postGraceScore ) {
        this.postGraceScore = postGraceScore ;
    }
}
