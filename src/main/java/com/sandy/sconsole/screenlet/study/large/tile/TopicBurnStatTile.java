package com.sandy.sconsole.screenlet.study.large.tile;

import static com.sandy.sconsole.core.frame.UIConstant.BG_COLOR ;
import static javax.swing.SwingConstants.CENTER ;

import java.awt.Color ;

import javax.swing.JLabel ;
import javax.swing.SwingConstants ;

import com.sandy.common.bus.Event ;
import com.sandy.common.util.StringUtil ;
import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.core.screenlet.* ;
import com.sandy.sconsole.screenlet.study.ExerciseBurnInfo ;

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
    
    private JLabel numQLbl             = createDefaultLabel( "Num problems" ) ;
    private JLabel numQ                = createDefaultLabel( "" ) ;
    
    private JLabel burnStartDtLbl      = createDefaultLabel( "Burn start date" ) ;
    private JLabel burnStartDt         = createDefaultLabel( "" ) ;
    
    private JLabel numSolvedQLbl       = createDefaultLabel( "Num solved problems" ) ;
    private JLabel numSolvedQ          = createDefaultLabel( "" ) ;
    
    private JLabel numRemainingQLbl    = createDefaultLabel( "Num remaining problems" ) ;
    private JLabel numRemainingQ       = createDefaultLabel( "" ) ;
    
    private JLabel burnEndDtLbl        = createDefaultLabel( "Burn end date" ) ;
    private JLabel burnEndDt           = createDefaultLabel( "" ) ;
    
    private JLabel baselineBurnRateLbl = createDefaultLabel( "Baseline burn rate" ) ;
    private JLabel baselineBurnRate    = createDefaultLabel( "" ) ;
    
    private JLabel currentBurnRateLbl  = createDefaultLabel( "Current burn rate" ) ;
    private JLabel currentBurnRate     = createDefaultLabel( "" ) ;
    
    private JLabel requiredBurnRateLbl = createDefaultLabel( "Required burn rate" ) ;
    private JLabel requiredBurnRate    = createDefaultLabel( "" ) ;
    
    private JLabel projectedEndDtLbl   = createDefaultLabel( "Projected end date" ) ;
    private JLabel projectedEndDt      = createDefaultLabel( "" ) ;
    
    public TopicBurnStatTile( ScreenletPanel mother ) {
        super( mother ) ;
        
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
            label.setHorizontalAlignment( SwingConstants.LEFT ) ;
            label.setForeground( Color.DARK_GRAY ) ;
        }
        else {
            label.setText( "" ) ;
            label.setHorizontalAlignment( SwingConstants.RIGHT ) ;
            label.setForeground( Color.GREEN ) ;
        }
        return label ;
    }
    
    @Override
    public void handleEvent( Event event ) {
        super.handleEvent( event ) ;
        
        switch( event.getEventType() ) {
            case EventCatalog.BURN_INFO_REFRESHED:
                ExerciseBurnInfo bi = (ExerciseBurnInfo)event.getValue() ;
                break ;
        }
    }
}
