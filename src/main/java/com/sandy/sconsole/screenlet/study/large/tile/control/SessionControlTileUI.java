package com.sandy.sconsole.screenlet.study.large.tile.control;

import static com.sandy.sconsole.core.frame.UIConstant.* ;
import static javax.swing.SwingConstants.CENTER ;

import java.awt.BorderLayout ;
import java.awt.Color ;

import javax.swing.* ;
import javax.swing.border.Border ;

import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.remote.DemuxKeyProcessor ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.dao.entity.Session.SessionType ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;

import info.clearthought.layout.TableLayout ;

@SuppressWarnings( "serial" )
public abstract class SessionControlTileUI extends AbstractScreenletTile {

    static final Logger log = Logger.getLogger( SessionControlTileUI.class ) ;
    
    private class LabelMeta {
        String constraint = null ;
        JLabel label = null ;
        float fontSize = -1F ;
        Color fgColor = null ;
        boolean border = true ;
        
        LabelMeta( JLabel l, String con, float fSz, Color color, boolean bdr ) {
            constraint = con ;
            label = l ;
            fontSize = fSz ;
            fgColor = color ;
            border = bdr ;
        }
    }
    
    private static final int NUM_ROW = 8 ;
    private static final int NUM_COL = 12 ;
    
    public static Color TYPE_LBL_FG       = Color.GRAY ;
    public static Color TOPIC_LBL_FG      = Color.LIGHT_GRAY ;
    public static Color BOOK_LBL_FG       = Color.GRAY.brighter() ;
    public static Color SUMS_LEFT_LBL_FG  = Color.GRAY ;
    public static Color PROBLEM_LBL_FG    = Color.decode( "#C2E880" ) ;
    public static Color STIME_LBL_FG      = Color.decode( "#75BAF9" ) ;
    public static Color LTIME_LBL_FG      = Color.decode( "#F98BCC" ) ;
    public static Color BTN1_LBL_FG       = Color.GRAY ;
    public static Color BTN2_LBL_FG       = Color.WHITE ;
    
    public enum Btn1Type { PLAY, PAUSE, CLEAR } ;
    public enum Btn2Type { STOP, CHANGE, CLEAR, CANCEL } ;
    public enum OutcomeButtonsState { HIDDEN, INACTIVE, ACTIVE } ;
    
    public static Border INVALID_BORDER = BorderFactory.createCompoundBorder( 
                            BorderFactory.createEmptyBorder( 2, 2, 2, 2 ), 
                            BorderFactory.createLineBorder( Color.RED, 1 ) ) ;
    
    public JLabel typeLbl      = createDefaultLabel( "" ) ;
    public JLabel topicLbl     = createDefaultLabel( "" ) ;
    public JLabel bookLbl      = createDefaultLabel( "" ) ;
    public JLabel sumsLeftLbl  = createDefaultLabel( "" ) ;
    public JLabel problemLbl   = createDefaultLabel( "" ) ;
    public JLabel sTimeLbl     = createDefaultLabel( "" ) ;
    public JLabel lTimeLbl     = createDefaultLabel( "" ) ;
    public JLabel btn1Lbl      = createDefaultLabel( "" ) ;
    public JLabel btn2Lbl      = createDefaultLabel( "" ) ;
    
    public JLabel numSkipLbl   = createDefaultLabel( "" ) ;
    public JLabel numSolvedLbl = createDefaultLabel( "" ) ;
    public JLabel numRedoLbl   = createDefaultLabel( "" ) ;
    public JLabel numPigeonLbl = createDefaultLabel( "" ) ;
    public JLabel numIgnoreLbl = createDefaultLabel( "" ) ;
    
    public JLabel btnSolvedLbl = createDefaultLabel( "Solved" ) ;
    public JLabel btnRedoLbl   = createDefaultLabel( "Redo" ) ;
    public JLabel btnPigeonLbl = createDefaultLabel( "Pigeon" ) ;
    public JLabel btnSkipLbl   = createDefaultLabel( "Skip" ) ;
    public JLabel btnIgnoreLbl = createDefaultLabel( "Ignore" ) ;
    
    public Icon exerciseIcon = null ;
    public Icon theoryIcon   = null ;
    public Icon lectureIcon  = null ;
    public Icon playIcon     = null ;
    public Icon pauseIcon    = null ;
    public Icon stopIcon     = null ;
    public Icon cancelIcon   = null ;
    
    public DemuxKeyProcessor keyProcessor = null ;
    
    private LabelMeta[] labelMetaArray = {
        new LabelMeta( typeLbl,      "0,0,1,2",  25F, TYPE_LBL_FG,       true  ), 
        new LabelMeta( topicLbl,     "2,0,11,1", 50F, TOPIC_LBL_FG,      false ), 
        new LabelMeta( bookLbl,      "2,2,9,2",  40F, BOOK_LBL_FG,       false ), 
        new LabelMeta( sumsLeftLbl,  "10,2,11,2",35F, SUMS_LEFT_LBL_FG,  false ), 
        new LabelMeta( problemLbl,   "0,3,7,4",  40F, PROBLEM_LBL_FG,    true  ), 
        new LabelMeta( sTimeLbl,     "8,3,11,4", 60F, STIME_LBL_FG,      true  ), 
        new LabelMeta( lTimeLbl,     "8,5,11,6", 60F, LTIME_LBL_FG,      true  ), 
        new LabelMeta( btn1Lbl,      "8,7,9,7",  25F, BTN1_LBL_FG,       true  ), 
        new LabelMeta( btn2Lbl,      "10,7,11,7",25F, BTN2_LBL_FG,       true  )
    } ;
    
    public SessionControlTileUI( ScreenletPanel parent ) {
        super( parent ) ;
        loadIcons() ;
        setUpUI() ;
    }
    
    // ------------------------- Base UI creation [Start] ----------------------
    
    private void loadIcons() {
        exerciseIcon = new ImageIcon( getClass().getResource( "/icons/exercise.png" ) ) ;
        theoryIcon   = new ImageIcon( getClass().getResource( "/icons/theory.png"   ) ) ;
        lectureIcon  = new ImageIcon( getClass().getResource( "/icons/lecture.png"  ) ) ;
        playIcon     = new ImageIcon( getClass().getResource( "/icons/play.png"     ) ) ;
        pauseIcon    = new ImageIcon( getClass().getResource( "/icons/pause.png"    ) ) ;
        stopIcon     = new ImageIcon( getClass().getResource( "/icons/stop.png"     ) ) ;
        cancelIcon   = new ImageIcon( getClass().getResource( "/icons/cancel.png"     ) ) ;
    }
    
    private void setUpUI() {
        setLayout( createLayout() ) ;
        for( LabelMeta meta : labelMetaArray ) {
            addPanel( meta ) ;
        }
        add( createProblemOutcomePanel(), "0,5,7,7" ) ;
        validate() ;
        repaint() ;
    }
    
    private TableLayout createLayout() {
        double[] colSizes = new double[NUM_COL] ;
        double[] rowSizes = new double[NUM_ROW] ;

        for( int i = 0 ; i < NUM_COL ; i++ ) {
            colSizes[i] = 1D/NUM_COL ;
        }
        for( int i = 0 ; i < NUM_ROW ; i++ ) {
            rowSizes[i] = 1D/NUM_ROW ;
        }
        return new TableLayout( colSizes, rowSizes ) ;
    }
    
    private void addPanel( LabelMeta meta ) {
        
        JLabel label = meta.label ;
        JPanel panel = createDefaultPanel() ;
        
        label.setForeground( meta.fgColor ) ;
        if( meta.fontSize > 0 ) {
            label.setFont( BASE_FONT.deriveFont( meta.fontSize ) ) ;
        }
        
        panel.add( meta.label ) ;
        if( meta.border ) {
            panel.setBorder( TILE_BORDER ) ;
        }
        
        add( panel, meta.constraint ) ;
    }
    
    
    private JPanel createDefaultPanel() {
        
        JPanel panel = new JPanel() ;
        panel.setLayout( new BorderLayout() ) ;
        panel.setBackground( UIConstant.BG_COLOR ) ;
        return panel ;
    }
    
    private JPanel createProblemOutcomePanel() {
        
        TableLayout layout = new TableLayout() ;
        for( int r=0; r<3; r++ ) {
            layout.insertRow( r, 0.33F ) ;
        }
        for( int c=0; c<5; c++ ) {
            layout.insertColumn( c, 0.20F ) ;
        }
        
        numSolvedLbl.setFont ( BASE_FONT.deriveFont( 60F ) ) ;
        numRedoLbl.setFont   ( BASE_FONT.deriveFont( 60F ) ) ;
        numPigeonLbl.setFont ( BASE_FONT.deriveFont( 60F ) ) ;
        numSkipLbl.setFont   ( BASE_FONT.deriveFont( 60F ) ) ;
        numIgnoreLbl.setFont ( BASE_FONT.deriveFont( 60F ) ) ;

        numSolvedLbl.setForeground ( UIConstant.FN_A_COLOR ) ;
        numRedoLbl.setForeground   ( UIConstant.FN_B_COLOR ) ;
        numPigeonLbl.setForeground ( UIConstant.FN_C_COLOR ) ;
        numSkipLbl.setForeground   ( UIConstant.FN_D_COLOR ) ;
        numIgnoreLbl.setForeground ( UIConstant.FN_F_COLOR ) ;
        
        btnSolvedLbl.setFont ( BASE_FONT.deriveFont( 30F ) ) ;
        btnRedoLbl.setFont   ( BASE_FONT.deriveFont( 30F ) ) ;
        btnPigeonLbl.setFont ( BASE_FONT.deriveFont( 30F ) ) ;
        btnSkipLbl.setFont   ( BASE_FONT.deriveFont( 30F ) ) ;
        btnIgnoreLbl.setFont ( BASE_FONT.deriveFont( 30F ) ) ;

        numSolvedLbl.setBorder ( TILE_BORDER ) ;
        numRedoLbl.setBorder   ( TILE_BORDER ) ;
        numPigeonLbl.setBorder ( TILE_BORDER ) ;
        numSkipLbl.setBorder   ( TILE_BORDER ) ;
        numIgnoreLbl.setBorder ( TILE_BORDER ) ;
        
        btnSolvedLbl.setBorder ( TILE_BORDER ) ;
        btnRedoLbl.setBorder   ( TILE_BORDER ) ;
        btnPigeonLbl.setBorder ( TILE_BORDER ) ;
        btnSkipLbl.setBorder   ( TILE_BORDER ) ;
        btnIgnoreLbl.setBorder ( TILE_BORDER ) ;
        
        JPanel panel = new JPanel( layout ) ;
        panel.setBackground( BG_COLOR ) ;
        
        panel.add( numSolvedLbl, "0,0,0,1" ) ;
        panel.add( numRedoLbl,   "1,0,1,1" ) ;
        panel.add( numPigeonLbl, "2,0,2,1" ) ;
        panel.add( numSkipLbl,   "3,0,3,1" ) ;
        panel.add( numIgnoreLbl, "4,0,4,1" ) ;
        
        panel.add( btnSolvedLbl, "0,2,0,2" ) ;
        panel.add( btnRedoLbl,   "1,2,1,2" ) ;
        panel.add( btnPigeonLbl, "2,2,2,2" ) ;
        panel.add( btnSkipLbl,   "3,2,3,2" ) ;
        panel.add( btnIgnoreLbl, "4,2,4,2" ) ;
        return panel ;
    }

    private JLabel createDefaultLabel( String defaultText ) {
        
        JLabel label = new JLabel() ;
        label.setForeground( Color.DARK_GRAY ) ;
        label.setHorizontalAlignment( CENTER ) ;
        label.setVerticalAlignment( CENTER ) ;
        label.setVisible( true ) ;
        label.setOpaque( true ) ;
        label.setBackground( BG_COLOR ) ;
        
        if( StringUtil.isNotEmptyOrNull( defaultText ) ) {
            label.setText( defaultText ) ;
        }
        return label ;
    }
    // ------------------------- Base UI creation [End] ----------------------
    
    
    // ----------------- Panel content manipulation [Starts] -------------------
    
    public void cleanControlPanel() {
        
        setSessionTypeIcon( null ) ;
        setTopicLabel( null ) ;
        setBookLabel( null ) ;
        setProblemLabel( null ) ;
        
        clearInvalidationBorders() ;
        clearChangeUIHighlights() ;
        
        updateNumProblemsLeftInBookLabel( -1 ) ;
        updateSessionTimeLabel( -1 ) ;
        updateLapTimeLabel( -1 ) ;
        updateOutcomeCounts( null ) ;
        
        setBtn1UI( Btn1Type.CLEAR ) ;
        setBtn2UI( Btn2Type.CLEAR ) ;
    }
    
    public void clearInvalidationBorders() {
        typeLbl.setBorder( null ) ;
        topicLbl.setBorder( null ) ;
        bookLbl.setBorder( null ) ;
        problemLbl.setBorder( null ) ;
    }
    
    public void clearChangeUIHighlights() {
        typeLbl.setBackground( UIConstant.BG_COLOR ) ;
        topicLbl.setBackground( UIConstant.BG_COLOR ) ;
        bookLbl.setBackground( UIConstant.BG_COLOR ) ;
        problemLbl.setBackground( UIConstant.BG_COLOR ) ;
    }
    
    public void invalidateSessionTypePanel() {
        typeLbl.setBorder( INVALID_BORDER ) ;
    }
    
    public void invalidateTopicPanel() {
        topicLbl.setBorder( INVALID_BORDER ) ;
    }
    
    public void invalidateBookPanel() {
        bookLbl.setBorder( INVALID_BORDER ) ;
    }
    
    public void invalidateProblemPanel() {
        problemLbl.setBorder( INVALID_BORDER ) ;
    }
    
    public void setOutcomeButtonsState( OutcomeButtonsState state ) {
        
        boolean visibility = (state == OutcomeButtonsState.HIDDEN) ? false : true ;
        
        btnSkipLbl.setVisible( visibility ) ;
        btnSolvedLbl.setVisible( visibility ) ;
        btnRedoLbl.setVisible( visibility ) ;
        btnPigeonLbl.setVisible( visibility ) ;
        btnIgnoreLbl.setVisible( visibility ) ;
        
        if( state == OutcomeButtonsState.INACTIVE ) {
            
            btnSkipLbl.setBackground( BG_COLOR ) ; 
            btnSolvedLbl.setBackground( BG_COLOR ) ;
            btnRedoLbl.setBackground( BG_COLOR ) ;
            btnPigeonLbl.setBackground( BG_COLOR ) ;
            btnIgnoreLbl.setBackground( BG_COLOR ) ;

            btnSkipLbl.setForeground( Color.DARK_GRAY ) ; 
            btnSolvedLbl.setForeground( Color.DARK_GRAY ) ;
            btnRedoLbl.setForeground( Color.DARK_GRAY ) ;
            btnPigeonLbl.setForeground( Color.DARK_GRAY ) ;
            btnIgnoreLbl.setForeground( Color.DARK_GRAY ) ;
        }
        else if( state == OutcomeButtonsState.ACTIVE ) {
            
            btnSolvedLbl.setBackground( FN_A_COLOR ) ;
            btnRedoLbl.setBackground( FN_B_COLOR ) ;
            btnPigeonLbl.setBackground( FN_C_COLOR ) ;
            btnSkipLbl.setBackground( FN_D_COLOR ) ; 
            btnIgnoreLbl.setBackground( FN_F_COLOR ) ;

            btnSkipLbl.setForeground( Color.WHITE ) ; 
            btnSolvedLbl.setForeground( Color.WHITE ) ;
            btnRedoLbl.setForeground( Color.WHITE ) ;
            btnPigeonLbl.setForeground( Color.WHITE ) ;
            btnIgnoreLbl.setForeground( Color.WHITE ) ;
        }
    }
    
    public void setSessionTypeIcon( SessionType type ) {
        
        if( type == null ) {
            typeLbl.setIcon( null ) ;
        }
        else if( type == SessionType.EXERCISE ) {
            typeLbl.setIcon( exerciseIcon ) ;
        }
        else if( type == SessionType.THEORY ) {
            typeLbl.setIcon( theoryIcon ) ;
        }
        else if( type == SessionType.EXERCISE ) {
            typeLbl.setIcon( lectureIcon ) ;
        }
    }

    public void setTopicLabel( Topic topic ) {
        topicLbl.setText( topic == null ? "" : topic.getTopicName() ) ;
    }
    
    public void setBookLabel( Book book ) {
        bookLbl.setText( book == null ? "" : book.getBookShortName() ) ;
    }
    
    public void setProblemLabel( Problem problem ) {
        
        if( problem == null ) {
            problemLbl.setText( "" ) ;
        }
        else {
            StringBuffer text = new StringBuffer() ;
            
            if( problem.getStarred() ) {
                text.append( "* " ) ;
            }
            
            text.append( "Ch-" )
                .append( problem.getChapterId() )
                .append( "/" )
                .append( problem.getExerciseName() )
                .append( " - " )
                .append( problem.getProblemTag() ) ;
            
            problemLbl.setText( text.toString() ) ;
        }
    }

    public void updateNumProblemsLeftInBookLabel( int num ) {
        sumsLeftLbl.setText( num < 0 ? "" : "[" + Integer.toString( num ) + "]" ) ;
    }
    
    public void updateSessionTimeLabel( long seconds ) {
        sTimeLbl.setText( seconds < 0 ? "" : getElapsedTimeLabel( seconds, true ) ) ;
    }
    
    public void updateLapTimeLabel( long seconds ) {
        lTimeLbl.setText( seconds < 0 ? "" : getElapsedTimeLabel( seconds, false ) ) ;
    }
    
    public void updateOutcomeCounts( Session session ) {
        if( session != null ) {
            updateNumSolvedLabel  ( session.getNumSolved()  ) ;
            updateNumRedoLabel    ( session.getNumRedo()    ) ;
            updateNumPigeonLabel  ( session.getNumPigeon()  ) ;
            updateNumSkippedLabel ( session.getNumSkipped() ) ;
            updateNumIgnoredLabel ( session.getNumIgnored() ) ;
        }
        else {
            updateNumSolvedLabel  ( -1 ) ;
            updateNumRedoLabel    ( -1 ) ;
            updateNumPigeonLabel  ( -1 ) ;
            updateNumSkippedLabel ( -1 ) ;
            updateNumIgnoredLabel ( -1 ) ;
        }
    }
    
    private void updateNumSolvedLabel( int num ) {
        numSolvedLbl.setText( num < 0 ? "" : Integer.toString( num ) ) ;
    }
    
    private void updateNumRedoLabel( int num ) {
        numRedoLbl.setText( num < 0 ? "" : Integer.toString( num ) ) ;
    }
    
    private void updateNumPigeonLabel( int num ) {
        numPigeonLbl.setText( num < 0 ? "" : Integer.toString( num ) ) ;
    }
    
    private void updateNumSkippedLabel( int num ) {
        numSkipLbl.setText( num < 0 ? "" : Integer.toString( num ) ) ;
    }
    
    private void updateNumIgnoredLabel( int num ) {
        numIgnoreLbl.setText( num < 0 ? "" : Integer.toString( num ) ) ;
    }
    
    public void setBtn1UI( Btn1Type btnType ) {
        
        if( btnType == Btn1Type.PLAY ) {
            btn1Lbl.setIcon( playIcon ) ;
        }
        else if( btnType == Btn1Type.PAUSE ) {
            btn1Lbl.setIcon( pauseIcon ) ;
        }
        else if( btnType == Btn1Type.CLEAR ) {
            btn1Lbl.setIcon( null ) ;
        }
    }
    
    public void setBtn2UI( Btn2Type btnType ) {
        
        btn2Lbl.setBackground( UIConstant.BG_COLOR ) ;
        btn2Lbl.setText( null ) ;
        
        switch( btnType ) {
            case STOP:
                btn2Lbl.setIcon( stopIcon ) ;
                break ;
            case CHANGE:
                btn2Lbl.setBackground( FN_A_COLOR ) ;
                btn2Lbl.setIcon( null ) ;
                btn2Lbl.setText( "Change" ) ;
                break ;
            case CANCEL:
                btn2Lbl.setBackground( BG_COLOR ) ;
                btn2Lbl.setIcon( cancelIcon ) ;
                btn2Lbl.setText( null ) ;
                break ;
            case CLEAR:
                break ;
        }
    }
    // ----------------- Panel content manipulation [Ends] -------------------
    
    // ----------------- UI Utility methods [Start] -------------------
    
    private String getElapsedTimeLabel( long seconds, boolean longFormat ) {
        int secs    = (int)(seconds) % 60 ;
        int minutes = (int)((seconds / 60) % 60) ;
        int hours   = (int)(seconds / (60*60)) ;
        
        if( longFormat ) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs ) ;
        }
        return String.format("%02d:%02d", minutes, secs ) ;
    }
    
    // ----------------- UI Utility methods [End] -------------------
}
