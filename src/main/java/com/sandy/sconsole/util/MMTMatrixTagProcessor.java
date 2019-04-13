package com.sandy.sconsole.util;

import java.io.BufferedReader ;
import java.io.StringReader ;
import java.util.ArrayList ;

import org.apache.log4j.Logger ;

public class MMTMatrixTagProcessor {

    private static final Logger log = Logger.getLogger( MMTMatrixTagProcessor.class ) ;
    
    private class ColCell {
        public String id = null ;
        public String content = null ;
        
        ColCell( String id, String content ) {
            this.id = id ;
            this.content = content ;
        }
    }
    
    private static String COL1_HDR_TAG = "@col1Title" ;
    private static String COL2_HDR_TAG = "@col2Title" ;
    
    private enum ProcessingState { IDLE, COL1, COL2 } ;
    
    private ProcessingState       procState     = ProcessingState.IDLE ;
    private String                markupData    = null ;
    private QuestionTextFormatter textProcessor = null ;
    
    private String             col1Header = "" ;
    private String             col2Header = "" ;
    private ArrayList<ColCell> col1Items  = new ArrayList<>() ;
    private ArrayList<ColCell> col2Items  = new ArrayList<>() ;
    
    public MMTMatrixTagProcessor( String markupData, QuestionTextFormatter textProcessor ) {
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
            else if( line.startsWith( COL1_HDR_TAG ) ) {
                col1Header = line.substring( COL1_HDR_TAG.length() ).trim() ;
                procState = ProcessingState.COL1 ;
            }
            else if( line.startsWith( COL2_HDR_TAG ) ) {
                col2Header = line.substring( COL2_HDR_TAG.length() ).trim() ;
                procState = ProcessingState.COL2 ;
            }
            else if( line.matches( "^[a-z]\\. .*$" ) ) {
                String id = line.substring( 0, 1 ) ;
                String content = line.substring( 2 ).trim() ;
                
                log.debug( "Procesing state " + procState );
                
                if( procState == ProcessingState.COL1 ) {
                    col1Items.add( new ColCell( id, content ) ) ;
                }
                else if( procState == ProcessingState.COL2 ) {
                    col2Items.add( new ColCell( id, content ) ) ;
                }
                else {
                    throw new Exception( "Column content found without a header." ) ;
                }
            }
        }
    }
    
    private String transformIntoTableMeta() {
        
        StringBuffer buffer = new StringBuffer( "{{@table \n" ) ;
        
        buffer.append( "@th \n" )
              .append( "@th " + col1Header + "\n" )
              .append( "@th \n" )
              .append( "@th " + col2Header + "\n" ) ;
        
        int numRows = Math.max( col1Items.size(), col2Items.size() ) ;
        for( int i=0; i<numRows; i++ ) {
            
            ColCell colCell = null ;
            
            if( i < col1Items.size() ) {
                colCell = col1Items.get( i ) ;
            }
            appendCellTD( colCell, buffer ) ;
            
            colCell = null ;
            if( i < col2Items.size() ) {
                colCell = col2Items.get( i ) ;
            }
            appendCellTD( colCell, buffer ) ;
        }
        
        buffer.append( "}}\n" ) ;
        return buffer.toString() ;
    }
        
    private void appendCellTD( ColCell cell, StringBuffer buffer ) {

        if( cell == null ) {
            buffer.append( "@td \n" ) ;
            buffer.append( "@td \n" ) ;
        }
        else {
            buffer.append( "@td " + cell.id + "\n" ) ;
            buffer.append( "@td " + cell.content + "\n" ) ;
        }
    }
}
