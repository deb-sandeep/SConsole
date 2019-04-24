package com.sandy.sconsole.analysis;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.commons.math3.stat.StatUtils ;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation ;
import org.apache.commons.math3.stat.descriptive.rank.Percentile ;
import org.apache.log4j.Logger ;
import org.springframework.context.ApplicationContext ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.dao.entity.ProblemAttempt ;
import com.sandy.sconsole.dao.entity.ProblemAttemptAnalysis ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.ProblemAttemptAnalysisRepository ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;

public class PAARecordUpdater {
    
    private static final Logger log = Logger.getLogger( PAARecordUpdater.class ) ;

    private ProblemRepository pRepo = null ;
    private ProblemAttemptRepository paRepo = null ;
    private ProblemAttemptAnalysisRepository paaRepo = null ;
    
    public PAARecordUpdater() {
        ApplicationContext ctx = SConsole.getAppContext() ;

        pRepo   = ctx.getBean( ProblemRepository.class ) ;
        paRepo  = ctx.getBean( ProblemAttemptRepository.class ) ;
        paaRepo = ctx.getBean( ProblemAttemptAnalysisRepository.class ) ;
    }
    
    public void updateAnalysis( Problem problem ) {
        
        String problemType = problem.getProblemType() ;
        Topic topic = problem.getTopic() ;
        int topicId = topic.getId() ;
 
        List<ProblemAttempt> attempts = null ;
        attempts = paRepo.findByTopicIdAndProblemType( topicId, problemType ) ;
        
        ProblemAttemptAnalysis paa = null ;
        paa = paaRepo.findByTopicIdAndProblemType( topicId, problemType ) ;
        
        if( paa == null ) {
            paa = new ProblemAttemptAnalysis() ;
            paa.setTopic( topic ) ;
            paa.setProblemType( problemType ) ;
            paa.setTotalNumProblems( pRepo.findActiveProblemCount( topicId, problemType ) ) ;
            paa.setSubject( topic.getSubject() ) ;
            paa.setTopicName( topic.getTopicName() ) ;
            paaRepo.save( paa ) ;
        }
        
        updateAnalysis( paa, attempts ) ;
    }

    public void updateAnalysis( ProblemAttemptAnalysis paa,
                                List<ProblemAttempt> attempts ) {
        
        log.debug( "Updating attempt analysis for " ) ;
        log.debug( "\ttopic = " + paa.getTopicName() ) ;
        if( attempts == null || attempts.isEmpty() ) {
            log.debug( "\tNo problems attempted. Skipping analysis" ) ;
            return ;
        }
        else {
            log.debug( "\tnumAttempts = " + attempts.size() ) ;
        }
        
        Map<Integer, Integer[]> problemTimeMap = new HashMap<>() ;
        
        for( ProblemAttempt pa : attempts ) {
            Integer   problemId = pa.getProblem().getId() ;
            Integer[] data      = problemTimeMap.get( problemId ) ;
            
            if( data == null ) {
                data = new Integer[] { 0, 0, 0 } ;
                problemTimeMap.put( problemId, data ) ;
            }
            data[0] += pa.getDuration() ;
            data[1] += 1 ;
            if( pa.getOutcome().equals( ProblemAttempt.OUTCOME_SOLVED ) ) {
                data[2] = 1 ;
            }
        }
        
        computeStatsAndUpdateRecord( paa, problemTimeMap ) ;
    }

    private void computeStatsAndUpdateRecord( ProblemAttemptAnalysis paa,
                                              Map<Integer, Integer[]> timeMap ) {
        
        int   totalProblemsAttempted = 0 ;
        int   totalProblemsSolvedInOneAttempt = 0 ;
        int   stddev = 0 ;
        int   average = 0 ;
        int   pctile70 = 0 ;
        int   pctile80 = 0 ;
        float efficiency = 0 ;
        
        List<Double> timeList = new ArrayList<>() ;
        double[] timesArray = null ;
        
        Percentile percentileHelper = new Percentile() ;
        StandardDeviation stddevHelper = new StandardDeviation() ;
        
        // Efficiency - The number of one attempt solved problems by the
        // total number of problems attempted
        totalProblemsAttempted = timeMap.size() ;
        for( Integer[] tupule : timeMap.values() ) {
            if( tupule[2] == 1 ) {
                // The problem is solved
                timeList.add( Double.valueOf( tupule[0] ) ) ;
                if( tupule[1] == 1 ) {
                    // The problem has been solved in one attempt
                    totalProblemsSolvedInOneAttempt++ ;
                }
            }
        }
        
        efficiency = ((float)totalProblemsSolvedInOneAttempt)/totalProblemsAttempted ;
        timesArray = timeList.stream().mapToDouble(d -> d).toArray() ;
        
        pctile70 = (int)percentileHelper.evaluate( timesArray, 70 ) ;
        pctile80 = (int)percentileHelper.evaluate( timesArray, 80 ) ;
        stddev   = (int)stddevHelper.evaluate( timesArray ) ;
        average  = (int)StatUtils.mean( timesArray ) ;
        
        paa.setStddev( stddev ) ;
        paa.setAvgTime( average ) ;
        paa.setEfficiency( efficiency ) ;
        paa.setSeventyPercentile( (int)pctile70 ) ;
        paa.setEightyPercentile( (int)pctile80 ) ;
        paa.setNumProblemsAttempted( totalProblemsAttempted ) ;
        
        paaRepo.save( paa ) ;
    }
}