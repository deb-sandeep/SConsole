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
    
    static final Logger log = Logger.getLogger( PAARecordUpdater.class ) ;

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
        
        if( attempts == null || attempts.isEmpty() ) {
            return ;
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
        int   pctile50 = 0 ;
        int   pctile55 = 0 ;
        int   pctile60 = 0 ;
        int   pctile65 = 0 ;
        int   pctile70 = 0 ;
        int   pctile75 = 0 ;
        int   pctile80 = 0 ;
        int   pctile85 = 0 ;
        int   pctile90 = 0 ;
        int   pctile95 = 0 ;
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
        
        pctile50 = (int)percentileHelper.evaluate( timesArray, 50 ) ;
        pctile55 = (int)percentileHelper.evaluate( timesArray, 55 ) ;
        pctile60 = (int)percentileHelper.evaluate( timesArray, 60 ) ;
        pctile65 = (int)percentileHelper.evaluate( timesArray, 65 ) ;
        pctile70 = (int)percentileHelper.evaluate( timesArray, 70 ) ;
        pctile75 = (int)percentileHelper.evaluate( timesArray, 75 ) ;
        pctile80 = (int)percentileHelper.evaluate( timesArray, 80 ) ;
        pctile85 = (int)percentileHelper.evaluate( timesArray, 85 ) ;
        pctile90 = (int)percentileHelper.evaluate( timesArray, 90 ) ;
        pctile95 = (int)percentileHelper.evaluate( timesArray, 95 ) ;
        
        stddev   = (int)stddevHelper.evaluate( timesArray ) ;
        average  = (int)StatUtils.mean( timesArray ) ;
        
        paa.setStddev( stddev ) ;
        paa.setAvgTime( average ) ;
        paa.setEfficiency( efficiency ) ;
        
        paa.setFiftyPercentile      ( pctile50 ) ;
        paa.setFiftyFivePercentile  ( pctile55 ) ;
        paa.setSixtyPercentile      ( pctile60 ) ;
        paa.setSixtyFivePercentile  ( pctile65 ) ;
        paa.setSeventyPercentile    ( pctile70 ) ;
        paa.setSeventyFivePercentile( pctile75 ) ;
        paa.setEightyPercentile     ( pctile80 ) ;
        paa.setEightyFivePercentile ( pctile85 ) ;
        paa.setNinetyPercentile     ( pctile90 ) ;
        paa.setNinetyFivePercentile ( pctile95 ) ;
        
        paa.setNumProblemsAttempted( totalProblemsAttempted ) ;
        
        paaRepo.save( paa ) ;
    }
}
