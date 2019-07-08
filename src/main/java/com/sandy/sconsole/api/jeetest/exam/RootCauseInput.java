package com.sandy.sconsole.api.jeetest.exam;

public class RootCauseInput {

    private Integer testAttemptId = null ;
    private Integer testQuestionId = null ;
    private String  rootCause = null ;
    
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
    
    public String getRootCause() {
        return rootCause ;
    }
    public void setRootCause( String rootCause ) {
        this.rootCause = rootCause ;
    }
}
