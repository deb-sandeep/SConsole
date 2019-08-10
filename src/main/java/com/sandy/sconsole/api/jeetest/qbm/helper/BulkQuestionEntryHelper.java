package com.sandy.sconsole.api.jeetest.qbm.helper;

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
        public String qRef = null ;
        public boolean isPart = false ;
        public int intId = -1 ;
        public int decimalId = -1 ;
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
        
        Map<String, BulkQEntry> entriesMap = new LinkedHashMap<String, BulkQEntry>() ;
        
        if( files != null ) {
            
            String subPreamble = getSubjectPreamble( subjectName ) ;
            sortFileArray( subPreamble, files ) ;
            
            for( File file : files ) {
                
                FileInfo fi = parseFileInfo( subPreamble, file.getName() ) ;
                BulkQEntry entry = getBulkQEntry( entriesMap, fi ) ;
                
                entry.getImgNames().add( file.getName() ) ;
                entry.getImgPaths().add( file.getAbsolutePath()
                                             .substring( SConsole.JEETEST_IMG_DIR.getAbsolutePath().length()+1 ) ) ;
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
                    
                    int f1IntPart = fi1.intId ;
                    int f2IntPart = fi2.intId ;
                    
                    if( f1IntPart < f2IntPart ) {
                        return -1 ;
                    }
                    else if( f1IntPart > f2IntPart ) {
                        return 1 ;
                    }
                    
                    int f1DecimalId = fi1.decimalId ;
                    int f2DecimalId = fi2.decimalId ;
                    
                    if( f1DecimalId < f2DecimalId ) {
                        return -1 ;
                    }
                    else if( f1DecimalId > f2DecimalId ) {
                        return 1 ;
                    }
                }
                catch( Exception e ) {
                    log.error( "Error in sort comparision", e ) ;
                }
                return 0 ;
            }
        } ) ;
    }
    
    private FileInfo parseFileInfo( String subPreamble, String fileName ) {
        
        String idStr = fileName.substring( fileName.lastIndexOf( "_" ) + 1 ) ;
        idStr = idStr.substring( 0, idStr.lastIndexOf( '.' ) ) ;
        
        String intStr = idStr ;
        String decStr = "-1" ;
        
        if( idStr.contains( "." ) ) {
            intStr = idStr.substring( 0, idStr.indexOf( '.' ) ) ;
            decStr = idStr.substring( idStr.indexOf( '.' ) + 1 ) ;
        }
        
        String qRef = fileName.substring( subPreamble.length() ) ;
        qRef = qRef.substring( 0, qRef.indexOf( '.' ) ) ;
        qRef = qRef.replace( '_', '/' ) ;
        
        FileInfo fi = new FileInfo() ;
        fi.qRef      = qRef ;
        fi.intId     = Integer.parseInt( intStr ) ;
        fi.decimalId = Integer.parseInt( decStr ) ;
        fi.isPart    = !decStr.equals( "-1" ) ;
        
        return fi ;
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
