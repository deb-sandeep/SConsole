package com.sandy.sconsole.util;

import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import org.apache.log4j.Logger ;
import org.pegdown.Extensions ;
import org.pegdown.PegDownProcessor ;

public class QuestionTextFormatter {

    static final Logger log = Logger.getLogger( QuestionTextFormatter.class ) ;
    
    private static PegDownProcessor pdProcessor   = 
                new PegDownProcessor( Extensions.ALL & 
                                     ~Extensions.HARDWRAPS & 
                                     ~Extensions.ANCHORLINKS ) ;
    
    private static final String JN_MARKER_PATTERN = 
                             "\\{\\{@([a-zA-Z0-9_]*)\\s+((.(?!\\{\\{))*)\\}\\}" ;
    
    private static final String MJ_BLOCK_MARKER_PATTERN = 
                             "\\$\\$.*?\\$\\$" ;
    
    private static final String MJ_INLINE_MARKER_PATTERN = 
                             "\\\\\\(.*?\\\\\\)" ;
    
    public String formatText( String input ) 
        throws Exception {
        
        if( input == null ) return null ;
        
        String output = null ;
        output = processJoveNotesMarkers( input ) ;
        output = processBlockMathJaxMarkers( output ) ;
        output = processInlineMathJaxMarkers( output ) ;
        
        output = processMarkDown( output ) ;
        
        // Let's piggy back on bootstrap formatting of tables.
        String customTableTag = "<table class=\"pure-table pure-table-horizontal\">" ;
        output = output.replaceAll( "<table>", customTableTag ) ;
        output = output.replaceAll( "\\\\\\\\", "\\\\" ) ;
        
        return output ;
    }
    
    private String processMarkDown( String input ) {
        String output = pdProcessor.markdownToHtml( input ) ;
        if( output.startsWith( "<p>" ) && output.endsWith( "</p>" ) ) {
            output = output.substring( 3, output.length()-4 ) ;
        }
        return output ;
    }
    
    private String processJoveNotesMarkers( String input ) 
        throws Exception {
        
        StringBuilder outputBuffer = new StringBuilder() ;
        
        Pattern r = Pattern.compile( JN_MARKER_PATTERN, Pattern.DOTALL ) ;
        Matcher m = r.matcher( input ) ;
        
        int lastEndMarker = 0 ;
        
        while( m.find() ) {
            int start = m.start() ;
            int end   = m.end() ;
            
            String markerType = m.group( 1 ) ;
            String markerData = m.group( 2 ) ;
            
            String processedString = processMarker( markerType, markerData ) ;
            if( processedString != null ) {
                outputBuffer.append( input.substring( lastEndMarker, start ) ) ;
                outputBuffer.append( processedString ) ;
                lastEndMarker = end ;
            }
        }
        outputBuffer.append( input.substring(lastEndMarker, input.length() ) ) ;
        return outputBuffer.toString() ;
    }
    
    private String processBlockMathJaxMarkers( String input ) 
            throws Exception {
        
        StringBuilder outputBuffer = new StringBuilder() ;
        
        Pattern r = Pattern.compile( MJ_BLOCK_MARKER_PATTERN ) ;
        Matcher m = r.matcher( input ) ;
        
        int lastEndMarker = 0 ;
        
        while( m.find() ) {
            int start = m.start() ;
            int end   = m.end() ;
            
            String markerData = m.group( 0 ) ;
            String processedString = markerData.replace( "\\", "\\\\" ) ;

            outputBuffer.append( input.substring( lastEndMarker, start ) ) ;
            outputBuffer.append( processedString ) ;
            
            lastEndMarker = end ;
        }
        outputBuffer.append( input.substring(lastEndMarker, input.length() ) ) ;
        return outputBuffer.toString() ;
    }
    
    private String processInlineMathJaxMarkers( String input ) 
            throws Exception {
        
        StringBuilder outputBuffer = new StringBuilder() ;
        
        Pattern r = Pattern.compile( MJ_INLINE_MARKER_PATTERN ) ;
        Matcher m = r.matcher( input ) ;
        
        int lastEndMarker = 0 ;
        
        while( m.find() ) {
            int start = m.start() ;
            int end   = m.end() ;
            
            String markerData = m.group( 0 ) ;
            String processedString = markerData.replace( "\\", "\\\\" ) ;
            
            outputBuffer.append( input.substring( lastEndMarker, start ) ) ;
            outputBuffer.append( processedString ) ;
            
            lastEndMarker = end ;
        }
        outputBuffer.append( input.substring(lastEndMarker, input.length() ) ) ;
        return outputBuffer.toString() ;
    }
    
    private String processMarker( String type, String data ) 
        throws Exception {
        
        if( type.equals( "img" ) ) {
            processImg( data ) ;
            return null ;
        }
        else if( type.equals( "table" ) ) {
            TableTagProcessor processor = new TableTagProcessor( data, this ) ;
            return processor.getProcessedText() ;
        }
        else if( type.equals( "ichem" ) ) {
            return "\\( \\ce{" + data + "} \\)" ;
        }
        else if( type.equals( "imath" ) ) {
            return "\\( " + data + " \\)" ;
        }
        else if( type.equals( "chem" ) ) {
            return "$$ \\ce{" + data + "} $$" ;
        }
        else if( type.equals( "math" ) ) {
            return processMathTagContents( data ) ;
        }
        else if( type.equals( "red" ) ) {
            return "<span class='red'>" + processMarkDown( data ) + "</span>" ;
        }
        else if( type.equals( "green" ) ) {
            return "<span class='green'>" + processMarkDown( data ) + "</span>" ;
        }
        else if( type.equals( "blue" ) ) {
            return "<span class='blue'>" + processMarkDown( data ) + "</span>" ;
        }
        else if( type.equals( "mmt_matrix" ) ) {
            MMTMatrixTagProcessor processor = new MMTMatrixTagProcessor( data, this ) ;
            return processor.getProcessedText() ;
        }
        
        return null ;
    }
    
    public void processImg( String imgName ) 
        throws Exception {

        // There is nothing much to do here. The img tag will be rendered
        // on the browser. We might, however want to check if the specified
        // image exists in the folder. This needs to be thought through..
        // For now, keeping the function body as empty.
    }

    private String processMathTagContents( String content ) {
        String[] lines = content.split( "\n" ) ;
        StringBuffer buffer = new StringBuffer() ;
        if( lines.length > 1 ) {
            for( String line : lines ) {
                buffer.append( "\\( " + line + " \\)\n\n" ) ;
            }
        }
        else {
            buffer.append( "$$ " + content + " $$" ) ;
        }
        return buffer.toString() ;
    }
    
    public String createAttributeString( String[][] attributes ) {
        StringBuilder buffer = new StringBuilder() ;
        
        if( attributes != null && attributes.length > 0 ) {
            for( String[] attr : attributes ) {
                buffer.append( " " ) ;
                buffer.append( attr[0] ) ;
                if( attr[1] != null ) {
                    buffer.append( "=\"" ).append( attr[1] ).append( "\"" ) ; 
                }
            }
            buffer.append( " " ) ;
        }
        
        return buffer.toString() ;
    }
}