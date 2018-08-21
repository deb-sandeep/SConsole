package test;

import java.awt.Color ;
import java.awt.Container ;

import javax.swing.JFrame ;
import javax.swing.JPanel ;
import javax.swing.border.LineBorder ;

import com.sandy.common.ui.SwingUtils ;
import com.sandy.sconsole.SConsole ;

import info.clearthought.layout.TableLayout ;

@SuppressWarnings( "serial" )
public class TableLayoutTest extends JFrame {
    
    private static final String TIME_PC            = "0,0,2,0" ;
    private static final String TITLE_PC           = "3,0,6,0" ;
    private static final String DATE_PC            = "7,0,9,0" ;
    
    private static final String GANTT_PC           = "0,1,9,1" ;
    
    private static final String SESSION_STAT_PC    = "0,2,1,5" ;
    private static final String SESSION_CONTROL_PC = "2,2,7,5" ;
    private static final String DAY_STAT_PC        = "8,0,9,5" ;
    
    private static final String BURN_PC            = "0,6,4,9" ;
    private static final String DAY_TOTAL_PC       = "5,6,6,7" ;
    private static final String DAY_RELATIVE_PC    = "7,6,9,7" ;
    private static final String LAST_30D_PC        = "5,8,9,9" ;
    
    private int colorCounter = 0 ;
    private Container cPane = null ;
    
    public TableLayoutTest() {
        super( "Table Layout Test" ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        cPane = getContentPane() ;
        cPane.setLayout( createLayout() ) ;
        fillComponents() ;
//        fillGridWithRandomColors( 6 ) ;
        
        SwingUtils.centerOnScreen( this, 1000, (int)((9.0/16.0)*1000) ) ;
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ) ;
    }
    
    private TableLayout createLayout() {
        double[] colSizes = new double[10] ;
        double[] rowSizes = new double[10] ;
        
        for( int i=0; i<10; i++ ) {
            colSizes[i] = rowSizes[i] = 0.1D ;
        }

        return new TableLayout( colSizes, rowSizes ) ;
    }
    
    private void fillComponents() {
        fillHeaderRow() ;
        fillDayGanttRow() ;
        fillStatRow() ;
        fillBurnAndStatRow() ;
    }
    
    private void fillHeaderRow() {
        addPanel( TIME_PC ) ;
        addPanel( TITLE_PC ) ;
        addPanel( DATE_PC ) ;
    }
    
    private void fillDayGanttRow() {
        addPanel( GANTT_PC ) ;
    }
    
    private void fillStatRow() {
        addPanel( SESSION_STAT_PC ) ;
        addPanel( SESSION_CONTROL_PC ) ;
        addPanel( DAY_STAT_PC ) ;
    }
    
    private void fillBurnAndStatRow() {
        addPanel( BURN_PC ) ;
        addPanel( DAY_TOTAL_PC ) ;
        addPanel( DAY_RELATIVE_PC ) ;
        addPanel( LAST_30D_PC ) ;
    }
    
    void fillGridWithRandomColors( int startRow ) {
        
        for( int r = startRow ; r < 10 ; r++ ) {
            for( int c = 0 ; c < 10 ; c++ ) {
                addPanel( c + "," + r ) ;
            }
        }
    }
    
    private void addPanel( String constraints ) {
        JPanel panel = new JPanel() ;
        panel.setBackground( SConsole.BG_COLOR ) ;
        panel.setBorder( new LineBorder( Color.DARK_GRAY.darker().darker() ) );
        cPane.add( panel, constraints ) ;
    }
    
    Color getNextColor() {
        Color colors[] = { Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, 
                            Color.DARK_GRAY, Color.BLACK, Color.RED, Color.PINK, 
                            Color.ORANGE, Color.YELLOW, Color.GREEN,
                            Color.MAGENTA, Color.CYAN, Color.BLUE } ;
        
        if( colorCounter == colors.length ) colorCounter = 0 ;
        return colors[colorCounter++] ;
    }
    
    public static void main( String[] args ) {
        TableLayoutTest frame = new TableLayoutTest() ;
        frame.setVisible( true ) ;
    }
}
