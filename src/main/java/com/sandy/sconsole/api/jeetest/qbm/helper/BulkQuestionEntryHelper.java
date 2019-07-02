package com.sandy.sconsole.api.jeetest.qbm.helper;

import java.io.File ;
import java.io.FileFilter ;
import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.Comparator ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.jeetest.qbm.vo.BulkQEntry ;
import com.sandy.sconsole.api.jeetest.qbm.vo.QBMMasterData ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.TestQuestionRepository ;

public class BulkQuestionEntryHelper {
    
    static final Logger log = Logger.getLogger( BulkQuestionEntryHelper.class ) ;
    
    private TestQuestionRepository tqRepo = null ;
    
    public BulkQuestionEntryHelper( TestQuestionRepository repo ) {
        this.tqRepo = repo ;
    }

    public List<BulkQEntry> findBulkQuestionEntries( String subjectName,
                                                     Topic topic, 
                                                     Book book, 
                                                     String baseQRef ) {
        
        List<String> usedQRefs = getTestQuestionsWithQRefLike( 
                                    topic.getId(), book.getId(), baseQRef ) ;
        
        File[] files = selectUnassignedImages( subjectName, topic, book,
                                               baseQRef, usedQRefs ) ;
        sortFileArray( files ) ;
        
        ArrayList<BulkQEntry> qEntries = new ArrayList<>() ;
        String subPreamble = getSubjectPreamble( subjectName ) ;
        
        for( File file : files ) {
            BulkQEntry entry = new BulkQEntry() ;
            
            String qRef = file.getName().substring( subPreamble.length() ) ;
            qRef = qRef.substring( 0, qRef.lastIndexOf( '.' ) ) ;
            qRef = qRef.replace( '_', '/' ) ;
            
            entry.setqRef( qRef ) ;
            entry.setImgName( file.getName() ) ;
            entry.setImgPath( file.getAbsolutePath()
                                  .substring( SConsole.JEETEST_IMG_DIR
                                                      .getAbsolutePath()
                                                      .length()+1 ) ) ;
            qEntries.add( entry ) ;
        }
        return qEntries ;
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
    
    private void sortFileArray( File[] files ) {
        
        Arrays.sort( files, new Comparator<File>() {
            public int compare( File f1, File f2 ) {
                
                int f1IntId = getIntegerId( f1.getName() ) ;
                int f2IntId = getIntegerId( f2.getName() ) ;
                
                int f1IntPart = (int)Math.floor( f1IntId ) ;
                int f2IntPart = (int)Math.floor( f2IntId ) ;
                
                if( f1IntPart < f2IntPart ) {
                    return -1 ;
                }
                else if( f1IntPart > f2IntPart ) {
                    return 1 ;
                }
                
                int f1DecimalId = getDecimalId( f1.getName() ) ;
                int f2DecimalId = getDecimalId( f2.getName() ) ;
                
                if( f1DecimalId < f2DecimalId ) {
                    return -1 ;
                }
                else if( f1DecimalId > f2DecimalId ) {
                    return 1 ;
                }

                return 0 ;
            }
        } ) ;
    }
    
    private int getIntegerId( String fileName ) {
        String intStr = fileName.substring( fileName.lastIndexOf( "_" ) + 1 ) ;
        intStr = intStr.substring( 0, intStr.indexOf( '.' )  ) ;
        return Integer.parseInt( intStr ) ;
    }
    
    private int getDecimalId( String fileName ) {
        String intStr = fileName.substring( fileName.lastIndexOf( "_" ) + 1 ) ;
        intStr = intStr.substring( intStr.indexOf( '.' )+1  ) ;
        if( intStr.contains( "." ) ) {
            intStr = intStr.substring( 0, intStr.indexOf( "." ) ) ;
            return Integer.parseInt( intStr ) ;
        }
        return 0 ;
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
