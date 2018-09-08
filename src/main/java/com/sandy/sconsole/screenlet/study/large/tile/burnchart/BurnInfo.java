package com.sandy.sconsole.screenlet.study.large.tile.burnchart;

import java.text.SimpleDateFormat ;
import java.util.* ;
import java.util.concurrent.TimeUnit ;

import org.apache.commons.lang.time.DateUtils ;
import org.springframework.context.ApplicationContext ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.burn.HistoricBurnStat ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;

public class BurnInfo {

    public static final SimpleDateFormat DF = new SimpleDateFormat( "yyyy-MM-dd" ) ;
    
    private static final Date DEFAULT_BURN_COMPLETION = new GregorianCalendar( 2019, 11, 1 ).getTime() ;
    private static final int DEFAULT_DAILY_BURN_RATE = 9 ;
    private static final int MOVING_AVG_NUM_DAYS = 7 ;
    
    private ProblemAttemptRepository paRepo = null ;
    private ProblemRepository pRepo = null ;
    
    private Topic topic = null ;
    private List<HistoricBurnStat> historicBurns = null ;
    
    private int  numProblemCount           = 0 ;
    private int  numSolvedProblemCount     = 0 ;
    private int  numRemainingProblemCount  = 0 ;
    private Date burnCompletionDate        = null ;
    private Date burnStartDate             = null ;
    private int  numDaysTillBurnCompletion = 0 ;
    private int  totalDuration             = 0 ;
    private int  currentBurnRate           = 0 ;
    private int  baseMilestoneBurnRate     = 0 ;
    private int  revisedMilestoneBurnRate  = 0 ;
    
    public BurnInfo( Topic topic ) 
        throws Exception {
        
        this.topic = topic ;
        
        ApplicationContext ctx = SConsole.getAppContext() ;
        paRepo = ctx.getBean( ProblemAttemptRepository.class )  ;
        pRepo = ctx.getBean( ProblemRepository.class )  ;

        initialize() ;
    }
    
    private void initialize() 
        throws Exception {
        
        numProblemCount          = pRepo.findActiveProblemCount( topic.getId() ) ;
        numSolvedProblemCount    = pRepo.findSolvedProblemCount( topic.getId() ) ;
        numRemainingProblemCount = numProblemCount - numSolvedProblemCount ;
        
        initHistoricBurns() ;
        initBurnCompletionMilestone() ;
        determineBurnRates() ;
    }
    
    private void initHistoricBurns() {
        
        historicBurns = paRepo.getHistoricBurnStats( topic.getId() ) ;
    }
    
    private void initBurnCompletionMilestone() {
        
        if( topic.getBurnCompletion() == null ) {
            burnCompletionDate = DEFAULT_BURN_COMPLETION ;
        }
        else {
            burnCompletionDate = new Date( topic.getBurnCompletion().getTime() ) ;
        }
        
        // Determine the days from now till burn completion milestone
        numDaysTillBurnCompletion = (int)TimeUnit.DAYS.convert( 
                               burnCompletionDate.getTime() - new Date().getTime(), 
                               TimeUnit.MILLISECONDS ) ;
    }
    
    private void determineBurnRates() 
        throws Exception {
        
        if( historicBurns.isEmpty() ) {
            // There is no prior burn history, let's assign the default. 
            // This assumes that the start day is today.
            burnStartDate = DateUtils.truncate( new Date(), Calendar.HOUR ) ;
            currentBurnRate = DEFAULT_DAILY_BURN_RATE ;
            baseMilestoneBurnRate = (int)Math.ceil((float)numRemainingProblemCount / 
                                          numDaysTillBurnCompletion) ;
            revisedMilestoneBurnRate = baseMilestoneBurnRate ;
            totalDuration = numDaysTillBurnCompletion ;
        }
        else {
            int numSums = 0 ;
            int numDaysForMovingAvg = historicBurns.size() ;
            if( numDaysForMovingAvg >= MOVING_AVG_NUM_DAYS ) {
                numDaysForMovingAvg = MOVING_AVG_NUM_DAYS ;
            }

            for( int i=0; i<numDaysForMovingAvg; i++ ) {
                HistoricBurnStat stat = historicBurns.get( historicBurns.size()-1-i ) ;
                numSums += stat.getNumQuestionsSolved() ;
            }
            currentBurnRate = numSums/numDaysForMovingAvg ;
            
            burnStartDate = DF.parse( historicBurns.get( 0 ).getDate() ) ;
            totalDuration = (int)TimeUnit.DAYS.convert( 
                                    burnCompletionDate.getTime() - burnStartDate.getTime(), 
                                    TimeUnit.MILLISECONDS ) ;
            baseMilestoneBurnRate = (int)Math.ceil((float)numProblemCount/totalDuration) ;
            revisedMilestoneBurnRate = (int)Math.ceil((float)numRemainingProblemCount / 
                                             numDaysTillBurnCompletion) ;
        }
    }
    
    public List<HistoricBurnStat> getHistoricBurns() {
        return this.historicBurns ;
    }

    public int getNumProblems() {
        return numProblemCount ;
    }

    public int getNumProblemsRemaining() {
        return numRemainingProblemCount ;
    }
    
    public int getNumProblemsSolved() {
        return numSolvedProblemCount ;
    }

    public Date getBurnCompletionDate() {
        return burnCompletionDate ;
    }

    public Date getBurnStartDate() {
        return burnStartDate ;
    }

    public int getNumDaysTillBurnCompletion() {
        return numDaysTillBurnCompletion ;
    }
    
    public int getTotalDuration() {
        return totalDuration ;
    }

    public int getCurrentBurnRate() {
        return currentBurnRate ;
    }
    
    public int getBaseMilestoneBurnRate() {
        return baseMilestoneBurnRate ;
    }

    public int getRevisedMilestoneBurnRate() {
        return revisedMilestoneBurnRate ;
    }
    
    public Topic getTopic() {
        return this.topic ;
    }
    
    public String toString() {
        
        StringBuffer buffer = new StringBuffer( "\n" ) ;
        
        buffer.append( "Total problem count      = " ).append( numProblemCount ).append( "\n" ) ;
        buffer.append( "Total problems solved    = " ).append( getNumProblemsSolved() ).append( "\n" ) ;
        buffer.append( "Total problem remaining  = " ).append( numRemainingProblemCount ).append( "\n" ) ;
        buffer.append( "Start date               = " ).append( DF.format( burnStartDate ) ).append( "\n" ) ;
        buffer.append( "Completion date          = " ).append( DF.format( burnCompletionDate ) ).append( "\n" ) ;
        buffer.append( "Total duration           = " ).append( totalDuration ).append( "\n" ) ;
        buffer.append( "Num days till completion = " ).append( numDaysTillBurnCompletion ).append( "\n" ) ;
        buffer.append( "Base burn for milestone  = " ).append( getBaseMilestoneBurnRate() ).append( "\n" ) ;
        buffer.append( "Current daily burn rate  = " ).append( currentBurnRate ).append( "\n" ) ;
        buffer.append( "Revised milestone burn   = " ).append( getRevisedMilestoneBurnRate() ).append( "\n" ) ;
        
        return buffer.toString() ;
    }
}
