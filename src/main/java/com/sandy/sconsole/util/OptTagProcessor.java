package com.sandy.sconsole.util;

import java.io.BufferedReader ;
import java.io.StringReader ;
import java.util.ArrayList ;

import org.apache.log4j.Logger ;

public class OptTagProcessor {

    static final Logger log = Logger.getLogger( OptTagProcessor.class ) ;
    private static final String OPT_TABLE_V_TAG = "<table class='option-table-v'>" ;
    private static final String OPT_TABLE_H_TAG = "<table class='option-table-h'>" ;
    
    private String                markupData    = null ;
    private QuestionTextFormatter textProcessor = null ;
    private boolean               hTable        = true ;
    
    private ArrayList<String> tdContents  = new ArrayList<>() ;
    
    public OptTagProcessor( String markupData, QuestionTextFormatter textProcessor,
                            boolean horizontalTable ) {
        this.markupData    = markupData ;
        this.textProcessor = textProcessor ;
        this.hTable        = horizontalTable ;
    }
    
    public String getProcessedText() throws Exception {
        parseMarkupData() ;
        String tableTransform = null ; 
        if( this.hTable ) {
            tableTransform = transformIntoHTable() ;
        }
        else {
            tableTransform = transformIntoVTable() ;
        }
        
        return this.textProcessor.formatText( tableTransform ) ;
    }
    
    private void parseMarkupData() throws Exception {
        
        BufferedReader br = new BufferedReader( new StringReader( this.markupData ) ) ;
        String line = null ;
        while( ( line = br.readLine() ) != null ) {
            
            line = line.trim() ;
            
            if( StringUtil.isEmptyOrNull( line ) ) {
                continue ;
            }
            else if( line.matches( "^[0-9]\\. .*$" ) ) {
                String content = line.substring( 2 ).trim() ;
                tdContents.add( "{{@math " + content + " }}" ) ;
            }
        }
    }
    
    private String transformIntoHTable() {
        
        StringBuffer buffer = new StringBuffer( OPT_TABLE_H_TAG ) ;
        buffer.append( "<thead>" )
              .append( "<tr>" )
              .append( "<th>1.</th>" )
              .append( "<th>2.</th>" ) 
              .append( "<th>3.</th>" ) 
              .append( "<th>4.</th>" )
              .append( "</tr>" )
              .append( "</thead>" ) ;
        
        buffer.append( "<tbody>" )
              .append( "<tr>" ) ;
        
        for( String td : tdContents ) {
            buffer.append( "<td><div>" + td + "</div></td>" ) ;
        }
        
        buffer.append( "</tr>" )
              .append( "</tbody>" )
              .append( "</table>" ) ;
        
        return buffer.toString() ;
    }

    private String transformIntoVTable() {
        
        StringBuffer buffer = new StringBuffer( OPT_TABLE_V_TAG ) ;
        
        buffer.append( "<tbody>" ) ;
        for( int i=0; i<tdContents.size(); i++ ) {
            String td = tdContents.get( i ) ;
            buffer.append( "<tr>" ) ;
            buffer.append( "<th width='50px'>" + (i+1) + ".</th>" ) ;
            buffer.append( "<td><div>" + td + "</div></td>" ) ;
            buffer.append( "</tr>" ) ;
         }
        buffer.append( "</tbody>" )
              .append( "</table>" ) ;
        
        return buffer.toString() ;
    }
}
