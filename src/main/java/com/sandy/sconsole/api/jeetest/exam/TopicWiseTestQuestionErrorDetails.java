package com.sandy.sconsole.api.jeetest.exam;

import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import com.sandy.sconsole.dao.repository.TestQuestionBindingRepository.TopicTestQuestionCount ;

public class TopicWiseTestQuestionErrorDetails {

    private String subjectName  = null ;
    private int    topicId      = -1 ;
    private String topicName    = null ;
    private int    numQuestions = 0 ;
    
    private Map<Integer, String> errorDetails = new HashMap<>() ;
    private Map<String, List<Integer>> rcClusters = new HashMap<>() ;
    
    public TopicWiseTestQuestionErrorDetails( TopicTestQuestionCount ttqc ) {
        this.subjectName = ttqc.getSubjectName() ;
        this.topicId = ttqc.getTopicId() ;
        this.topicName = ttqc.getTopicName() ;
        this.numQuestions = ttqc.getNumQuestions() ;
    }
    
    public String getSubjectName() {
        return subjectName ;
    }
    public void setSubjectName( String subjectName ) {
        this.subjectName = subjectName ;
    }
    
    public int getTopicId() {
        return topicId ;
    }
    public void setTopicId( int topicId ) {
        this.topicId = topicId ;
    }
    
    public String getTopicName() {
        return topicName ;
    }
    public void setTopicName( String topicName ) {
        this.topicName = topicName ;
    }
    
    public int getNumQuestions() {
        return numQuestions ;
    }
    public void setNumQuestions( int numQuestions ) {
        this.numQuestions = numQuestions ;
    }
    
    public Map<Integer, String> getErrorDetails() {
        return errorDetails ;
    }
    public void setErrorDetails( Map<Integer, String> errorDetails ) {
        this.errorDetails = errorDetails ;
    }
    
    public int getNumWrongAnswers() {
        return this.errorDetails.size() ;
    }

    public Map<String, List<Integer>> getRcClusters() {
        return rcClusters ;
    }

    public void setRcClusters( Map<String, List<Integer>> rcClusters ) {
        this.rcClusters = rcClusters ;
    }

    public float computeEfficiency() {
        if( this.numQuestions == 0 ) {
            return 0 ;
        }
        
        int numEfficiencyErrors = 0 ;
        for( String rc : rcClusters.keySet() ) {
            if( rc.equals( "RECOLLECTION" ) || 
                rc.equals( "LATERAL" ) || 
                rc.equals( "CONCEPT" ) || 
                rc.equals( "WTF_LATERAL" ) || 
                rc.equals( "ALIEN_CONCEPT" ) || 
                rc.equals( "WTF" ) || 
                rc.equals( "-UNASSIGNED-" ) ) {
                
                numEfficiencyErrors += rcClusters.get( rc ).size() ;
            }
        }
        float eff = ((float)( numQuestions - numEfficiencyErrors )/numQuestions)*100 ;
        return (float)Math.floor( eff * 100 ) / 100 ;
    }
}
