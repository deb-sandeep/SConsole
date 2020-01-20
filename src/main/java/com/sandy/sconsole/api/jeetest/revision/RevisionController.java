package com.sandy.sconsole.api.jeetest.revision;

import java.util.HashMap ;
import java.util.Iterator ;
import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestParam ;
import org.springframework.web.bind.annotation.ResponseBody ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.sconsole.dao.entity.ProblemAttemptAnalysis ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.repository.ProblemAttemptAnalysisRepository ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;
import com.sandy.sconsole.util.ResponseMsg ;

@RestController
public class RevisionController {
    
    static final Logger log = Logger.getLogger( RevisionController.class ) ;
    
    @Autowired
    private ProblemAttemptRepository paRepo = null ;
    
    @Autowired
    private ProblemRepository pRepo = null ;
    
    @Autowired
    private ProblemAttemptAnalysisRepository paaRepo = null ;
    
    @GetMapping( "/RevisionQuestions" ) 
    public ResponseEntity<List<RevisionQuestion>> getRevisionQuestions(
            @RequestParam( name="subjectName",    required=true ) String subjectName,
            @RequestParam( name="topicId",        required=true ) Integer topicId,
            @RequestParam( name="bookId",         required=true ) Integer bookId,
            @RequestParam( name="ignoreReviewed", required=true ) Boolean ignoreReviewed ) {
        
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
            
            filterRevisionQuestions( revisionQuestions, topicId ) ;
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( revisionQuestions ) ;
        }
        catch( Exception e ) {
            log.error( "Error getting available test attempts.", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
    
    private void filterRevisionQuestions( List<RevisionQuestion> questions, Integer topicId ) {
        
        Map<String, ProblemAttemptAnalysis> paaMap = new HashMap<>() ;
        
        Iterator<RevisionQuestion> iter = null ;
        iter = questions.iterator() ;
        while( iter.hasNext() ) {
            RevisionQuestion question = iter.next() ;
            if( question.getOutcome().equals( "Redo" ) || 
                question.getOutcome().equals( "Pigeon" ) || 
                question.getStarred() == 1 ) {
                // do nothing
            }
            else {
                ProblemAttemptAnalysis paa = paaMap.get( question.getProblemType() ) ;
                if( paa == null ) {
                    paa = paaRepo.findByTopicIdAndProblemType( topicId, question.getProblemType() ) ;
                    paaMap.put( question.getProblemType(), paa ) ;
                }
                
                if( question.getDuration() < paa.getEightyPercentile() ) {
                    log.debug( "Removing question because it is less than 80%ile" ) ;
                    iter.remove() ;
                }
            }
        }
    }
    
    @PostMapping( "/ToggleStar" ) 
    @ResponseBody
    public ResponseEntity<ResponseMsg> toggleStarStatus(
            @RequestParam( name="questionId", required=true ) Integer questionId,
            @RequestParam( name="starred"   , required=true ) Integer starStatus ) {
        
        try {
            log.debug( "Toggling star status." ) ;
            
            Problem problem = pRepo.findById( questionId ).get() ;
            problem.setStarred( starStatus == 1 ) ;
            pRepo.save( problem ) ;
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( ResponseMsg.SUCCESS ) ;
        }
        catch( Exception e ) {
            log.error( "Error toggling star status.", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }

    @PostMapping( "/SetRevised" ) 
    @ResponseBody
    public ResponseEntity<ResponseMsg> setRevised(
            @RequestParam( name="questionId", required=true ) Integer questionId ) {
        
        try {
            log.debug( "Setting problem as revised." ) ;
            
            Problem problem = pRepo.findById( questionId ).get() ;
            problem.setRevisionCount( problem.getRevisionCount()+1 ) ;
            pRepo.save( problem ) ;
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( ResponseMsg.SUCCESS ) ;
        }
        catch( Exception e ) {
            log.error( "Error toggling star status.", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
}
