package test ;

import static javax.swing.SwingConstants.* ;

import java.awt.* ;

import javax.swing.* ;
import javax.swing.border.* ;

import com.sandy.common.ui.* ;
import com.sandy.sconsole.core.frame.* ;

import info.clearthought.layout.* ;

@SuppressWarnings( "serial" )
public class SessionControlPanelLayoutPrototype extends JFrame {

    private static final int NUM_ROW = 8 ;
    private static final int NUM_COL = 12 ;
    private static final int CELL_HEIGHT = 100 ;
    private static final int CELL_WIDTH = (int)((16F/9)*CELL_HEIGHT) ;

    private static final String TYPE_PC       = "0,0,1,2" ;
    private static final String TOPIC_PC      = "2,0,11,1" ;
    private static final String BOOK_PC       = "2,2,11,2" ;
    private static final String PROBLEM_PC    = "0,3,7,4" ;
    private static final String S_TIME_PC     = "8,3,11,4" ;
    private static final String NUM_SKIP_PC   = "0,5,1,6" ;
    private static final String NUM_SOLVED_PC = "2,5,3,6" ;
    private static final String NUM_REDO_PC   = "4,5,5,6" ;
    private static final String NUM_PIGEON_PC = "6,5,7,6" ;
    private static final String L_TIME_PC     = "8,5,11,6" ;
    private static final String BTN_SKIP_PC   = "0,7,1,7" ;
    private static final String BTN_SOLVED_PC = "2,7,3,7" ;
    private static final String BTN_REDO_PC   = "4,7,5,7" ;
    private static final String BTN_PIGEON_PC = "6,7,7,7" ;
    private static final String BTN_1_PC      = "8,7,9,7" ;
    private static final String BTN_2_PC      = "10,7,11,7" ;

    private JLabel typePC      = null ;
    private JLabel topicPC     = null ;
    private JLabel bookPC      = null ;
    private JLabel problemPC   = null ;
    private JLabel sTimePC     = null ;
    private JLabel numSkipPC   = null ;
    private JLabel numSolvedPC = null ;
    private JLabel numRedoPC   = null ;
    private JLabel numPigeonPC = null ;
    private JLabel lTimePC     = null ;
    private JLabel btnSkipPC   = null ;
    private JLabel btnSolvedPC = null ;
    private JLabel btnRedoPC   = null ;
    private JLabel btnPigeonPC = null ;
    private JLabel btn1PC      = null ;
    private JLabel btn2PC      = null ;
    
    private int       colorCounter = 0 ;
    private Container cPane        = null ;

    public SessionControlPanelLayoutPrototype() {
        super( "Table Layout Test" ) ;
        initUIComponents() ;
        setUpUI() ;
    }
    
    private void initUIComponents() {
        this.typePC      = new JLabel( "typePC"      ) ;
        this.topicPC     = new JLabel( "topicPC"     ) ;
        this.bookPC      = new JLabel( "bookPC"      ) ;
        this.problemPC   = new JLabel( "problemPC"   ) ;
        this.sTimePC     = new JLabel( "sTimePC"     ) ;
        this.numSkipPC   = new JLabel( "numSkipPC"   ) ;
        this.numSolvedPC = new JLabel( "numSolvedPC" ) ;
        this.numRedoPC   = new JLabel( "numRedoPC"   ) ;
        this.numPigeonPC = new JLabel( "numPigeonPC" ) ;
        this.lTimePC     = new JLabel( "lTimePC"     ) ;
        this.btnSkipPC   = new JLabel( "btnSkipPC"   ) ;
        this.btnSolvedPC = new JLabel( "btnSolvedPC" ) ;
        this.btnRedoPC   = new JLabel( "btnRedoPC"   ) ;
        this.btnPigeonPC = new JLabel( "btnPigeonPC" ) ;
        this.btn1PC      = new JLabel( "btn1PC"      ) ;
        this.btn2PC      = new JLabel( "btn2PC"      ) ;
    }

    private void setUpUI() {

        cPane = getContentPane() ;
        cPane.setLayout( createLayout() ) ;
        fillComponents() ;
        // fillGridWithRandomColors( 6 ) ;

        SwingUtils.centerOnScreen( this, CELL_WIDTH*NUM_COL, CELL_HEIGHT*NUM_ROW ) ;
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ) ;
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

    private void fillComponents() {
        addPanel( typePC,      TYPE_PC ) ;
        addPanel( topicPC,     TOPIC_PC ) ;
        addPanel( bookPC,      BOOK_PC ) ;
        addPanel( problemPC,   PROBLEM_PC ) ;
        addPanel( sTimePC,     S_TIME_PC ) ;
        addPanel( numSkipPC,   NUM_SKIP_PC ) ;
        addPanel( numSolvedPC, NUM_SOLVED_PC ) ;
        addPanel( numRedoPC,   NUM_REDO_PC ) ;
        addPanel( numPigeonPC, NUM_PIGEON_PC ) ;
        addPanel( lTimePC,     L_TIME_PC ) ;
        addPanel( btnSkipPC,   BTN_SKIP_PC ) ;
        addPanel( btnSolvedPC, BTN_SOLVED_PC ) ;
        addPanel( btnRedoPC,   BTN_REDO_PC ) ;
        addPanel( btnPigeonPC, BTN_PIGEON_PC ) ;
        addPanel( btn1PC,      BTN_1_PC ) ;
        addPanel( btn2PC,      BTN_2_PC ) ;
    }

    private void addPanel( JLabel label, String constraints ) {
        
        label.setForeground( Color.WHITE ) ;
        label.setHorizontalAlignment( CENTER );
        label.setVerticalAlignment( CENTER );
        JPanel panel = new JPanel() ;
        panel.setBackground( UIConstant.BG_COLOR ) ;
        panel.setLayout( new BorderLayout() ) ;
        panel.add( label ) ;
        panel.setBorder( new LineBorder( Color.GRAY ) ) ;
        cPane.add( panel, constraints ) ;
    }

    Color getNextColor() {
        Color colors[] = { Color.WHITE, Color.LIGHT_GRAY, Color.GRAY,
                Color.DARK_GRAY, Color.BLACK, Color.RED, Color.PINK,
                Color.ORANGE, Color.YELLOW, Color.GREEN, Color.MAGENTA,
                Color.CYAN, Color.BLUE } ;

        if( colorCounter == colors.length ) colorCounter = 0 ;
        return colors[colorCounter++] ;
    }

    public static void main( String[] args ) {
        SessionControlPanelLayoutPrototype frame = new SessionControlPanelLayoutPrototype() ;
        frame.setVisible( true ) ;
    }
}
