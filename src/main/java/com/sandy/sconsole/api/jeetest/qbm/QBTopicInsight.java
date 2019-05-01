package com.sandy.sconsole.api.jeetest.qbm;

import java.util.HashMap ;
import java.util.Map ;

public class QBTopicInsight {
    
    private String subjectName ;
    private String topicName ;
    private Integer totalQuestions = 0 ;
    private Integer attemptedQuestions = 0 ;
    
    private Map<String, Integer> getTotalQuestionsByType = new HashMap<>() ;
    private Map<String, Integer> getAttemptedQuestionsByType = new HashMap<>() ;
    
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
    
    public Integer getTotalQuestions() {
        return totalQuestions ;
    }
    public void setTotalQuestions( Integer totalQuestions ) {
        this.totalQuestions = totalQuestions ;
    }
    
    public Integer getAttemptedQuestions() {
        return attemptedQuestions ;
    }
    public void setAttemptedQuestions( Integer attemptedQuestions ) {
        this.attemptedQuestions = attemptedQuestions ;
    }
    
    public Map<String, Integer> getGetTotalQuestionsByType() {
        return getTotalQuestionsByType ;
    }
    public void setGetTotalQuestionsByType(
            Map<String, Integer> getTotalQuestionsByType ) {
        this.getTotalQuestionsByType = getTotalQuestionsByType ;
    }
    
    public Map<String, Integer> getGetAttemptedQuestionsByType() {
        return getAttemptedQuestionsByType ;
    }
    
    public void setGetAttemptedQuestionsByType(
            Map<String, Integer> getAttemptedQuestionsByType ) {
        this.getAttemptedQuestionsByType = getAttemptedQuestionsByType ;
    }
}
