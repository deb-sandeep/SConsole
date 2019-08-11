package com.sandy.sconsole.api.jeetest.qbm.helper;

import java.awt.Point ;
import java.io.File ;
import java.io.FileFilter ;
import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.Comparator ;
import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.jeetest.qbm.vo.BulkQEntry ;
import com.sandy.sconsole.api.jeetest.qbm.vo.QBMMasterData ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.TestQuestionRepository ;

public class BulkQuestionEntryHelper {
    
    public static class FileInfo {
        public String   qRef = null ;
        public Point    qNo = null ;
        public int      partNum = -1 ;
        public String   lctRef = null ;
        public boolean  isLCTQuestion = false ;
        public boolean  isLCTContext = false ;
        public Point    lctNo = null ;
        
        public boolean isLCT() {
            return lctRef != null ;
        }
        
        public boolean isPart() { 
            return partNum != -1 ;
        }
        
        public String toString() {
            StringBuffer buffer = new StringBuffer() ;
            buffer.append( "\nQRef   = " + qRef ).append( "\n" )
                  .append( "QNo    = " + qNo.x + "." + qNo.y ).append( "\n" )
                  .append( "Part # = " + partNum ).append( "\n" )
                  .append( "lctRef = " + lctRef ).append( "\n" )
                  .append( "lct Q? = " + isLCTQuestion ).append( "\n" )
                  .append( "lct C? = " + isLCTContext ).append( "\n" )
                  .append( "lctNo  = " + lctNo ).append( "\n" ) ;
            return buffer.toString() ;
        }
    }
    
    static final Logger log = Logger.getLogger( BulkQuestionEntryHelper.class ) ;
    
    private TestQuestionRepository tqRepo = null ;
    
    public BulkQuestionEntryHelper( TestQuestionRepository repo ) {
        this.tqRepo = repo ;
    }

    public List<BulkQEntry> findBulkQuestionEntries( String subjectName,
                                                     Topic topic, 
                                                     Book book, 
                                                     String baseQRef ) {
        
        ArrayList<BulkQEntry> qEntries = new ArrayList<>() ;
        List<String> usedQRefs = getTestQuestionsWithQRefLike( 
                                    topic.getId(), book.getId(), baseQRef ) ;
        
        File[] files = selectUnassignedImages( subjectName, topic, book,
                                               baseQRef, usedQRefs ) ;
        
        Map<String, BulkQEntry> entriesMap = new LinkedHashMap<>() ;
        Map<String, BulkQEntry> lctContextMap = new LinkedHashMap<>() ;
        
        if( files != null ) {
            
            String subPreamble = getSubjectPreamble( subjectName ) ;
            sortFileArray( subPreamble, files ) ;
            
            for( File file : files ) {
                
                FileInfo fi = parseFileInfo( subPreamble, file.getName() ) ;
                
                if( fi.isLCTContext ) {
                    BulkQEntry lctCtxEntry = lctContextMap.get( fi.lctRef ) ;
                    if( lctCtxEntry == null ) {
                        lctCtxEntry = new BulkQEntry() ;
                        lctContextMap.put( fi.lctRef, lctCtxEntry ) ;
                    }
                    lctCtxEntry.getImgNames().add( file.getName() ) ;
                    lctCtxEntry.getImgPaths().add( file.getAbsolutePath()
                                .substring( SConsole.JEETEST_IMG_DIR
                                                    .getAbsolutePath()
                                                    .length()+1 ) ) ;
                }
                else {
                    BulkQEntry entry = getBulkQEntry( entriesMap, fi ) ;
                    
                    if( fi.isLCTQuestion ) {
                        if( ( fi.isPart() && fi.partNum == 1 ) ||
                              !fi.isPart() ) {
                            
                            BulkQEntry lctCtx = lctContextMap.get( fi.lctRef ) ;
                            for( String imgName : lctCtx.getImgNames() ) {
                                entry.getImgNames().add( imgName ) ;
                            }
                            for( String imgPath : lctCtx.getImgPaths() ) {
                                entry.getImgPaths().add( imgPath ) ;
                            }
                        }
                    }
                    
                    entry.getImgNames().add( file.getName() ) ;
                    entry.getImgPaths().add( file.getAbsolutePath()
                                                 .substring( SConsole.JEETEST_IMG_DIR
                                                                     .getAbsolutePath()
                                                                     .length()+1 ) ) ;
                }
            }
            
            for( BulkQEntry entry : entriesMap.values() ) {
                qEntries.add( entry ) ;
            }
        }
        return qEntries ;
    }
    
    private BulkQEntry getBulkQEntry( Map<String, BulkQEntry> entriesMap, 
                                      FileInfo fi ) {
        
        BulkQEntry entry = entriesMap.get( fi.qRef ) ;
        if( entry == null ) {
            entry = new BulkQEntry( fi ) ;
            entriesMap.put( fi.qRef, entry ) ;
        }
        return entry ;
    }

    private File[] selectUnassignedImages( String subjectName, Topic topic,
                                           Book book, String baseQRef, 
                                           List<String> usedQRefs ) {
       
        File baseDir = new File( SConsole.JEETEST_IMG_DIR, 
                                 subjectName + "/" + 
                                 topic.getTopicName() + "/" + 
                                 book.getBookShortName() ) ;
        
        final String prefix = getSelectionPrefix( subjectName, baseQRef ) ;
        File[] files = baseDir.listFiles( new FileFilter() {
            public boolean accept( File file ) {
                String fName = file.getName() ;
                if( fName.startsWith( prefix ) && fName.endsWith( ".png" ) ) {
                    for( String usedQRef : usedQRefs ) {
                        if( fName.endsWith( usedQRef ) ) {
                            return false ;
                        }
                    }
                    return true ;
                }
                return false ;
            }
        } ) ;
        return files ;
    }
    
    private void sortFileArray( String subPreamble, File[] files ) {
        
        Arrays.sort( files, new Comparator<File>() {
            public int compare( File f1, File f2 ) {
                
                try {
                    FileInfo fi1 = parseFileInfo( subPreamble, f1.getName() ) ;
                    FileInfo fi2 = parseFileInfo( subPreamble, f2.getName() ) ;
                    
                    if( fi1.isLCTContext && !fi2.isLCTContext ) {
                        return -1 ;
                    }
                    else if( !fi1.isLCTContext && fi2.isLCTContext ) {
                        return 1 ;
                    }
                    
                    if( fi1.qNo.equals( fi2.qNo ) ) {
                        return fi1.partNum - fi2.partNum ;
                    }
                    
                    if( fi1.qNo.x == fi2.qNo.x ) {
                        return fi1.qNo.y - fi2.qNo.y ;
                    }
                    else {
                        return fi1.qNo.x - fi2.qNo.x ;
                    }
                }
                catch( Exception e ) {
                    log.error( "Error in sort comparision", e ) ;
                }
                return 0 ;
            }
        } ) ;
    }
    
    public static void main( String[] args ) {
        BulkQuestionEntryHelper obj = new BulkQuestionEntryHelper( null ) ;
        FileInfo fi = obj.parseFileInfo( "Chem_Q_", "Chem_Q_IC_1_LCT_1(2).png" ) ;
        log.debug( fi ) ;
    }
    
    //    <Sub>_Q_[Section]*_[<LCT>]_<QID>(Part).png
    //
    //    Sub : Phy | Chem | Math
    //    Section : [A-Z0-9]+_ // No LCT
    //    LCT : LCT_[QID]
    //    QID : INT_ID.DEC_ID
    //    Part : [0-9]
    private FileInfo parseFileInfo( String subPreamble, String fileName ) {

        // Strip off the filename extension (.png)
        fileName = fileName.substring( 0, fileName.lastIndexOf( '.' ) ) ;
        
        // Strip off the subject preamble
        fileName = fileName.substring( subPreamble.length() ) ;
        
        // Determine if this is a part file
        boolean isPart = fileName.matches( ".+\\([0-9]+\\)" ) ;
        int partNum = -1 ;
        
        // If this is a part file, extract the part number
        if( isPart ) {
            int i1 = fileName.lastIndexOf( '(' ) ;
            int i2 = fileName.lastIndexOf( ')' ) ;
            partNum = Integer.parseInt( fileName.substring( i1+1, i2 ) ) ;
            
            fileName = fileName.substring( 0, i1 ) ;
        }
        
        // Now the last part contains the question number in the x.y format
        String qNoPart = fileName.substring( fileName.lastIndexOf( '_' )+1 ) ;
        Point qNo = parsePoint( qNoPart ) ;
        
        fileName = fileName.substring( 0, fileName.lastIndexOf( '_' ) ) ;
        
        boolean isLCTContext = false ;
        boolean isLCTQuestion = false ;
        String lctRef = null ;
        Point lctNo = null ;
        
        if( fileName.contains( "_LCT" ) ) {
            isLCTContext = fileName.endsWith( "_LCT" ) ;
            isLCTQuestion = !isLCTContext ;
            lctRef = fileName ;
            
            if( isLCTContext ) {
                lctRef = lctRef + "_" + qNoPart ;
            }
            
            lctRef = lctRef.replace( '_', '/' ) ;
            lctNo = parsePoint( lctRef.substring( lctRef.lastIndexOf( '/' ) + 1 ) ) ; 
        }
        
        String qRef = null ;
        qRef = fileName + "_" + qNoPart ;
        qRef = qRef.replace( '_', '/' ) ;
        
        FileInfo fi = new FileInfo() ;
        fi.qRef = qRef ;
        fi.partNum = partNum ;
        fi.lctRef = lctRef ;
        fi.isLCTQuestion = isLCTQuestion ;
        fi.isLCTContext = isLCTContext ;
        fi.qNo = qNo ;
        fi.lctNo = lctNo ;
        
        return fi ;
    }
    
    private Point parsePoint( String input ) {
        
        String intStr = input ;
        String decStr = "-1" ;
        
        if( input.contains( "." ) ) {
            intStr = input.substring( 0, input.indexOf( '.' ) ) ;
            decStr = input.substring( input.indexOf( '.' ) + 1 ) ;
        }
        
        Point point = new Point() ;
        point.x = Integer.parseInt( intStr ) ;
        point.y = Integer.parseInt( decStr ) ;
        return point ;
    }
    
    private List<String> getTestQuestionsWithQRefLike( 
                            Integer topicId, Integer bookId, String qRef ) {
        
        List<TestQuestion> questions = tqRepo.findQuestionsWithQRef( topicId, bookId, qRef ) ;
        List<String> qRefs = new ArrayList<>() ;
        
        for( TestQuestion q : questions ) {
            qRefs.add( q.getQuestionRef().replaceAll( "/", "_" ) + ".png" ) ;
        }
        
        return qRefs ;
    }
    
    private String getSelectionPrefix( String subjectName, String baseQRef ) {
        
        String buffer = getSubjectPreamble( subjectName ) ;
        buffer += baseQRef.replace( '/', '_' ) ;
        return buffer ;
    }
    
    private String getSubjectPreamble( String subjectName ) {
        
        String buffer = null ;
        if( subjectName.equals( QBMMasterData.S_TYPE_PHY ) ) {
            buffer = "Phy_Q_" ;
        }
        else if( subjectName.equals( QBMMasterData.S_TYPE_MATHS ) ) {
            buffer = "Math_Q_" ;
        }
        else {
            buffer = "Chem_Q_" ;
        }
        return buffer ;
    }
}
