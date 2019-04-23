package com.sandy.sconsole.analysis;

import java.util.Calendar ;
import java.util.HashMap ;
import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.util.DayTickListener ;
import com.sandy.sconsole.dao.entity.ProblemAttemptAnalysis ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.ProblemAttemptAnalysisRepository ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;

/** Problem Attempt Analysis Generator */
public class PAAGenerator implements DayTickListener {

    private static final Logger log = Logger.getLogger( PAAGenerator.class ) ;
    
    private ProblemAttemptAnalysisRepository paaRepo = null ;
    private TopicRepository topicRepo = null ;
    
    private Map<Integer, Map<String, ProblemAttemptAnalysis>> paaLookup = null ;
    
    public PAAGenerator( ProblemAttemptAnalysisRepository paaRepo,
                         TopicRepository topicRepo ) {
        this.paaRepo = paaRepo ;
        this.topicRepo = topicRepo ;
        this.paaLookup = new LinkedHashMap<>() ;
    }
    
    public boolean isAsync() {
        return true ;
    }
    
    @Override
    public void dayTicked( Calendar instance ) {
        try {
            analyzeProblemAttempts() ;
        }
        catch( Exception e ) {
            log.error( "Error processing problem attempt analysis", e ) ;
        }
    }

    public void analyzeProblemAttempts() {
        
        log.debug( "Analyzing problem attempts and creating reporting tables" ) ;
        refreshPAARecordsAndLoadPAAMap() ;
        
        Map<String, ProblemAttemptAnalysis> paaPTypeMap = null ;
        Topic topic = null ;
        for( Integer topicId : paaLookup.keySet() ) {
            paaPTypeMap = paaLookup.get( topicId ) ;
            topic = topicRepo.findById( topicId ).get() ;
            refreshAnalysis( topic, paaPTypeMap ) ;
        }
    }
    
    private void refreshPAARecordsAndLoadPAAMap() {
        
        log.debug( "Refreshing topics in the analysis table." ) ;
        List<Object[]> data = topicRepo.getActiveProblemCountByTopic() ;
        for( Object tupule[] : data ) {
            Topic   topic             = ( Topic  )tupule[0] ;
            String  problemType       = ( String )tupule[1] ;
            Integer numActiveProblems = (( Long  )tupule[2]).intValue() ;
            
            ProblemAttemptAnalysis paa = null ;
            Map<String, ProblemAttemptAnalysis> hashMap = null ;
            
            paa = paaRepo.findByTopicIdAndProblemType( topic.getId(), problemType ) ;
            
            if( paa == null ) {
                
                log.debug( "Saving new paa record. " + 
                           "Topic = " + topic.getId() + ", " +
                           "ProblemType = " + problemType ) ;
                
                paa = new ProblemAttemptAnalysis() ;
                paa.setTopic( topic ) ;
                paa.setProblemType( problemType ) ;
                paa.setTotalNumProblems( numActiveProblems.intValue() ) ;
                paa.setSubject( topic.getSubject() ) ;
                paa.setTopicName( topic.getTopicName() ) ;
                paaRepo.save( paa ) ;
            }
            else {
                if( paa.getTotalNumProblems() != numActiveProblems.intValue() ) {
                    paa.setTotalNumProblems( numActiveProblems ) ;
                    
                    log.debug( "Updating new paa record. " + 
                               "Topic = " + topic.getId() + ", " +
                               "ProblemType = " + problemType + ", " + 
                               "Num problems = " + numActiveProblems ) ;
                    paaRepo.save( paa ) ;
                }
            }
            
            hashMap = paaLookup.get( topic.getId() ) ;
            if( hashMap == null ) {
                hashMap = new HashMap<>() ;
                paaLookup.put( topic.getId(), hashMap ) ;
            }
            hashMap.put( problemType, paa ) ;
        }
        log.debug( "PAA lookup loaded." ) ;
    }
    
    private void refreshAnalysis( Topic topic, 
                                  Map<String, ProblemAttemptAnalysis> paaMap ) {
        
        log.debug( "Refreshing problem analysis for topic " + topic.getTopicName() ) ;

        
    }
}
