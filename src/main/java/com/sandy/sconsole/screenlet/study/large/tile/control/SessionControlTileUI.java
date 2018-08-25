package com.sandy.sconsole.screenlet.study.large.tile.control;

import static com.sandy.sconsole.core.frame.UIConstant.* ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;
import static javax.swing.SwingConstants.* ;

import java.awt.* ;
import java.util.* ;

import javax.swing.* ;
import javax.swing.border.* ;

import com.sandy.common.util.* ;
import com.sandy.sconsole.core.frame.* ;
import com.sandy.sconsole.core.remote.* ;
import com.sandy.sconsole.core.screenlet.* ;
import com.sandy.sconsole.dao.entity.* ;
import com.sandy.sconsole.dao.entity.master.* ;

import info.clearthought.layout.* ;

@SuppressWarnings( "serial" )
public abstract class SessionControlTileUI extends AbstractScreenletTile {

    private class LabelMeta {
        JPanel panel = null ;
        String constraint = null ;
        JLabel label = null ;
        float fontSize = -1F ;
        Color fgColor = null ;
        boolean border = true ;
        boolean initialized = false ;
        
        LabelMeta( JPanel p, JLabel l, String con ) {
            this( p, l, con, 25F, true ) ;
        }
        
        LabelMeta( JPanel p, JLabel l, String con, float fSz) {
            this( p, l, con, fSz, Color.GRAY, true ) ;
        }
        
        LabelMeta( JPanel p, JLabel l, String con, Color fgCol, boolean border ) {
            this( p, l, con, 25F, fgCol, border ) ;
        }
        
        LabelMeta( JPanel p, JLabel l, String con, float fSz, Color fgColor ) {
            this( p, l, con, fSz, fgColor, true ) ;
        }
        
        LabelMeta( JPanel p, JLabel l, String con, float fSz, boolean border ) {
            this( p, l, con, fSz, Color.GRAY, border ) ;
        }
        
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
    
    protected enum Btn1Type { PLAY, PAUSE, CLEAR } ;
    protected enum Btn2Type { STOP, CHANGE, CLEAR } ;
    
    private JLabel typeLbl      = createDefaultLabel( "" ) ;
    private JLabel topicLbl     = createDefaultLabel( "" ) ;
    private JLabel bookLbl      = createDefaultLabel( "" ) ;
    private JLabel sumsLeftLbl  = createDefaultLabel( "" ) ;
    private JLabel problemLbl   = createDefaultLabel( "" ) ;
    private JLabel sTimeLbl     = createDefaultLabel( "" ) ;
    private JLabel numSkipLbl   = createDefaultLabel( "" ) ;
    private JLabel numSolvedLbl = createDefaultLabel( "" ) ;
    private JLabel numRedoLbl   = createDefaultLabel( "" ) ;
    private JLabel numPigeonLbl = createDefaultLabel( "" ) ;
    private JLabel lTimeLbl     = createDefaultLabel( "" ) ;
    private JLabel btnSkipLbl   = createDefaultLabel( "Skip [A]" ) ;
    private JLabel btnSolvedLbl = createDefaultLabel( "Solved [B]" ) ;
    private JLabel btnRedoLbl   = createDefaultLabel( "Redo [C]" ) ;
    private JLabel btnPigeonLbl = createDefaultLabel( "Pigeon [D]" ) ;
    private JLabel btn1Lbl      = createDefaultLabel( "" ) ;
    private JLabel btn2Lbl      = createDefaultLabel( "" ) ;
    
    private JPanel typePnl      = createDefaultPanel() ;
    private JPanel topicPnl     = createDefaultPanel() ;
    private JPanel bookPnl      = createDefaultPanel() ;
    private JPanel sumsLeftPnl  = createDefaultPanel() ;
    private JPanel problemPnl   = createDefaultPanel() ;
    private JPanel sTimePnl     = createDefaultPanel() ;
    private JPanel numSkipPnl   = createDefaultPanel() ;
    private JPanel numSolvedPnl = createDefaultPanel() ;
    private JPanel numRedoPnl   = createDefaultPanel() ;
    private JPanel numPigeonPnl = createDefaultPanel() ;
    private JPanel lTimePnl     = createDefaultPanel() ;
    private JPanel btnSkipPnl   = createDefaultPanel() ;
    private JPanel btnSolvedPnl = createDefaultPanel() ;
    private JPanel btnRedoPnl   = createDefaultPanel() ;
    private JPanel btnPigeonPnl = createDefaultPanel() ;
    private JPanel btn1Pnl      = createDefaultPanel() ;
    private JPanel btn2Pnl      = createDefaultPanel() ;

    private Icon exerciseIcon = null ;
    private Icon theoryIcon   = null ;
    private Icon lectureIcon  = null ;
    private Icon playIcon     = null ;
    private Icon pauseIcon    = null ;
    private Icon stopIcon     = null ;
    
    private Map<JPanel, LabelMeta> pmMap = new HashMap<>() ;
    
    protected RemoteKeyEventProcessor keyProcessor = null ;
    
    public SessionControlTileUI( ScreenletPanel parent ) {
        super( parent ) ;
        populatePanelMetaMap() ;
        setUpUI() ;
    }
    
    private void populatePanelMetaMap() {
        pmMap.put( typePnl,      new LabelMeta( typePnl,      typeLbl,      "0,0,1,2"              ) ) ;
        pmMap.put( topicPnl,     new LabelMeta( topicPnl,     topicLbl,     "2,0,11,1", 50F, false ) ) ;
        pmMap.put( bookPnl,      new LabelMeta( bookPnl,      bookLbl,      "2,2,9,2",  40F, false ) ) ;
        pmMap.put( sumsLeftPnl,  new LabelMeta( sumsLeftPnl,  sumsLeftLbl,  "10,2,11,2",30F, false ) ) ;
        pmMap.put( problemPnl,   new LabelMeta( problemPnl,   problemLbl,   "0,3,7,4",  40F, Color.decode( "#C2E880" ) ) ) ;
        pmMap.put( sTimePnl,     new LabelMeta( sTimePnl,     sTimeLbl,     "8,3,11,4", 60F, Color.decode( "#75BAF9" ) ) ) ;
        pmMap.put( numSkipPnl,   new LabelMeta( numSkipPnl,   numSkipLbl,   "0,5,1,6",  60F        ) ) ;
        pmMap.put( numSolvedPnl, new LabelMeta( numSolvedPnl, numSolvedLbl, "2,5,3,6",  60F        ) ) ;
        pmMap.put( numRedoPnl,   new LabelMeta( numRedoPnl,   numRedoLbl,   "4,5,5,6",  60F        ) ) ;
        pmMap.put( numPigeonPnl, new LabelMeta( numPigeonPnl, numPigeonLbl, "6,5,7,6",  60F        ) ) ;
        pmMap.put( lTimePnl,     new LabelMeta( lTimePnl,     lTimeLbl,     "8,5,11,6", 60F, Color.decode( "#F98BCC" ) ) ) ;
        pmMap.put( btnSkipPnl,   new LabelMeta( btnSkipPnl,   btnSkipLbl,   "0,7,1,7",  30F, Color.WHITE, false ) ) ;
        pmMap.put( btnSolvedPnl, new LabelMeta( btnSolvedPnl, btnSolvedLbl, "2,7,3,7",  30F, Color.WHITE, false ) ) ;
        pmMap.put( btnRedoPnl,   new LabelMeta( btnRedoPnl,   btnRedoLbl,   "4,7,5,7",  30F, Color.WHITE, false ) ) ;
        pmMap.put( btnPigeonPnl, new LabelMeta( btnPigeonPnl, btnPigeonLbl, "6,7,7,7",  30F, Color.WHITE, false ) ) ;
        pmMap.put( btn1Pnl,      new LabelMeta( btn1Pnl,      btn1Lbl,      "8,7,9,7"              ) ) ;
        pmMap.put( btn2Pnl,      new LabelMeta( btn2Pnl,      btn2Lbl,      "10,7,11,7", Color.white, true ) ) ;
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
                panel.setBorder( new LineBorder( UIConstant.TILE_BORDER_COLOR ) ) ;
            }
            meta.initialized = true ;
        }
        add( panel, meta.constraint ) ;
        validate() ;
        repaint() ;
    }
    
    protected void setSessionTypeIcon( String type ) {
        
        Icon icon = null ;
        switch( type ) {
            case Session.TYPE_EXERCISE :
                icon = exerciseIcon ;
                break ;
            case Session.TYPE_THEORY :
                icon = theoryIcon ;
                break ;
            case Session.TYPE_LECTURE :
                icon = lectureIcon ;
                break ;
        }
        typeLbl.setIcon( icon ) ;
    }

    protected void setTopicLabel( String name ) {
        topicLbl.setText( name ) ;
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
                .append( "/" )
                .append( problem.getProblemTag() ) ;
            
            problemLbl.setText( text.toString() ) ;
        }
    }

    protected void setBookLabel( String name ) {
        bookLbl.setText( "" ) ;
        if( name != null ) {
            bookLbl.setText( name ) ;
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
                keyProcessor.setFnHandler( FN_A, new FnHandler() {
                    public void process() {
                        changeSessionDetails() ;
                    }
                } ) ;
                keyProcessor.setKeyEnabled( true, FN_A ) ;
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
    
    protected void updateNumProblemsLeftInChapter( int num ) {
        sumsLeftLbl.setText( "[" + Integer.toString( num ) + "]" ) ;
    }
    
    protected abstract void changeSessionDetails() ;
}
