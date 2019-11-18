package test;

import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import org.apache.log4j.Logger ;

public class RegExpTester {

    private static final Logger log = Logger.getLogger( RegExpTester.class ) ;
    
    private static final String PATTERN = 
            "\\$\\{([^\\{]*)}+" ;

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
        return input.substring( 2, input.length()-1 ) ;
    }
    
    public static void main( String[] args ) throws Exception {
        String input = "This is ${hello} world and ${out} there." ;
        String output = new RegExpTester().processPattern( input ) ;
        log.debug( output ) ;
    }
}
