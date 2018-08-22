package com.sandy.sconsole.screenlet.study.large.tile.control;

import static com.sandy.sconsole.core.frame.UIConstant.* ;
import static java.awt.Color.* ;
import static javax.swing.SwingConstants.* ;

import java.awt.BorderLayout ;
import java.awt.Color ;

import javax.swing.Icon ;
import javax.swing.ImageIcon ;
import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.border.LineBorder ;

import com.sandy.common.util.StringUtil ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;

import info.clearthought.layout.TableLayout ;

@SuppressWarnings( "serial" )
public class SessionControlTileUI extends AbstractScreenletTile {

    private class LabelMeta {
        JPanel panel = null ;
        String constraint = null ;
        JLabel label = null ;
        float fontSize = -1F ;
        Color fgColor = null ;
        boolean border = true ;
        
        LabelMeta( JPanel p, JLabel l, String con ) {
            this( p, l, con, 25F, true ) ;
        }
        
        LabelMeta( JPanel p, JLabel l, String con, float fSz) {
            this( p, l, con, fSz, Color.GRAY, true ) ;
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

    protected JLabel typeLbl      = createDefaultLabel( "" ) ;
    protected JLabel topicLbl     = createDefaultLabel( "Work, Energy & Power" ) ;
    protected JLabel bookLbl      = createDefaultLabel( "Cengage - Physics" ) ;
    protected JLabel problemLbl   = createDefaultLabel( "8.1 / CAE-2 / 23" ) ;
    protected JLabel sTimeLbl     = createDefaultLabel( "00:23:00" ) ;
    protected JLabel numSkipLbl   = createDefaultLabel( "0" ) ;
    protected JLabel numSolvedLbl = createDefaultLabel( "5" ) ;
    protected JLabel numRedoLbl   = createDefaultLabel( "2" ) ;
    protected JLabel numPigeonLbl = createDefaultLabel( "1" ) ;
    protected JLabel lTimeLbl     = createDefaultLabel( "4:00" ) ;
    protected JLabel btnSkipLbl   = createDefaultLabel( "Skip (A)" ) ;
    protected JLabel btnSolvedLbl = createDefaultLabel( "Solved (B)" ) ;
    protected JLabel btnRedoLbl   = createDefaultLabel( "Redo (C)" ) ;
    protected JLabel btnPigeonLbl = createDefaultLabel( "Pigeon (D)" ) ;
    protected JLabel btn1Lbl      = createDefaultLabel( "" ) ;
    protected JLabel btn2Lbl      = createDefaultLabel( "" ) ;
    
    protected JPanel typePnl      = createDefaultPanel() ;
    protected JPanel topicPnl     = createDefaultPanel() ;
    protected JPanel bookPnl      = createDefaultPanel() ;
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

    private Icon exerciseIcon = null ;
    private Icon theoryIcon   = null ;
    private Icon lectureIcon  = null ;
    private Icon playIcon     = null ;
    private Icon pauseIcon    = null ;
    private Icon stopIcon     = null ;
    
    private LabelMeta[] labelMetaList = {
        new LabelMeta( typePnl,      typeLbl,      "0,0,1,2" ),
        new LabelMeta( topicPnl,     topicLbl,     "2,0,11,1", 50F, false ),
        new LabelMeta( bookPnl,      bookLbl,      "2,2,11,2", 35F, false ),
        new LabelMeta( problemPnl,   problemLbl,   "0,3,7,4",  60F ),
        new LabelMeta( sTimePnl,     sTimeLbl,     "8,3,11,4", 50F ),
        new LabelMeta( numSkipPnl,   numSkipLbl,   "0,5,1,6",  60F ),
        new LabelMeta( numSolvedPnl, numSolvedLbl, "2,5,3,6",  60F ),
        new LabelMeta( numRedoPnl,   numRedoLbl,   "4,5,5,6",  60F ),
        new LabelMeta( numPigeonPnl, numPigeonLbl, "6,5,7,6",  60F ),
        new LabelMeta( lTimePnl,     lTimeLbl,     "8,5,11,6", 60F ),
        new LabelMeta( btnSkipPnl,   btnSkipLbl,   "0,7,1,7" ),
        new LabelMeta( btnSolvedPnl, btnSolvedLbl, "2,7,3,7" ),
        new LabelMeta( btnRedoPnl,   btnRedoLbl,   "4,7,5,7" ),
        new LabelMeta( btnPigeonPnl, btnPigeonLbl, "6,7,7,7" ),
        new LabelMeta( btn1Pnl,      btn1Lbl,      "8,7,9,7" ),
        new LabelMeta( btn2Pnl,      btn2Lbl,      "10,7,11,7" )
    } ;

    public SessionControlTileUI( ScreenletPanel parent ) {
        super( parent ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        setLayout( createLayout() ) ;
        loadIcons() ;
        for( LabelMeta meta : labelMetaList ) {
            addPanel( meta ) ;
        }
        
        typeLbl.setIcon( exerciseIcon ) ;
        
        btnSkipPnl.setBackground( FN_A_COLOR ) ;
        btnSolvedPnl.setBackground( FN_B_COLOR ) ;
        btnRedoPnl.setBackground( FN_C_COLOR ) ;
        btnPigeonPnl.setBackground( FN_D_COLOR ) ;

        btnSkipLbl.setForeground( WHITE ) ;
        btnSolvedLbl.setForeground( WHITE ) ;
        btnRedoLbl.setForeground( WHITE ) ;
        btnPigeonLbl.setForeground( WHITE ) ;
        
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
        label.setForeground( meta.fgColor ) ;
        if( meta.fontSize > 0 ) {
            label.setFont( BASE_FONT.deriveFont( meta.fontSize ) ) ;
        }
        
        JPanel panel = meta.panel ;
        panel.add( meta.label ) ;
        if( meta.border ) {
            panel.setBorder( new LineBorder( Color.GRAY ) ) ;
        }
        
        add( panel, meta.constraint ) ;
    }
}
