package com.sandy.sconsole.util;

import java.util.ArrayList ;
import java.util.List ;
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
    
    private static final String SHORTCUT_IMATH_MARKUP_PATTERN = 
                             "`[^\\s]+" ;

    private static final String SHORTCUT_ICHEM_MARKUP_PATTERN = 
                             "#[^\\s]+" ;

    public String formatText( String input ) 
        throws Exception {
        
        if( input == null ) return null ;
        
        String output = input ;
        output = processShortcutMarkups( SHORTCUT_IMATH_MARKUP_PATTERN, output ) ;
        output = processShortcutMarkups( SHORTCUT_ICHEM_MARKUP_PATTERN, output ) ;
        output = processJoveNotesMarkers( output ) ;
        output = processBlockMathJaxMarkers( output ) ;
        output = processInlineMathJaxMarkers( output ) ;
        
        output = processMarkDown( output ) ;
        output = output.replaceAll( "\\\\\\\\", "\\\\" ) ;
        
        return output ;
    }
    
    private String processShortcutMarkups( String pattern, String input ) {
        StringBuilder outputBuffer = new StringBuilder() ;
        
        Pattern r = Pattern.compile( pattern, Pattern.DOTALL ) ;
        Matcher m = r.matcher( input ) ;
        
        int lastEndMarker = 0 ;
        
        while( m.find() ) {
            int start = m.start() ;
            int end   = m.end() ;
            
            String processedString = "" ;
            
            if( pattern.equals( SHORTCUT_IMATH_MARKUP_PATTERN ) ) {
                processedString = processShortcutIMathMarker( input.substring( start, end ) ) ;
            }
            else if( pattern.equals( SHORTCUT_ICHEM_MARKUP_PATTERN ) ) {
                processedString = processShortcutIChemMarker( input.substring( start, end ) ) ;
            }
            
            if( processedString != null ) {
                outputBuffer.append( input.substring( lastEndMarker, start ) ) ;
                outputBuffer.append( processedString ) ;
                lastEndMarker = end ;
            }
        }
        
        outputBuffer.append( input.substring(lastEndMarker, input.length() ) ) ;
        return outputBuffer.toString() ;
    }
    
    private String processShortcutIMathMarker( String input ) {
        return "\\(" + input.substring( 1 ) + "\\)" ;
    }
    
    private String processShortcutIChemMarker( String input ) {
        return "{{@ichem " + input.substring( 1 ) + "}}" ;
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
            return processImg( data ) ;
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
        else if( type.equals( "opt" ) ) {
            OptTagProcessor processor = new OptTagProcessor( data, this, true ) ;
            return processor.getProcessedText() ;
        }
        else if( type.equals( "optv" ) ) {
            OptTagProcessor processor = new OptTagProcessor( data, this, false ) ;
            return processor.getProcessedText() ;
        }
        
        return null ;
    }
    
    public String processImg( String imgName ) 
        throws Exception {
        return "<img src='/jeetest/images/" + imgName + "'/>" ;
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
    
    public List<String> getEmbeddedImageNames( String input ) {
        
        Pattern r = Pattern.compile( JN_MARKER_PATTERN, Pattern.DOTALL ) ;
        Matcher m = r.matcher( input ) ;
        
        List<String> embeddedImageNames = new ArrayList<>() ;
        
        while( m.find() ) {
            String markerType = m.group( 1 ) ;
            String markerData = m.group( 2 ) ;
            
            if( markerType.equals( "img" ) ) {
                embeddedImageNames.add( markerData ) ;
            }
        }
        return embeddedImageNames ;
    }
}