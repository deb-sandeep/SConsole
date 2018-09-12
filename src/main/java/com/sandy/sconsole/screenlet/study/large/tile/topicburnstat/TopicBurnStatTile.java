package com.sandy.sconsole.screenlet.study.large.tile.topicburnstat;

import static com.sandy.sconsole.core.frame.UIConstant.BG_COLOR ;
import static javax.swing.SwingConstants.CENTER ;

import java.awt.Color ;
import java.awt.Font ;
import java.text.SimpleDateFormat ;

import javax.swing.BorderFactory ;
import javax.swing.JLabel ;
import javax.swing.SwingConstants ;

import com.sandy.common.bus.Event ;
import com.sandy.common.util.StringUtil ;
import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.screenlet.* ;
import com.sandy.sconsole.screenlet.study.ExerciseBurnInfo ;

import info.clearthought.layout.TableLayout ;

/**
 * This tile shows the following information:
 * 
 * * Total number of active questions in this topic
 * * Burn start date
 * * Num solved problems
 * * Num remaining problems
 * * Burn completion date
 * * Baseline burn 
 * * Current burn
 * * Burnrate required
 * * Overshoot at current burn rate
 */
@SuppressWarnings( "serial" )
public class TopicBurnStatTile extends AbstractScreenletTile {
    
    private static final SimpleDateFormat DF = new SimpleDateFormat( "dd-MMM-yyyy" ) ;
    
    private static final Font LBL_FONT    = UIConstant.BASE_FONT.deriveFont( 16F ) ;
    private static final Font VAL_FONT    = UIConstant.BASE_FONT.deriveFont( 30F ) ;
    private static final Font SM_VAL_FONT = UIConstant.BASE_FONT.deriveFont( 23F ) ;
    
    private JLabel burnEndDtLbl        = createDefaultLabel( "Burn end date" ) ;
    private JLabel burnEndDt           = createDefaultLabel( "" ) ;
    
    private JLabel burnStartDtLbl      = createDefaultLabel( "Burn start date" ) ;
    private JLabel burnStartDt         = createDefaultLabel( "" ) ;
    
    private JLabel numQLbl             = createDefaultLabel( "Num Q" ) ;
    private JLabel numQ                = createDefaultLabel( "" ) ;
    
    private JLabel numSolvedQLbl       = createDefaultLabel( "Num Q solved" ) ;
    private JLabel numSolvedQ          = createDefaultLabel( "" ) ;
    
    private JLabel numRemainingQLbl    = createDefaultLabel( "Num Q remaining" ) ;
    private JLabel numRemainingQ       = createDefaultLabel( "" ) ;
    
    private JLabel baselineBurnRateLbl = createDefaultLabel( "Baseline burn rate" ) ;
    private JLabel baselineBurnRate    = createDefaultLabel( "" ) ;
    
    private JLabel currentBurnRateLbl  = createDefaultLabel( "Current burn rate" ) ;
    private JLabel currentBurnRate     = createDefaultLabel( "" ) ;
    
    private JLabel requiredBurnRateLbl = createDefaultLabel( "Required burn rate" ) ;
    private JLabel requiredBurnRate    = createDefaultLabel( "" ) ;
    
    private JLabel projectedEndDtLbl   = createDefaultLabel( "Overshoot days" ) ;
    private JLabel projectedEndDt      = createDefaultLabel( "" ) ;
    
    private JLabel[] labels = {
            burnStartDtLbl,
            burnStartDt,
            burnEndDtLbl,
            burnEndDt,
            numQLbl,
            numQ,
            numSolvedQLbl,
            numSolvedQ,
            numRemainingQLbl,
            numRemainingQ,
            baselineBurnRateLbl,
            baselineBurnRate,
            currentBurnRateLbl,
            currentBurnRate,
            requiredBurnRateLbl,
            requiredBurnRate,
            projectedEndDtLbl,
            projectedEndDt
    } ;
    
    public TopicBurnStatTile( ScreenletPanel mother ) {
        super( mother ) ;
    
        setUpUI() ;
        
        mother.getScreenlet()
              .getEventBus()
              .addSubscriberForEventTypes( this, true, 
                                           EventCatalog.BURN_INFO_REFRESHED ) ;
    }
    
    private JLabel createDefaultLabel( String defaultText ) {
        
        JLabel label = new JLabel() ;
        label.setVerticalAlignment( CENTER ) ;
        label.setOpaque( true ) ;
        label.setBackground( BG_COLOR ) ;
        
        if( StringUtil.isNotEmptyOrNull( defaultText ) ) {
            label.setText( defaultText ) ;
            label.setForeground( Color.GRAY.darker() ) ;
            label.setFont( LBL_FONT ) ;
            label.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 0 ) );
            label.setHorizontalAlignment( SwingConstants.LEFT ) ;
        }
        else {
            label.setText( "" ) ;
            label.setForeground( Color.GRAY.brighter() ) ;
            label.setFont( VAL_FONT ) ;
            label.setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 10 ) );
            label.setHorizontalAlignment( SwingConstants.RIGHT ) ;
        }
        return label ;
    }
    
    private void setUpUI() {
        
        setLayout() ;
        
        for( int row=0; row<labels.length/2; row++ ) {
            JLabel titleLbl = labels[2*row] ;
            JLabel valLbl   = labels[2*row+1] ;
            
            add( titleLbl, "0," + row + ",0," + row ) ;
            add( valLbl,   "1," + row + ",1," + row ) ;
        }
        
        burnStartDt.setFont( SM_VAL_FONT ) ;
        burnEndDt.setFont( SM_VAL_FONT ) ;
    }
    
    private void setLayout() {
        
        int numRows = labels.length/2 ;
        float rowHeightPct = 1.0F/numRows ;
        
        TableLayout layout = new TableLayout() ;
        for( int i=0; i<numRows; i++ ) {
            layout.insertRow( 0, rowHeightPct ) ;
        }
        layout.insertColumn( 0, 0.5F ) ;
        layout.insertColumn( 0, 0.5F ) ;
        setLayout( layout ) ;
    }
    
    @Override
    public void handleEvent( Event event ) {
        super.handleEvent( event ) ;
        
        switch( event.getEventType() ) {
            case EventCatalog.BURN_INFO_REFRESHED:
                ExerciseBurnInfo bi = (ExerciseBurnInfo)event.getValue() ;
                refreshBurnInfo( bi ) ;
                break ;
        }
    }
    
    private void refreshBurnInfo( ExerciseBurnInfo bi ) {
        
        numQ.setText             ( String.valueOf( bi.getNumProblems() ) ) ;
        burnStartDt.setText      ( DF.format( bi.getBurnStartDate() ) ) ;
        numSolvedQ.setText       ( String.valueOf( bi.getNumProblemsSolved() ) ) ;
        numRemainingQ.setText    ( String.valueOf( bi.getNumProblemsRemaining() ) ) ;
        burnEndDt.setText        ( DF.format( bi.getBurnCompletionDate() ) ) ;
        baselineBurnRate.setText ( String.valueOf( bi.getBaseMilestoneBurnRate() ) ) ;
        currentBurnRate.setText  ( String.valueOf( bi.getCurrentBurnRate() ) ) ;
        requiredBurnRate.setText ( String.valueOf( bi.getRevisedMilestoneBurnRate() ) ) ;
        projectedEndDt.setText   ( String.valueOf( bi.getOvershootDays() ) ) ; 
        
        highlightValues( bi ) ; 
    }
    
    private void highlightValues( ExerciseBurnInfo bi ) {
        
        if( bi.getCurrentBurnRate() < bi.getBaseMilestoneBurnRate() ) {
            currentBurnRate.setForeground( Color.RED ) ;
        }
        else {
            currentBurnRate.setForeground( Color.GREEN.darker() ) ;
        }
        
        if( bi.getRevisedMilestoneBurnRate() <= bi.getBaseMilestoneBurnRate() ) {
            requiredBurnRate.setForeground( Color.GREEN.darker() ) ;
        }
        else {
            requiredBurnRate.setForeground( Color.RED ) ;
        }
        
        if( bi.getOvershootDays() <= 0 ) {
            projectedEndDt.setForeground( Color.GREEN.darker() ) ;
        }
        else {
            projectedEndDt.setForeground( Color.RED ) ;
        }
    }
}
