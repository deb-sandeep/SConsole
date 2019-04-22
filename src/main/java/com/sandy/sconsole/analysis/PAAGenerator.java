package com.sandy.sconsole.analysis;

import java.util.Calendar ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.util.DayTickListener ;
import com.sandy.sconsole.dao.entity.ProblemAttemptAnalysis ;
import com.sandy.sconsole.dao.repository.ProblemAttemptAnalysisRepository ;

/** Problem Attempt Analysis Generator */
public class PAAGenerator implements DayTickListener {

    private static final Logger log = Logger.getLogger( PAAGenerator.class ) ;
    
    private ProblemAttemptAnalysisRepository paaRepo = null ;
    
    public PAAGenerator( ProblemAttemptAnalysisRepository paaRepo ) {
        this.paaRepo = paaRepo ;
    }
    
    public void test() {
        Iterable<ProblemAttemptAnalysis> paaIter = paaRepo.findAll() ;
        for( ProblemAttemptAnalysis paa : paaIter ) {
            log.debug( paa ) ;
        }
    }
    
    public boolean isAsync() {
        return true ;
    }
    
    @Override
    public void dayTicked( Calendar instance ) {
    }
}
