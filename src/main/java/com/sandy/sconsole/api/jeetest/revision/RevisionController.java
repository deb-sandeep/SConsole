package com.sandy.sconsole.api.jeetest.revision;

import java.util.List ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.RequestParam ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;

@RestController
public class RevisionController {
    
    static final Logger log = Logger.getLogger( RevisionController.class ) ;
    
    @Autowired
    private ProblemAttemptRepository paRepo = null ;
    
    @GetMapping( "/RevisionQuestions" ) 
    public ResponseEntity<List<RevisionQuestion>> getRevisionQuestions(
            @RequestParam( value="subjectName",    required=true ) String subjectName,
            @RequestParam( value="topicId",        required=true ) Integer topicId,
            @RequestParam( value="bookId",         required=true ) Integer bookId,
            @RequestParam( value="ignoreReviewed", required=true ) Boolean ignoreReviewed ) {
        try {
            log.debug( "Getting revision questions." ) ;
            
            List<RevisionQuestion> revisionQuestions = null ;
            
            int revCountThreshold = ( !ignoreReviewed ) ? 999 : 0 ;
            
            if( topicId > 0 && bookId > 0 ) {
                revisionQuestions = paRepo.getRevisionQuestions( revCountThreshold, subjectName, topicId, bookId ) ;
            }
            else if( topicId > 0 && bookId < 0 ) {
                revisionQuestions = paRepo.getRevisionQuestions( revCountThreshold, subjectName, topicId ) ;
            }
            else if( topicId < 0 && bookId > 0 ) {
                revisionQuestions = paRepo.getRevisionQuestionsByBook( revCountThreshold, subjectName, bookId ) ;
            }
            else if( topicId < 0 && bookId < 0 ) {
                revisionQuestions = paRepo.getRevisionQuestions( revCountThreshold, subjectName ) ;
            }
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( revisionQuestions ) ;
        }
        catch( Exception e ) {
            log.error( "Error getting available test attempts.", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
}
