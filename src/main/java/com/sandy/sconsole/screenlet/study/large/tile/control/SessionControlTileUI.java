package com.sandy.sconsole.screenlet.study.large.tile.control;

import static com.sandy.sconsole.core.frame.UIConstant.* ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;
import static javax.swing.SwingConstants.* ;

import java.awt.* ;
import java.util.* ;

import javax.swing.* ;
import javax.swing.border.* ;

import org.apache.log4j.* ;

import com.sandy.common.util.* ;
import com.sandy.sconsole.core.frame.* ;
import com.sandy.sconsole.core.remote.* ;
import com.sandy.sconsole.core.screenlet.* ;
import com.sandy.sconsole.dao.entity.* ;
import com.sandy.sconsole.dao.entity.master.* ;

import info.clearthought.layout.* ;

@SuppressWarnings( "serial" )
public abstract class SessionControlTileUI extends AbstractScreenletTile {

    static final Logger log = Logger.getLogger( SessionControlTileUI.class ) ;
    
    private class LabelMeta {
        JPanel panel = null ;
        String constraint = null ;
        JLabel label = null ;
        float fontSize = -1F ;
        Color fgColor = null ;
        boolean border = true ;
        boolean initialized = false ;
        
        LabelMeta( JPanel p, JLabel l, String con, float fSz, Color color, boolean bdr ) {
            constraint = con ;
            panel = p ;
            label = l ;
            fontSize = fSz ;
            fgColor = color ;
            border = bdr ;
        }
    }
    
    private static final int NUM_ROW = 8 ;
    private static final int NUM_COL = 12 ;
    
    protected static Color TYPE_LBL_FG       = Color.GRAY ;
    protected static Color TOPIC_LBL_FG      = Color.LIGHT_GRAY ;
    protected static Color BOOK_LBL_FG       = Color.GRAY ;
    protected static Color SUMS_LEFT_LBL_FG  = Color.GRAY ;
    protected static Color PROBLEM_LBL_FG    = Color.decode( "#C2E880" ) ;
    protected static Color STIME_LBL_FG      = Color.decode( "#75BAF9" ) ;
    protected static Color NUM_SKIP_LBL_FG   = UIConstant.FN_A_COLOR ;
    protected static Color NUM_SOLVED_LBL_FG = UIConstant.FN_B_COLOR ;
    protected static Color NUM_REDO_LBL_FG   = UIConstant.FN_C_COLOR ;
    protected static Color NUM_PIGEON_LBL_FG = UIConstant.FN_D_COLOR ;
    protected static Color LTIME_LBL_FG      = Color.decode( "#F98BCC" ) ;
    protected static Color BTN_SKIP_LBL_FG   = Color.WHITE ;
    protected static Color BTN_SOLVED_LBL_FG = Color.WHITE ;
    protected static Color BTN_REDO_LBL_FG   = Color.WHITE ;
    protected static Color BTN_PIGEON_LBL_FG = Color.WHITE ;
    protected static Color BTN1_LBL_FG       = Color.GRAY ;
    protected static Color BTN2_LBL_FG       = Color.WHITE ;
    
    private static Border INVALID_PANEL_BORDER = new LineBorder( Color.RED, 3 ) ;
    
    protected enum Btn1Type { PLAY, PAUSE, CLEAR } ;
    protected enum Btn2Type { STOP, CHANGE, CLEAR, CANCEL } ;
    protected enum UseCase { 
        PLAY_SESSION, 
        PAUSE_SESSION, 
        STOP_SESSION,
        CHANGE_SESSION
    }
    
    protected JLabel typeLbl      = createDefaultLabel( "" ) ;
    protected JLabel topicLbl     = createDefaultLabel( "" ) ;
    protected JLabel bookLbl      = createDefaultLabel( "" ) ;
    protected JLabel sumsLeftLbl  = createDefaultLabel( "" ) ;
    protected JLabel problemLbl   = createDefaultLabel( "" ) ;
    protected JLabel sTimeLbl     = createDefaultLabel( "" ) ;
    protected JLabel numSkipLbl   = createDefaultLabel( "" ) ;
    protected JLabel numSolvedLbl = createDefaultLabel( "" ) ;
    protected JLabel numRedoLbl   = createDefaultLabel( "" ) ;
    protected JLabel numPigeonLbl = createDefaultLabel( "" ) ;
    protected JLabel lTimeLbl     = createDefaultLabel( "" ) ;
    protected JLabel btnSkipLbl   = createDefaultLabel( "Skip [A]" ) ;
    protected JLabel btnSolvedLbl = createDefaultLabel( "Solved [B]" ) ;
    protected JLabel btnRedoLbl   = createDefaultLabel( "Redo [C]" ) ;
    protected JLabel btnPigeonLbl = createDefaultLabel( "Pigeon [D]" ) ;
    protected JLabel btn1Lbl      = createDefaultLabel( "" ) ;
    protected JLabel btn2Lbl      = createDefaultLabel( "" ) ;
    
    protected JPanel typePnl      = createDefaultPanel() ;
    protected JPanel topicPnl     = createDefaultPanel() ;
    protected JPanel bookPnl      = createDefaultPanel() ;
    protected JPanel sumsLeftPnl  = createDefaultPanel() ;
    protected JPanel problemPnl   = createDefaultPanel() ;
    protected JPanel sTimePnl     = createDefaultPanel() ;
    protected JPanel numSkipPnl   = createDefaultPanel() ;
    protected JPanel numSolvedPnl = createDefaultPanel() ;
    protected JPanel numRedoPnl   = createDefaultPanel() ;
    protected JPanel numPigeonPnl = createDefaultPanel() ;
    protected JPanel lTimePnl     = createDefaultPanel() ;
    protected JPanel btnSkipPnl   = createDefaultPanel() ;
    protected JPanel btnSolvedPnl = createDefaultPanel() ;
    protected JPanel btnRedoPnl   = createDefaultPanel() ;
    protected JPanel btnPigeonPnl = createDefaultPanel() ;
    protected JPanel btn1Pnl      = createDefaultPanel() ;
    protected JPanel btn2Pnl      = createDefaultPanel() ;

    protected Icon exerciseIcon = null ;
    protected Icon theoryIcon   = null ;
    protected Icon lectureIcon  = null ;
    protected Icon playIcon     = null ;
    protected Icon pauseIcon    = null ;
    protected Icon stopIcon     = null ;
    protected Icon cancelIcon   = null ;
    
    private Map<JPanel, LabelMeta> pmMap = new HashMap<>() ;
    private UseCase currentUseCase = UseCase.STOP_SESSION ;
    
    protected RemoteKeyEventProcessor keyProcessor = null ;
    
    public SessionControlTileUI( ScreenletPanel parent ) {
        super( parent ) ;
        populatePanelMetaMap() ;
        setUpUI() ;
    }
    
    protected abstract void executeChangeSessionDetailsUseCase() ;
    
    private void populatePanelMetaMap() {
        pmMap.put( typePnl,      new LabelMeta( typePnl,      typeLbl,      "0,0,1,2",  25F, TYPE_LBL_FG,       true  ) ) ;
        pmMap.put( topicPnl,     new LabelMeta( topicPnl,     topicLbl,     "2,0,11,1", 50F, TOPIC_LBL_FG,      false ) ) ;
        pmMap.put( bookPnl,      new LabelMeta( bookPnl,      bookLbl,      "2,2,9,2",  40F, BOOK_LBL_FG,       false ) ) ;
        pmMap.put( sumsLeftPnl,  new LabelMeta( sumsLeftPnl,  sumsLeftLbl,  "10,2,11,2",35F, SUMS_LEFT_LBL_FG,  false ) ) ;
        pmMap.put( problemPnl,   new LabelMeta( problemPnl,   problemLbl,   "0,3,7,4",  40F, PROBLEM_LBL_FG,    true  ) ) ;
        pmMap.put( sTimePnl,     new LabelMeta( sTimePnl,     sTimeLbl,     "8,3,11,4", 60F, STIME_LBL_FG,      true  ) ) ;
        pmMap.put( numSkipPnl,   new LabelMeta( numSkipPnl,   numSkipLbl,   "0,5,1,6",  60F, NUM_SKIP_LBL_FG,   true  ) ) ;
        pmMap.put( numSolvedPnl, new LabelMeta( numSolvedPnl, numSolvedLbl, "2,5,3,6",  60F, NUM_SOLVED_LBL_FG, true  ) ) ;
        pmMap.put( numRedoPnl,   new LabelMeta( numRedoPnl,   numRedoLbl,   "4,5,5,6",  60F, NUM_REDO_LBL_FG,   true  ) ) ;
        pmMap.put( numPigeonPnl, new LabelMeta( numPigeonPnl, numPigeonLbl, "6,5,7,6",  60F, NUM_PIGEON_LBL_FG, true  ) ) ;
        pmMap.put( lTimePnl,     new LabelMeta( lTimePnl,     lTimeLbl,     "8,5,11,6", 60F, LTIME_LBL_FG,      true  ) ) ;
        pmMap.put( btnSkipPnl,   new LabelMeta( btnSkipPnl,   btnSkipLbl,   "0,7,1,7",  30F, BTN_SKIP_LBL_FG,   false ) ) ;
        pmMap.put( btnSolvedPnl, new LabelMeta( btnSolvedPnl, btnSolvedLbl, "2,7,3,7",  30F, BTN_SOLVED_LBL_FG, false ) ) ;
        pmMap.put( btnRedoPnl,   new LabelMeta( btnRedoPnl,   btnRedoLbl,   "4,7,5,7",  30F, BTN_REDO_LBL_FG,   false ) ) ;
        pmMap.put( btnPigeonPnl, new LabelMeta( btnPigeonPnl, btnPigeonLbl, "6,7,7,7",  30F, BTN_PIGEON_LBL_FG, false ) ) ;
        pmMap.put( btn1Pnl,      new LabelMeta( btn1Pnl,      btn1Lbl,      "8,7,9,7",  25F, BTN1_LBL_FG,       true  ) ) ;
        pmMap.put( btn2Pnl,      new LabelMeta( btn2Pnl,      btn2Lbl,      "10,7,11,7",25F, BTN2_LBL_FG,       true  ) ) ;
    }
    
    private void setUpUI() {
        setLayout( createLayout() ) ;
        loadIcons() ;
        for( LabelMeta meta : pmMap.values() ) {
            addPanel( meta ) ;
        }
        
        btn1Lbl.setIcon( playIcon ) ;
        btn2Lbl.setIcon( stopIcon ) ;
    }
    
    private void loadIcons() {
        exerciseIcon = new ImageIcon( getClass().getResource( "/icons/exercise.png" ) ) ;
        theoryIcon   = new ImageIcon( getClass().getResource( "/icons/theory.png"   ) ) ;
        lectureIcon  = new ImageIcon( getClass().getResource( "/icons/lecture.png"  ) ) ;
        playIcon     = new ImageIcon( getClass().getResource( "/icons/play.png"     ) ) ;
        pauseIcon    = new ImageIcon( getClass().getResource( "/icons/pause.png"    ) ) ;
        stopIcon     = new ImageIcon( getClass().getResource( "/icons/stop.png"     ) ) ;
        cancelIcon   = new ImageIcon( getClass().getResource( "/icons/cancel.png"     ) ) ;
    }
    
    private JLabel createDefaultLabel( String defaultText ) {
        
        JLabel label = new JLabel() ;
        label.setForeground( Color.DARK_GRAY ) ;
        label.setHorizontalAlignment( CENTER );
        label.setVerticalAlignment( CENTER );
        
        if( StringUtil.isNotEmptyOrNull( defaultText ) ) {
            label.setText( defaultText ) ;
        }
        return label ;
    }
    
    private JPanel createDefaultPanel() {
        
        JPanel panel = new JPanel() ;
        panel.setBackground( UIConstant.BG_COLOR ) ;
        panel.setLayout( new BorderLayout() ) ;
        return panel ;
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
        JPanel panel = meta.panel ;
        
        if( !meta.initialized ) {
            label.setForeground( meta.fgColor ) ;
            if( meta.fontSize > 0 ) {
                label.setFont( BASE_FONT.deriveFont( meta.fontSize ) ) ;
            }
            
            panel.add( meta.label ) ;
            if( meta.border ) {
                panel.setBorder( TILE_BORDER ) ;
            }
            meta.initialized = true ;
        }
        add( panel, meta.constraint ) ;
        validate() ;
        repaint() ;
    }
    
    protected UseCase getCurrentUseCase() {
        return this.currentUseCase ;
    }
    
    protected void setCurrentUseCase( UseCase uc ) {
        this.currentUseCase = uc ;
    }
    
    protected void setSessionTypeIcon( String type ) {
        
        if( type == null ) {
            typeLbl.setIcon( null ) ;
        }
        else if( type.equals( Session.TYPE_EXERCISE ) ) {
            typeLbl.setIcon( exerciseIcon ) ;
        }
        else if( type.equals( Session.TYPE_THEORY ) ) {
            typeLbl.setIcon( theoryIcon ) ;
        }
        else if( type.equals( Session.TYPE_LECTURE ) ) {
            typeLbl.setIcon( lectureIcon ) ;
        }
    }

    protected void setTopicLabel( Topic topic ) {
        if( topic == null ) {
            topicLbl.setText( "" ) ;
        }
        else {
            topicLbl.setText( topic.getTopicName() ) ;
        }
    }
    
    protected void highlightPanelValidity( JPanel panel, boolean valid ) {
        if( !valid ) {
            panel.setBorder( INVALID_PANEL_BORDER ) ;
        }
        else {
            LabelMeta meta = pmMap.get( panel ) ;
            if( meta.border ) {
                panel.setBorder( TILE_BORDER ) ;
            }
            else {
                panel.setBorder( null ) ;
            }
        }
    }
    
    protected void setProblemLabel( Problem problem ) {
        problemLbl.setText( "" ) ;
        if( problem != null ) {
            
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

    protected void setBookLabel( Book book ) {
        if( book != null ) {
            bookLbl.setText( book.getBookShortName() ) ;
        }
        else {
            bookLbl.setText( "" ) ;
        }
    }
    
    protected void activateProblemOutcomeButtons( boolean activate ) {
        
        if( activate ) {
            btnSkipPnl.setBackground( FN_A_COLOR ) ;
            btnSolvedPnl.setBackground( FN_B_COLOR ) ;
            btnRedoPnl.setBackground( FN_C_COLOR ) ;
            btnPigeonPnl.setBackground( FN_D_COLOR ) ;

            btnSkipLbl.setForeground( Color.WHITE ) ;
            btnSolvedLbl.setForeground( Color.WHITE ) ;
            btnRedoLbl.setForeground( Color.WHITE ) ;
            btnPigeonLbl.setForeground( Color.WHITE ) ;

            btnSkipLbl.setText( "Skip [A]" ) ;
            btnSolvedLbl.setText( "Solved [B]" ) ;
            btnRedoLbl.setText( "Redo [C]" ) ;
            btnPigeonLbl.setText( "Pigeon [D]" ) ;
        }
        else {
            btnSkipPnl.setBackground( BG_COLOR ) ;
            btnSolvedPnl.setBackground( BG_COLOR ) ;
            btnRedoPnl.setBackground( BG_COLOR ) ;
            btnPigeonPnl.setBackground( BG_COLOR ) ;

            btnSkipLbl.setForeground( Color.DARK_GRAY ) ;
            btnSolvedLbl.setForeground( Color.DARK_GRAY ) ;
            btnRedoLbl.setForeground( Color.DARK_GRAY ) ;
            btnPigeonLbl.setForeground( Color.DARK_GRAY ) ;

            btnSkipLbl.setText( "Skip" ) ;
            btnSolvedLbl.setText( "Solved" ) ;
            btnRedoLbl.setText( "Redo" ) ;
            btnPigeonLbl.setText( "Pigeon" ) ;
        }
    }
    
    protected void setBtn1( Btn1Type btnType ) {
        switch( btnType ) {
            case PLAY:
                btn1Lbl.setIcon( playIcon ) ;
                keyProcessor.setKeyEnabled( true, RUN_PLAYPAUSE ) ;
                break ;
            case PAUSE:
                btn1Lbl.setIcon( pauseIcon ) ;
                keyProcessor.setKeyEnabled( true, RUN_PLAYPAUSE ) ;
                break ;
            case CLEAR:
                btn1Lbl.setIcon( null ) ;
                keyProcessor.setKeyEnabled( false, RUN_PLAYPAUSE ) ;
                break ;
        }
    }
    
    protected void setBtn2( Btn2Type btnType ) {
        
        btn2Pnl.setBackground( UIConstant.BG_COLOR ) ;
        btn2Lbl.setText( null ) ;
        switch( btnType ) {
            case STOP:
                btn2Lbl.setIcon( stopIcon ) ;
                keyProcessor.setKeyEnabled( true, RUN_STOP ) ;
                break ;
            case CHANGE:
                btn2Pnl.setBackground( FN_A_COLOR ) ;
                btn2Lbl.setIcon( null ) ;
                btn2Lbl.setText( "Change [A]" ) ;
                keyProcessor.setFnHandler( FN_A, new Handler( "Change" ) {
                    public void handle() {
                        executeChangeSessionDetailsUseCase() ;
                    }
                } ) ;
                keyProcessor.setKeyEnabled( true, FN_A ) ;
                break ;
            case CANCEL:
                btn2Pnl.setBackground( BG_COLOR ) ;
                btn2Lbl.setIcon( cancelIcon ) ;
                btn2Lbl.setText( null ) ;
                keyProcessor.setKeyEnabled( true, FN_CANCEL ) ;
                break ;
            case CLEAR:
                keyProcessor.setKeyEnabled( false, RUN_STOP, FN_A ) ;
                break ;
        }
    }
    
    private String getElapsedTimeLabel( long seconds, boolean longFormat ) {
        int secs    = (int)(seconds) % 60 ;
        int minutes = (int)((seconds / 60) % 60) ;
        int hours   = (int)(seconds / (60*60)) ;
        
        if( longFormat ) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs ) ;
        }
        return String.format("%02d:%02d", minutes, secs ) ;
    }
    
    protected void updateSessionTimeLabel( long seconds ) {
        sTimeLbl.setText( getElapsedTimeLabel( seconds, true ) ) ;
    }
    
    protected void updateLapTimeLabel( long seconds ) {
        lTimeLbl.setText( getElapsedTimeLabel( seconds, false ) ) ;
    }
    
    protected void updateNumSkippedLabel( int num ) {
        numSkipLbl.setText( Integer.toString( num ) ) ;
    }
    
    protected void updateNumSolvedLabel( int num ) {
        numSolvedLbl.setText( Integer.toString( num ) ) ;
    }
    
    protected void updateNumRedoLabel( int num ) {
        numRedoLbl.setText( Integer.toString( num ) ) ;
    }
    
    protected void updateNumPigeonLabel( int num ) {
        numPigeonLbl.setText( Integer.toString( num ) ) ;
    }
    
    protected void updateNumProblemsLeftInChapterLabel( int num ) {
        if( num < 0 ) {
            sumsLeftLbl.setText( "" ) ;
        }
        else {
            sumsLeftLbl.setText( "[" + Integer.toString( num ) + "]" ) ;
        }
    }
}
