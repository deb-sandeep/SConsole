package com.sandy.sconsole.screenlet.study;

import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.util.* ;
import java.util.concurrent.TimeUnit ;

import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;
import org.springframework.context.ApplicationContext ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.burncalibration.HistoricBurnStat ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;

public class TopicBurnInfo {

    static final Logger log = Logger.getLogger( TopicBurnInfo.class ) ;
    
    public static final SimpleDateFormat DF = new SimpleDateFormat( "yyyy-MM-dd" ) ;
    
    private static final Date DEFAULT_BURN_COMPLETION = new GregorianCalendar( 2019, 11, 1 ).getTime() ;
    private static final int DEFAULT_DAILY_BURN_RATE = 9 ;
    private static final int MOVING_AVG_NUM_DAYS = 7 ;
    
    private ProblemAttemptRepository paRepo = null ;
    private ProblemRepository pRepo = null ;
    
    private Topic topic = null ;
    private List<HistoricBurnStat> historicBurns = null ;
    
    private int numActiveProblemCount    = 0 ;
    private int numSolvedProblemCount    = 0 ;
    private int numRemainingProblemCount = 0 ; 
    
    private Date burnStartMilestoneDate         = null ;
    private int  numDaysSinceBurnStartMilestone = 0 ;
    
    private Date burnActualStartDate         = null ;
    private int  numDaysSinceBurnActualStart = 0 ;
    
    private Date burnCompletionMilestoneDate        = null ;
    private int  totalBurnMilestoneDuration         = 0 ;
    private int  numDaysTillBurnCompletionMilestone = 0 ;
    
    private Date burnProjectedCompletionDate        = null ;
    private int  numDaysTillProjectedBurnCompletion = 0 ; 
    private int  numOvershootDays                   = 0 ; 
    
    private int baseMilestoneBurnRate    = 0 ;
    private int revisedMilestoneBurnRate = 0 ; 
    private int currentBurnRate          = 0 ;
    
    private int numProblemsSolvedToday = 0 ;
    
    public TopicBurnInfo( Topic topic ) 
        throws Exception {
        
        this.topic = topic ;
        
        ApplicationContext ctx = SConsole.getAppContext() ;
        paRepo = ctx.getBean( ProblemAttemptRepository.class )  ;
        pRepo = ctx.getBean( ProblemRepository.class )  ;

        initialize() ;
    }
    
    private void initialize() 
        throws Exception {
        
        numActiveProblemCount    = pRepo.findActiveProblemCount( topic.getId() ) ;
        numSolvedProblemCount    = pRepo.findSolvedProblemCount( topic.getId() ) ;
        numRemainingProblemCount = numActiveProblemCount - numSolvedProblemCount ;
        historicBurns            = paRepo.getHistoricBurnStats( topic.getId() ) ;
        numProblemsSolvedToday   = paRepo.getNumProblemsSolvedToday( topic.getId() ) ;
        
        // Compute static information
        computeBurnStartDates() ;
        computeBurnMilestoneEndDate() ;
        computeBaseMilestoneBurnRate() ;
        
        // Compute yard sticks based on historic data
        computeCurrentBurnRate() ;
        
        // Compute projected values based on static and heuristic data
        computeRevisedMilestoneBurnRate() ;
        computeProjectedCompletion() ;
    }
    
    private void computeBurnStartDates() 
        throws ParseException {
        
        if( topic.getBurnStart() == null ) {
            // If there is no burn start milestone configured in the system,
            // we take the burn start as the day from which we started solving
            // problems. If no historic data is present, we assume that we are
            // starting the burn from today.
            if( historicBurns.isEmpty() ) {
                burnStartMilestoneDate = DateUtils.truncate( new Date(), Calendar.HOUR ) ;
            }
            else {
                burnStartMilestoneDate = DF.parse( historicBurns.get( 0 ).getDate() ) ;
            }
            burnActualStartDate = burnStartMilestoneDate ;
        }
        else {
            burnStartMilestoneDate = new Date( topic.getBurnStart().getTime() ) ;
            if( !historicBurns.isEmpty() ) {
                burnActualStartDate = DF.parse( historicBurns.get( 0 ).getDate() ) ;
            }
            else {
                burnActualStartDate = burnStartMilestoneDate ;
            }
        }
        
        Date now = new Date() ;
        numDaysSinceBurnStartMilestone = getDurationInDays( burnStartMilestoneDate, now ) ;
        numDaysSinceBurnActualStart = getDurationInDays( burnActualStartDate, now ) ;
    }
    
    private void computeBurnMilestoneEndDate() {
        
        if( topic.getBurnCompletion() == null ) {
            burnCompletionMilestoneDate = DEFAULT_BURN_COMPLETION ;
        }
        else {
            burnCompletionMilestoneDate = new Date( topic.getBurnCompletion().getTime() ) ;
        }
        
        // Why +1 - because on the last day (<24 hours), we practically still
        // have one day left.
        //
        // There is a possibility that the number of days till burn completion
        // is either zero or negative. This will happen when we have overshot
        // the burn completion date. This condition needs to be handled in
        // two places:
        // 1. In this class when computing the derivative attributes - negative
        //    values or divide by zero should not creep in
        // 2. In the usage of this class - we should have a flag which indicates
        //    if we have overshot so that the users can first read the flag
        //    and then appropriately interprete the values.
        numDaysTillBurnCompletionMilestone = (int)TimeUnit.DAYS.convert( 
                burnCompletionMilestoneDate.getTime() - new Date().getTime(), 
                TimeUnit.MILLISECONDS ) + 1 ;
        
        totalBurnMilestoneDuration = getDurationInDays( 
                                                burnStartMilestoneDate, 
                                                burnCompletionMilestoneDate ) ;
        
        numDaysTillBurnCompletionMilestone = getDurationInDays(
                                                new Date(),
                                                burnCompletionMilestoneDate ) ;
    }
    
    private void computeBaseMilestoneBurnRate() {
        
        baseMilestoneBurnRate = (int)Math.ceil(
                                        (float)numActiveProblemCount / 
                                        totalBurnMilestoneDuration ) ;
    }
    
    private void computeCurrentBurnRate() {
        
        if( historicBurns.isEmpty() ) {
            currentBurnRate = DEFAULT_DAILY_BURN_RATE ;
            return ;
        }
        
        int totalNumSumsForMovingAvg = 0 ;
        int numDaysForMovingAvg = historicBurns.size() ;
        if( numDaysForMovingAvg >= MOVING_AVG_NUM_DAYS ) {
            numDaysForMovingAvg = MOVING_AVG_NUM_DAYS ;
        }

        for( int i=0; i<numDaysForMovingAvg; i++ ) {
            HistoricBurnStat stat = historicBurns.get( historicBurns.size()-1-i ) ;
            totalNumSumsForMovingAvg += stat.getNumQuestionsSolved() ;
        }
        currentBurnRate = totalNumSumsForMovingAvg/numDaysForMovingAvg ;
    }
    
    private void computeRevisedMilestoneBurnRate() {
        
        if( isFullyBurnt() ) {
            revisedMilestoneBurnRate = 0 ;
        }
        else {
            if( hasCompletionMilestoneDatePassed() ) {
                revisedMilestoneBurnRate = 0 ;
            }
            else {
                revisedMilestoneBurnRate = (int)Math.ceil( 
                                           (float)numRemainingProblemCount / 
                                           numDaysTillBurnCompletionMilestone ) ;
            }
        }
    }
    
    private void computeProjectedCompletion() {
        
        if( isFullyBurnt() ) {
            burnProjectedCompletionDate = burnCompletionMilestoneDate ;
            numDaysTillBurnCompletionMilestone = 0 ;
            numOvershootDays = 0 ;
        }
        else {
            numDaysTillProjectedBurnCompletion = (int)Math.ceil( 
                    (float)numRemainingProblemCount / currentBurnRate 
            ) ;
            
            numOvershootDays = numDaysTillProjectedBurnCompletion -
                               numDaysTillBurnCompletionMilestone ;
            
            burnProjectedCompletionDate = DateUtils.addDays( new Date(), 
                                          numDaysTillProjectedBurnCompletion ) ;
        }
    }
    
    private int getDurationInDays( Date startDate, Date endDate ) {
        return (int)TimeUnit.DAYS.convert( 
                endDate.getTime() - startDate.getTime(),
                TimeUnit.MILLISECONDS ) + 1 ;
    }
    
    public List<HistoricBurnStat> getHistoricBurns() {
        return this.historicBurns ;
    }

    public Topic getTopic() {
        return topic ;
    }

    public int getNumActiveProblemCount() {
        return numActiveProblemCount ;
    }

    public int getNumSolvedProblemCount() {
        return numSolvedProblemCount ;
    }

    public int getNumRemainingProblemCount() {
        return numRemainingProblemCount ;
    }

    public Date getBurnStartMilestoneDate() {
        return burnStartMilestoneDate ;
    }

    public int getNumDaysSinceBurnStartMilestone() {
        return numDaysSinceBurnStartMilestone ;
    }

    public Date getBurnActualStartDate() {
        return burnActualStartDate ;
    }

    public int getNumDaysSinceBurnActualStart() {
        return numDaysSinceBurnActualStart ;
    }

    public Date getBurnCompletionMilestoneDate() {
        return burnCompletionMilestoneDate ;
    }

    public int getTotalBurnMilestoneDuration() {
        return totalBurnMilestoneDuration ;
    }

    public int getNumDaysTillBurnCompletionMilestone() {
        return numDaysTillBurnCompletionMilestone ;
    }

    public Date getBurnProjectedCompletionDate() {
        return burnProjectedCompletionDate ;
    }

    public int getNumDaysTillProjectedBurnCompletion() {
        return numDaysTillProjectedBurnCompletion ;
    }

    public int getNumOvershootDays() {
        return numOvershootDays ;
    }

    public int getBaseMilestoneBurnRate() {
        return baseMilestoneBurnRate ;
    }

    public int getRevisedMilestoneBurnRate() {
        return revisedMilestoneBurnRate ;
    }

    public int getCurrentBurnRate() {
        return currentBurnRate ;
    }

    public int getNumProblemsSolvedToday() {
        return numProblemsSolvedToday ;
    }

    public boolean hasCompletionMilestoneDatePassed() {
        return numDaysTillBurnCompletionMilestone <= 0 ;
    }
    
    public boolean isFullyBurnt() {
        return getNumRemainingProblemCount() == 0 ;
    }
    
    public String toString() {
        
        StringBuffer buffer = new StringBuffer( "\n" ) ;
        
        buffer.append( "Total problem count      = " ).append( numActiveProblemCount ).append( "\n" ) ;
        buffer.append( "Total problems solved    = " ).append( getNumSolvedProblemCount() ).append( "\n" ) ;
        buffer.append( "Total problem remaining  = " ).append( numRemainingProblemCount ).append( "\n" ) ;
        buffer.append( "Start date               = " ).append( DF.format( burnStartMilestoneDate ) ).append( "\n" ) ;
        buffer.append( "Completion date          = " ).append( DF.format( burnCompletionMilestoneDate ) ).append( "\n" ) ;
        buffer.append( "Total duration           = " ).append( getTotalBurnMilestoneDuration() ).append( "\n" ) ;
        buffer.append( "Num days till completion = " ).append( getNumDaysTillBurnCompletionMilestone() ).append( "\n" ) ;
        buffer.append( "Base burn for milestone  = " ).append( getBaseMilestoneBurnRate() ).append( "\n" ) ;
        buffer.append( "Current daily burn rate  = " ).append( currentBurnRate ).append( "\n" ) ;
        buffer.append( "Revised milestone burn   = " ).append( getRevisedMilestoneBurnRate() ).append( "\n" ) ;
        buffer.append( "Num problems solved today= " ).append( getNumProblemsSolvedToday() ).append( "\n" ) ;
        
        return buffer.toString() ;
    }
}
