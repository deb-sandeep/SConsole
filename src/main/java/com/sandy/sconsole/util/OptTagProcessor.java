package com.sandy.sconsole.util;

import java.io.BufferedReader ;
import java.io.StringReader ;
import java.util.ArrayList ;

import org.apache.log4j.Logger ;

public class OptTagProcessor {

    private static final Logger log = Logger.getLogger( OptTagProcessor.class ) ;
    
    private String                markupData    = null ;
    private QuestionTextFormatter textProcessor = null ;
    
    private ArrayList<String> tdContents  = new ArrayList<>() ;
    
    public OptTagProcessor( String markupData, QuestionTextFormatter textProcessor ) {
        this.markupData = markupData ;
        this.textProcessor = textProcessor ;
    }
    
    public String getProcessedText() throws Exception {
        parseMarkupData() ;
        String tableTransform = transformIntoTableMeta() ;
        return this.textProcessor.formatText( tableTransform ) ;
    }
    
    private void parseMarkupData() throws Exception {
        
        BufferedReader br = new BufferedReader( new StringReader( this.markupData ) ) ;
        String line = null ;
        while( ( line = br.readLine() ) != null ) {
            
            line = line.trim() ;
            log.debug( "> " + line ) ;
            
            if( StringUtil.isEmptyOrNull( line ) ) {
                continue ;
            }
            else if( line.matches( "^[0-9]\\. .*$" ) ) {
                String content = line.substring( 2 ).trim() ;
                tdContents.add( "[[@math " + content + " ]]" ) ;
            }
        }
    }
    
    private String transformIntoTableMeta() {
        
        StringBuffer buffer = new StringBuffer( "{{@table \n" ) ;
        
        buffer.append( "@th 1\n" )
              .append( "@th 2\n" )
              .append( "@th 3\n" )
              .append( "@th 4\n\n" ) ;
        
        for( String td : tdContents ) {
            buffer.append( "@td " + td + "\n" ) ;
        }
        
        buffer.append( "}}\n" ) ;
        return buffer.toString() ;
    }
}
