package com.sandy.sconsole.api.jeetest.exam;

import java.sql.Timestamp ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.sconsole.core.SConsoleConfig ;
import com.sandy.sconsole.dao.entity.ClickStreamEvent ;
import com.sandy.sconsole.dao.entity.TestAttempt ;
import com.sandy.sconsole.dao.entity.TestQuestionAttempt ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.repository.ClickStreamEventRepository ;
import com.sandy.sconsole.dao.repository.TestAttemptRepository ;
import com.sandy.sconsole.dao.repository.TestQuestionAttemptRepository ;
import com.sandy.sconsole.dao.repository.TestQuestionBindingRepository ;
import com.sandy.sconsole.dao.repository.master.TestQuestionRepository ;
import com.sandy.sconsole.util.ResponseMsg ;

@RestController
public class JEETestRestController {
    
    static final Logger log = Logger.getLogger( JEETestRestController.class ) ;
    
    @Autowired
    private TestAttemptRepository taRepo = null ;
    
    @Autowired
    private TestQuestionAttemptRepository tqaRepo = null ;
    
    @Autowired
    private ClickStreamEventRepository cseRepo = null ;
    
    @Autowired
    private TestQuestionRepository tqRepo = null ;
    
    @Autowired
    private TestQuestionBindingRepository tqbRepo = null ;
    
    @Autowired
    private SConsoleConfig config = null ;
    
    @GetMapping( "/TestAttempt" ) 
    public ResponseEntity<List<TestAttempt>> getTestAttempts() {
        try {
            log.debug( "Getting available test attempts." ) ;
            
            List<TestAttempt> testAttempts = taRepo.findAllByOrderByDateAttemptedDesc() ;
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( testAttempts ) ;
        }
        catch( Exception e ) {
            log.error( "Error getting available test attempts.", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
    
    @PostMapping( "/TestAttempt" ) 
    public ResponseEntity<TestAttempt> saveTestAttempt(
                                    @RequestBody TestAttempt attempt ) {
        try {
            log.debug( "Saving a test attempt." ) ;
            
            if( attempt.getDateAttempted() == null ) {
                attempt.setDateAttempted( new Timestamp( new Date().getTime() ) );
            }
            
            TestAttempt savedAttempt = null ;
            
            if( config.isRecordTestAttempt() ) {
                savedAttempt = taRepo.save( attempt ) ;
            }
            else {
                savedAttempt = attempt ;
                savedAttempt.setId( 0xBABE ) ;
                log.info( "Application has been configured not to save test attempts." ) ;
            }
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( savedAttempt ) ;
        }
        catch( Exception e ) {
            log.error( "Error saving test configuration", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
    
    @PostMapping( "/TestAttempt/TestQuestionAttempts" )
    public ResponseEntity<ResponseMsg> saveTestQuestionAttempts(
            @RequestBody List<TestQuestionAttempt> attempts ) {
        
        try {
            log.debug( "Saving test attempts." ) ;
            
            if( config.isRecordTestAttempt() ) {
                for( TestQuestionAttempt attempt : attempts ) {
                    log.debug( "Saving attempt for quesiton - " + attempt.getTestQuestionId() ) ;
                    tqaRepo.save( attempt ) ; 
                    
                    TestQuestion question = tqRepo.findById( attempt.getTestQuestionId() ).get() ;
                    if( attempt.getTimeSpent() > 0 ) {
                        question.setAttempted( true ) ;
                        tqRepo.save( question ) ;
                    }
                }
            }
            else {
                log.info( "Application has been configured not to save test attempts." ) ;
            }
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( ResponseMsg.SUCCESS ) ;
        }
        catch( Exception e ) {
            log.error( "Error saving test configuration", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
    
    @PostMapping( "/ClickStreamEvent" ) 
    public ResponseEntity<ResponseMsg> saveClickStreamEvent(
            @RequestBody ClickStreamEvent event ) {
        
        try {
            log.debug( "Saving click stream event." ) ;
            if( config.isRecordTestAttempt() ) {
                cseRepo.save( event ) ;
            }
            else {
                log.info( "Application has been configured not to save test attempts." ) ;
            }
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( ResponseMsg.SUCCESS ) ;
        }
        catch( Exception e ) {
            log.error( "Error saving test configuration", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
    
    @GetMapping( "/TestQuestionAttempt/TestAttempt/{testAttemptId}" )
    public ResponseEntity<List<List<? extends Object>>> getTestQuestionAttempts( 
                @PathVariable Integer testAttemptId ) {
        
        try {
            log.debug( "Fetching test quesiton attempts." ) ;
            List<TestQuestionAttempt> questionAttempts = null ;
            List<TestQuestion> questions = null ;
            
            questionAttempts = tqaRepo.findAllByTestAttemptId( testAttemptId ) ;
            
            TestAttempt testAttempt = taRepo.findById( testAttemptId ).get() ;
            questions = tqbRepo.getTestQuestionsForTestConfig( testAttempt.getTestConfig().getId() ) ;
            
            List<List<? extends Object>> response = new ArrayList<>() ;
            response.add( questionAttempts ) ;
            response.add( questions ) ;
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( response ) ;
        }
        catch( Exception e ) {
            log.error( "Error fetching test question attempts.", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }

    @GetMapping( "/ClickStreamEvent/TestAttempt/{testAttemptId}" )
    public ResponseEntity<List<List<? extends Object>>> getClickStreamEventsForTestAttempt( 
                @PathVariable Integer testAttemptId ) {
        
        try {
            log.debug( "Fetching click stream events." ) ;
            List<TestQuestion> questions = null ;
            List<ClickStreamEvent> events = null ;
            
            events = cseRepo.findAllByTestAttemptIdOrderById( testAttemptId ) ;
            
            TestAttempt testAttempt = taRepo.findById( testAttemptId ).get() ;
            questions = tqbRepo.getTestQuestionsForTestConfig( testAttempt.getTestConfig().getId() ) ;
            
            List<List<? extends Object>> response = new ArrayList<>() ;
            response.add( events ) ;
            response.add( questions ) ;
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( response ) ;
        }
        catch( Exception e ) {
            log.error( "Error fetching test question attempts.", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }

    @PostMapping( "/TestAttempt/UpdateGraceScore" )
    public ResponseEntity<ResponseMsg> updateGraceScore(
            @RequestBody GraceScoreInput graceScoreInput ) {
        
        try {
            log.debug( "Updating grace score." ) ;
            
            TestAttempt testAttempt = taRepo.findById( graceScoreInput.getTestAttemptId() ).get() ;
            TestQuestionAttempt tqAttempt = tqaRepo.findByTestAttemptIdAndTestQuestionId( 
                                                    graceScoreInput.getTestAttemptId(), 
                                                    graceScoreInput.getTestQuestionId() ) ;
            
            String preGraceAttemptState = graceScoreInput.getPreGraceAttemptState() ;

            tqAttempt.setAttemptStatus( graceScoreInput.getPostGraceAttemptState() ) ;
            tqAttempt.setScore( graceScoreInput.getPostGraceScore() ) ;
            if( tqAttempt.getScore() <= 0 ) {
                tqAttempt.setIsCorrect( Boolean.FALSE ) ;
                if( preGraceAttemptState.equals( "q-ans-and-marked-for-review" ) || 
                    preGraceAttemptState.equals( "q-attempted" ) ) {
                    testAttempt.setNumCorrectAnswers( testAttempt.getNumCorrectAnswers()-1 ) ;
                }
            }
            else {
                tqAttempt.setIsCorrect( Boolean.TRUE ) ;
                if( preGraceAttemptState.equals( "q-ans-and-marked-for-review" ) || 
                    preGraceAttemptState.equals( "q-attempted" ) ) {
                    testAttempt.setNumWrongAnswers( testAttempt.getNumWrongAnswers()-1 ) ;
                }
                testAttempt.setNumCorrectAnswers( testAttempt.getNumCorrectAnswers()+1 ) ;
            }
            
            testAttempt.setScore( testAttempt.getScore() + -1*graceScoreInput.getPreGraceScore() ) ;
            testAttempt.setScore( testAttempt.getScore() + graceScoreInput.getPostGraceScore() ) ;
            
            if( preGraceAttemptState.equals( "q-not-visited" ) ) {
                testAttempt.setNumNotVisited( testAttempt.getNumNotVisited()-1 ) ;
            }
            else if( preGraceAttemptState.equals( "q-not-answered" ) ) {
                testAttempt.setNumNotAnswered( testAttempt.getNumNotAnswered()-1 ) ;
            }
            else if( preGraceAttemptState.equals( "q-attempted" ) ) {
                testAttempt.setNumAttempted( testAttempt.getNumAttempted()-1 ) ;
            }
            else if( preGraceAttemptState.equals( "q-marked-for-review" ) ) {
                testAttempt.setNumMarkedForReview( testAttempt.getNumMarkedForReview()-1 ) ;
            }
            else if( preGraceAttemptState.equals( "q-ans-and-marked-for-review" ) ) {
                testAttempt.setNumAnsAndMarkedForReview( testAttempt.getNumAnsAndMarkedForReview()-1 ) ;
            }
            
            testAttempt.setNumAttempted( testAttempt.getNumAttempted()+1 ) ;

            tqaRepo.save( tqAttempt ) ;
            taRepo.save( testAttempt ) ;
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( ResponseMsg.SUCCESS ) ;
        }
        catch( Exception e ) {
            log.error( "Error saving grace input", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
    
    @PostMapping( "/TestAttempt/UpdateRootCause" ) 
    public ResponseEntity<ResponseMsg> updateRootCause( @RequestBody RootCauseInput rcInput ) {
        
        try {
            log.debug( "Updating root cause." ) ;
            
            TestQuestionAttempt tqAttempt = null ;
            tqAttempt = tqaRepo.findByTestAttemptIdAndTestQuestionId( 
                                            rcInput.getTestAttemptId(), 
                                            rcInput.getTestQuestionId() ) ;
            tqAttempt.setRootCause( rcInput.getRootCause() ) ;
            tqaRepo.save( tqAttempt ) ;
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( ResponseMsg.SUCCESS ) ;
        }
        catch( Exception e ) {
            log.error( "Error updating root cause.", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
}
