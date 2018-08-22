package com.sandy.sconsole.screenlet.study.large.tile.control;

import static com.sandy.sconsole.core.frame.UIConstant.BASE_FONT ;
import static com.sandy.sconsole.core.frame.UIConstant.FN_A_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.FN_B_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.FN_C_COLOR ;
import static com.sandy.sconsole.core.frame.UIConstant.FN_D_COLOR ;
import static java.awt.Color.WHITE ;
import static javax.swing.SwingConstants.CENTER ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.util.HashMap ;
import java.util.Map ;

import javax.swing.Icon ;
import javax.swing.ImageIcon ;
import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.border.LineBorder ;

import com.sandy.common.util.StringUtil ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.dao.entity.master.Problem ;

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
        
        LabelMeta( JPanel p, JLabel l, String con, boolean border ) {
            this( p, l, con, 25F, Color.GRAY, border ) ;
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

    private JLabel typeLbl      = createDefaultLabel( "" ) ;
    private JLabel topicLbl     = createDefaultLabel( "" ) ;
    private JLabel bookLbl      = createDefaultLabel( "" ) ;
    private JLabel problemLbl   = createDefaultLabel( "" ) ;
    private JLabel sTimeLbl     = createDefaultLabel( "" ) ;
    private JLabel numSkipLbl   = createDefaultLabel( "" ) ;
    private JLabel numSolvedLbl = createDefaultLabel( "" ) ;
    private JLabel numRedoLbl   = createDefaultLabel( "" ) ;
    private JLabel numPigeonLbl = createDefaultLabel( "" ) ;
    private JLabel lTimeLbl     = createDefaultLabel( "" ) ;
    private JLabel btnSkipLbl   = createDefaultLabel( "Skip (A)" ) ;
    private JLabel btnSolvedLbl = createDefaultLabel( "Solved (B)" ) ;
    private JLabel btnRedoLbl   = createDefaultLabel( "Redo (C)" ) ;
    private JLabel btnPigeonLbl = createDefaultLabel( "Pigeon (D)" ) ;
    private JLabel btn1Lbl      = createDefaultLabel( "" ) ;
    private JLabel btn2Lbl      = createDefaultLabel( "" ) ;
    
    private JPanel typePnl      = createDefaultPanel() ;
    private JPanel topicPnl     = createDefaultPanel() ;
    private JPanel bookPnl      = createDefaultPanel() ;
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
    
    public SessionControlTileUI( ScreenletPanel parent ) {
        super( parent ) ;
        populatePanelMetaMap() ;
        setUpUI() ;
    }
    
    private void populatePanelMetaMap() {
        pmMap.put( typePnl,      new LabelMeta( typePnl,      typeLbl,      "0,0,1,2"              ) ) ;
        pmMap.put( topicPnl,     new LabelMeta( topicPnl,     topicLbl,     "2,0,11,1", 50F, false ) ) ;
        pmMap.put( bookPnl,      new LabelMeta( bookPnl,      bookLbl,      "2,2,11,2", 35F, false ) ) ;
        pmMap.put( problemPnl,   new LabelMeta( problemPnl,   problemLbl,   "0,3,7,4",  60F        ) ) ;
        pmMap.put( sTimePnl,     new LabelMeta( sTimePnl,     sTimeLbl,     "8,3,11,4", 50F        ) ) ;
        pmMap.put( numSkipPnl,   new LabelMeta( numSkipPnl,   numSkipLbl,   "0,5,1,6",  60F        ) ) ;
        pmMap.put( numSolvedPnl, new LabelMeta( numSolvedPnl, numSolvedLbl, "2,5,3,6",  60F        ) ) ;
        pmMap.put( numRedoPnl,   new LabelMeta( numRedoPnl,   numRedoLbl,   "4,5,5,6",  60F        ) ) ;
        pmMap.put( numPigeonPnl, new LabelMeta( numPigeonPnl, numPigeonLbl, "6,5,7,6",  60F        ) ) ;
        pmMap.put( lTimePnl,     new LabelMeta( lTimePnl,     lTimeLbl,     "8,5,11,6", 60F        ) ) ;
        pmMap.put( btnSkipPnl,   new LabelMeta( btnSkipPnl,   btnSkipLbl,   "0,7,1,7", false       ) ) ;
        pmMap.put( btnSolvedPnl, new LabelMeta( btnSolvedPnl, btnSolvedLbl, "2,7,3,7", false       ) ) ;
        pmMap.put( btnRedoPnl,   new LabelMeta( btnRedoPnl,   btnRedoLbl,   "4,7,5,7", false       ) ) ;
        pmMap.put( btnPigeonPnl, new LabelMeta( btnPigeonPnl, btnPigeonLbl, "6,7,7,7", false       ) ) ;
        pmMap.put( btn1Pnl,      new LabelMeta( btn1Pnl,      btn1Lbl,      "8,7,9,7"              ) ) ;
        pmMap.put( btn2Pnl,      new LabelMeta( btn2Pnl,      btn2Lbl,      "10,7,11,7"            ) ) ;
    }
    
    private void setUpUI() {
        setLayout( createLayout() ) ;
        loadIcons() ;
        for( LabelMeta meta : pmMap.values() ) {
            addPanel( meta ) ;
        }
        
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
    
    private void removePanel( JPanel panel ) {
        remove( panel ) ;
        validate() ;
        repaint() ;
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
            panel.setBorder( new LineBorder( UIConstant.TILE_BORDER_COLOR.darker() ) ) ;
        }
        
        add( panel, meta.constraint ) ;
    }
    
    protected void setIcon( String type ) {
        
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

    protected void setTopic( String name ) {
        topicLbl.setText( name ) ;
    }
    
    protected void setProblem( Problem problem ) {
        problemLbl.setText( "" ) ;
        if( problem != null ) {
            problemLbl.setText( problem.getChapterId() + " / " + 
                                problem.getExerciseName() + " / " + 
                                problem.getProblemTag() ) ;
        }
    }

    protected void setBook( String name ) {
        bookLbl.setText( "" ) ;
        if( name != null ) {
            bookLbl.setText( name ) ;
        }
    }
    
    protected void setProblemButtonsVisible( boolean visible ) {
        
        if( !visible ) {
            removePanel( btnSkipPnl ) ;
            removePanel( btnSolvedPnl ) ;
            removePanel( btnRedoPnl ) ;
            removePanel( btnPigeonPnl ) ;
        }
        else {
            addPanel( pmMap.get( btnSkipPnl   ) ) ;
            addPanel( pmMap.get( btnSolvedPnl ) ) ;
            addPanel( pmMap.get( btnRedoPnl   ) ) ;
            addPanel( pmMap.get( btnPigeonPnl ) ) ;
        }
    }
}
