package test;

import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import org.apache.log4j.Logger ;

public class RegExpTester {

    private static final Logger log = Logger.getLogger( RegExpTester.class ) ;
    
    private static final String PATTERN = 
            "[^\\{]@[^\\s]+" ;

    private String processPattern( String input ) 
            throws Exception {
        
        StringBuilder outputBuffer = new StringBuilder() ;
        
        Pattern r = Pattern.compile( PATTERN, Pattern.DOTALL ) ;
        Matcher m = r.matcher( input ) ;
        
        int lastEndMarker = 0 ;
        
        while( m.find() ) {
            int start = m.start() ;
            int end   = m.end() ;
            
            String processedString = processMarker( input.substring( start, end ) ) ;
            if( processedString != null ) {
                outputBuffer.append( input.substring( lastEndMarker, start ) ) ;
                outputBuffer.append( processedString ) ;
                lastEndMarker = end ;
            }
        }
        
        outputBuffer.append( input.substring(lastEndMarker, input.length() ) ) ;
        return outputBuffer.toString() ;
    }
    
    private String processMarker( String input ) {
        log.debug( "Processing marker = " + input ) ;
        return input.substring( 0,1 ) + "\\(" + input.substring( 2 ) + "\\)" ;
    }
        
    public static void main( String[] args ) throws Exception {
        String input = "A mass @m supported by a massless string wound around a uniform hollow cylinder of mass @m and radius @R. If the string does not slip on the cylinder, with what acceleration will the mass fall on release?\n" + 
                "\n" + 
                "{{@img Phy_Ar_6_T3_1.png}}\n" + 
                "" ;
        String output = new RegExpTester().processPattern( input ) ;
        log.debug( output ) ;
    }
}
