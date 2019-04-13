package test;

import com.sandy.sconsole.util.QuestionTextFormatter ;

public class MMTMatrixFmtTest {

    private QuestionTextFormatter fmt = new QuestionTextFormatter() ;
    
    private String testString = 
            "{{@mmt_matrix " +
            "@col1Title Compounds \n" +
            "a. Col1A \n" +
            "b. Col1B \n" +
            "c. Col1C \n" +
            "d. Col1D \n" +
            "\n" + 
            "p. Col2P \n" + 
            "q. Col2Q \n" + 
            "r. Col2R \n" + 
            "s. Col2S \n" + 
            "t. Col2T \n" + 
            "}}" ;
    
    public void test() throws Exception {
        String fmtText = fmt.formatText( testString ) ;
        System.out.println( fmtText ) ;
    }
    
    public static void main( String[] args ) throws Exception {
        new MMTMatrixFmtTest().test() ;
    }
}
