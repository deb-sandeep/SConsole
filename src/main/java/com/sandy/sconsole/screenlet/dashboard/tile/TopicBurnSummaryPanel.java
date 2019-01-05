package com.sandy.sconsole.screenlet.dashboard.tile;

import java.awt.* ;

import javax.swing.BorderFactory ;
import javax.swing.JLabel ;
import javax.swing.JPanel ;

import org.apache.commons.lang.math.NumberUtils ;
import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;
import com.sandy.sconsole.screenlet.study.TopicBurnInfo ;

@SuppressWarnings( "serial" )
public class TopicBurnSummaryPanel extends JPanel {

    static final Logger log = Logger.getLogger( TopicBurnSummaryPanel.class ) ;
    
    private static final Font TITLE_FONT = UIConstant.BASE_FONT.deriveFont( 22F ) ;
    
    private class PctCompletionCanvas extends JPanel {
        
        private final Insets BORDER = new Insets( 2, 10, 2, 10 ) ;
        
        public PctCompletionCanvas() {
            setPreferredSize( new Dimension( 10, 10 ) ) ;
            setBackground( UIConstant.BG_COLOR ) ;
        }
        
        @Override
        public void paint( Graphics g ) {
            super.paint( g ) ;
            int width = getWidth() - BORDER.left - BORDER.right ;
            int height = getHeight() - BORDER.top - BORDER.bottom ;
            
            float pixelsPerQuestion = ((float)width)/burnInfo.getNumActiveProblemCount() ;

            int partition = (int)(pixelsPerQuestion * burnInfo.getNumSolvedProblemCount() ) ; 
            
            g.setColor( Color.GREEN.darker() .darker()) ;
            g.fillRect( BORDER.left, 
                        BORDER.top, 
                        partition, 
                        height ) ;
            
            g.setColor( Color.RED.darker().darker() ) ;
            g.fillRect( BORDER.left + partition + 1, 
                        BORDER.top, 
                        width - partition, 
                        height ) ;
        }
    }
    
    private class DailyBurnCanvas extends JPanel {

        private final Insets BORDER = new Insets( 2, 10, 2, 10 ) ;
        
        int maxValue = 0 ;
        int amberThreshold = 0 ;
        int greenThreshold = 0 ;
        int curVal = 0 ;
        
        int width = 0 ;
        int height = 0 ;
        float gridWidth = 0 ;
        
        public DailyBurnCanvas() {
            
            setBackground( UIConstant.BG_COLOR ) ;
            
            maxValue = NumberUtils.max( new int[]{
                 burnInfo.getRevisedMilestoneBurnRate(), 
                 burnInfo.getCurrentBurnRate(),
                 burnInfo.getNumProblemsSolvedToday()
            } ) + 2 ;
            
            if( burnInfo.getCurrentBurnRate() < 
                burnInfo.getRevisedMilestoneBurnRate() ) {
                amberThreshold = burnInfo.getCurrentBurnRate() ;
            }
            
            curVal = burnInfo.getNumProblemsSolvedToday() ;
            greenThreshold = burnInfo.getRevisedMilestoneBurnRate() ;
            
            // If completion milestone date has passed, revised milestone burn
            // rate has no meaning and will be zero. In this case, set the
            // amber threshold to 0 and green threshold to the max value. This
            // will ensure that the bar will always be in red.
            if( burnInfo.hasCompletionMilestoneDatePassed() && 
                burnInfo.getNumRemainingProblemCount() > 0 ) {
                
                amberThreshold = 0 ;
                greenThreshold = maxValue ;
            }
        }

        @Override
        public void paint( Graphics g ) {
            
            super.paint( g ) ;
            width = getWidth() - BORDER.left - BORDER.right ;
            height = getHeight() - BORDER.top - BORDER.bottom ;
            gridWidth = ((float)width)/maxValue ;
            
            paintCurrentValue( (Graphics2D)g ) ;
            paintGrid( (Graphics2D)g ) ;
        }
        
        private void paintGrid( Graphics2D g ) {
            
            g.setColor( Color.DARK_GRAY ) ;
            g.drawRect( BORDER.left, BORDER.top, width, height ) ;
            for( int i=1; i<=maxValue; i++ ) {
                int x = (int)(BORDER.left + gridWidth * i ) ;
                g.drawLine( x, BORDER.top, x, BORDER.top+height ) ;
            }
            
            int markerY = BORDER.top/2 ;
            int markerHeight = height + BORDER.top/2 + BORDER.bottom/2 ;
            int markerWidth = 3 ;
            
            if( amberThreshold > 0 ) {
                int x = (int)( BORDER.left + amberThreshold * gridWidth ) ;
                g.setColor( Color.ORANGE.brighter() ) ;
                g.drawRect( x, markerY, markerWidth, markerHeight );
            }
            
            int x = (int)( BORDER.left + greenThreshold * gridWidth ) ;
            g.setColor( Color.GREEN.brighter() ) ;
            g.drawRect( x, markerY, markerWidth, markerHeight );
        }
        
        private void paintCurrentValue( Graphics2D g ) {

            if( curVal >= greenThreshold ) {
                paintValue( 0, curVal, Color.GREEN.darker(), g ) ;
            }
            else if( amberThreshold > 0 && curVal >= amberThreshold ) {
                paintValue( 0, curVal, Color.ORANGE.darker(), g ) ;
            }
            else {
                paintValue( 0, curVal, Color.RED.darker(), g ) ;
            }
        }
        
        private void paintValue( int minVal, int maxVal, 
                                 Color color, Graphics2D g ) {
            
            int startX = (int)( BORDER.left + minVal*gridWidth ) ;
            int endX   = (int)( BORDER.left + maxVal*gridWidth ) ;
            
            g.setColor( color ) ;
            g.fillRect( startX, BORDER.top, (endX-startX), height ) ;
        }
    }
    
    private TopicBurnInfo burnInfo = null ;
    
    private String topicName = null ;
    private String subjectName = null ;
    
    public TopicBurnSummaryPanel( Topic topic ) 
        throws Exception {
        
        burnInfo = new TopicBurnInfo( topic ) ;
        topicName = burnInfo.getTopic().getTopicName() ;
        subjectName = burnInfo.getTopic().getSubject().getName() ;
        
        setUpUI() ;
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        setBackground( UIConstant.BG_COLOR ) ;
        setBorder( BorderFactory.createLineBorder( UIConstant.TILE_BORDER_COLOR.darker() ) ) ;
        
        add( getTitleLabel(), BorderLayout.NORTH ) ;
        add( getCenterPanel(), BorderLayout.CENTER ) ;
    }
    
    private JPanel getTitleLabel() {
        JLabel label = new JLabel() ;
        label.setFont( TITLE_FONT ) ;
        label.setForeground( StudyScreenlet.getSubjectColor( subjectName ).brighter() ) ;
        label.setText( topicName + getOvershootString() ) ;
        label.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 0 ) );
        
        JPanel panel = new JPanel() ;
        panel.setBackground( UIConstant.BG_COLOR ) ; 
        panel.setLayout( new BorderLayout() ) ;
        panel.add( label, BorderLayout.CENTER ) ;
        panel.add( getPctCompletionPanel(), BorderLayout.SOUTH ) ;
        
        return panel ;
    }
    
    private String getOvershootString() {
        
        int days = burnInfo.getNumOvershootDays() ;
        StringBuilder buffer = new StringBuilder( " (" ) ;
        
        if( days > 0 ) {
            buffer.append( "+" ) ;
        }
        buffer.append( days ) ;
        buffer.append( ")" ) ;
        return buffer.toString() ;
    }
    
    private JPanel getCenterPanel() {
        return new DailyBurnCanvas() ;
    }
    
    private JPanel getPctCompletionPanel() {
        return new PctCompletionCanvas() ;
    }
}
