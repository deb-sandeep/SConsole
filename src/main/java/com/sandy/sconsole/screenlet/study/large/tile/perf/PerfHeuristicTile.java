package com.sandy.sconsole.screenlet.study.large.tile.perf;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.GridLayout ;

import javax.swing.JPanel ;

import org.apache.log4j.Logger ;

import com.sandy.common.bus.Event ;
import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.dao.entity.ProblemAttempt ;
import com.sandy.sconsole.dao.entity.ProblemAttemptAnalysis ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.repository.ProblemAttemptAnalysisRepository ;

@SuppressWarnings( "serial" )
public class PerfHeuristicTile extends AbstractScreenletTile {
    
    static final Logger log = Logger.getLogger( PerfHeuristicTile.class ) ;

    private ProblemAttemptAnalysisRepository paaRepo = null ;
    private String screenletSubjectName = null ;
    
    private StatTimeLabel   avgTimeLabel    = null ;
    private StatTimeLabel   stddevTimeLabel = null ;
    private StatTimeLabel   pctile80Label   = null ;
    private PerfProgressBar progressBar     = null ;
    
    private Problem currentProblem = null ;
    
    private int runTime = 0 ;
    
    public PerfHeuristicTile( ScreenletPanel mother ) {
        super( mother ) ;
        
        paaRepo = SConsole.getAppContext()
                         .getBean( ProblemAttemptAnalysisRepository.class ) ;
        
        SConsole.GLOBAL_EVENT_BUS
                .addSubscriberForEventTypes( this, true, 
                                      EventCatalog.PROBLEM_ATTEMPT_STARTED,
                                      EventCatalog.PROBLEM_ATTEMPT_ENDED,
                                      EventCatalog.SESSION_PLAY_HEARTBEAT,
                                      EventCatalog.SESSION_ENDED ) ;
        
        this.screenletSubjectName = super.getScreenlet().getDisplayName() ;
        this.avgTimeLabel = new StatTimeLabel( Color.GREEN.darker(), "Av: " ) ;
        this.stddevTimeLabel = new StatTimeLabel( Color.GRAY, "Sd: " ) ;
        this.pctile80Label = new StatTimeLabel( Color.RED.darker(), "80: " ) ;
        this.progressBar = new PerfProgressBar() ;
        setUpUI() ;
        
    }
    
    private void setUpUI() {
        setUpWestPanel() ;
        add( progressBar, BorderLayout.CENTER ) ;
    }
    
    private void setUpWestPanel() {
        
        JPanel panel = new JPanel( new GridLayout( 3, 1 ) ) ;
        panel.setOpaque( false ) ;
        
        panel.add( this.avgTimeLabel ) ;
        panel.add( this.stddevTimeLabel ) ;
        panel.add( this.pctile80Label ) ;
        
        add( panel, BorderLayout.WEST ) ;
    }

    @Override
    public void handleEvent( Event event ) {
        super.handleEvent( event ) ;
        
        ProblemAttempt pa = null ;
        Session session = null ;
        switch( event.getEventType() ) {
            case EventCatalog.PROBLEM_ATTEMPT_STARTED:
                pa = ( ProblemAttempt )event.getValue() ;
                if( pa.getSubjectName().equals( this.screenletSubjectName ) ) {
                    handleProblemAttemptStart( pa.getProblem() ) ;
                }
                break ;
            case EventCatalog.PROBLEM_ATTEMPT_ENDED:
                pa = ( ProblemAttempt )event.getValue() ;
                if( pa.getSubjectName().equals( this.screenletSubjectName ) ) {
                    handleProblemAttemptEnd() ;
                }
                break ;
            case EventCatalog.SESSION_PLAY_HEARTBEAT:
                session = ( Session )event.getValue() ;
                if( session.getSubjectName().equals( this.screenletSubjectName ) && 
                    this.currentProblem != null ) {
                    handleHeartbeat() ;
                }
                break ;
            case EventCatalog.SESSION_ENDED:
                session = ( Session )event.getValue() ;
                if( session.getSubjectName().equals( this.screenletSubjectName ) && 
                    this.currentProblem != null ) {
                    handleSessionEnd() ;
                }
                break ;
        }
    }
    
    private void handleProblemAttemptStart( Problem problem ) {
        
        ProblemAttemptAnalysis paa = null ;
        int topicId = problem.getTopic().getId() ;
        String pType = problem.getProblemType() ;
        
        paa = paaRepo.findByTopicIdAndProblemType( topicId, pType ) ;
        if( paa != null ) {
            this.currentProblem = problem ;
            this.runTime = 0 ;
            
            this.avgTimeLabel.setDuration( paa.getAvgTime() ) ;
            this.stddevTimeLabel.setDuration( paa.getStddev() ) ;
            this.pctile80Label.setDuration( paa.getEightyPercentile() ) ;
            
            this.progressBar.setMarkers( paa.getAvgTime(), 
                                         paa.getEightyPercentile() ) ;
            this.progressBar.updateRunDuration( this.runTime ) ;
        }
    }
    
    private void handleHeartbeat() {
        if( this.currentProblem != null ) {
            this.runTime++ ;
            this.progressBar.updateRunDuration( this.runTime ) ;
        }
    }
    
    private void handleProblemAttemptEnd() {
        clearState() ;
    }
    
    private void handleSessionEnd() {
        clearState() ;
    }
    
    private void clearState() {
        this.runTime = 0 ;
        this.currentProblem = null ;
        this.avgTimeLabel.clear() ;
        this.stddevTimeLabel.clear() ;
        this.pctile80Label.clear() ;
        this.progressBar.setMarkers( 0, 0 ) ;
        this.progressBar.updateRunDuration( 0 ) ;
    }
}
