package com.sandy.sconsole.api.jeetest.qbm;

import java.util.HashMap ;
import java.util.Map ;

public class QBTopicInsight {
    
    private Integer topicId = null ;
    private String subjectName ;
    private String topicName ;
    private Integer totalQuestions = 0 ;
    private Integer attemptedQuestions = 0 ;
    private Integer assignedQuestions = 0 ;
    
    private Map<String, Integer> totalQuestionsByType = new HashMap<>() ;
    private Map<String, Integer> attemptedQuestionsByType = new HashMap<>() ;
    private Map<String, Integer> assignedQuestionsByType = new HashMap<>() ;
    
    public Integer getTopicId() {
        return topicId ;
    }
    public void setTopicId( Integer topicId ) {
        this.topicId = topicId ;
    }
    
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
    
    public Integer getAssignedQuestions() {
        return assignedQuestions ;
    }
    public void setAssignedQuestions( Integer assignedQuestions ) {
        this.assignedQuestions = assignedQuestions ;
    }
    
    public Map<String, Integer> getTotalQuestionsByType() {
        return totalQuestionsByType ;
    }
    public void setTotalQuestionsByType(
            Map<String, Integer> getTotalQuestionsByType ) {
        this.totalQuestionsByType = getTotalQuestionsByType ;
    }
    
    public Map<String, Integer> getAttemptedQuestionsByType() {
        return attemptedQuestionsByType ;
    }
    
    public void setAttemptedQuestionsByType(
            Map<String, Integer> getAttemptedQuestionsByType ) {
        this.attemptedQuestionsByType = getAttemptedQuestionsByType ;
    }
    
    public Map<String, Integer> getAssignedQuestionsByType() {
        return assignedQuestionsByType ;
    }
    public void setAssignedQuestionsByType(
            Map<String, Integer> assignedQuestionsByType ) {
        this.assignedQuestionsByType = assignedQuestionsByType ;
    }
}
