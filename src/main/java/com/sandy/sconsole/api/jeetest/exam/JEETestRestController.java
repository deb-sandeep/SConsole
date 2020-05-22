package com.sandy.sconsole.api.jeetest.exam;

import java.sql.Timestamp ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RequestParam ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.common.util.StringUtil ;
import com.sandy.sconsole.core.SConsoleConfig ;
import com.sandy.sconsole.dao.entity.ClickStreamEvent ;
import com.sandy.sconsole.dao.entity.TestAttempt ;
import com.sandy.sconsole.dao.entity.TestAttemptLapSnapshot ;
import com.sandy.sconsole.dao.entity.TestQuestionAttempt ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.repository.ClickStreamEventRepository ;
import com.sandy.sconsole.dao.repository.TestAttemptLapSnapshotRepository ;
import com.sandy.sconsole.dao.repository.TestAttemptRepository ;
import com.sandy.sconsole.dao.repository.TestQuestionAttemptRepository ;
import com.sandy.sconsole.dao.repository.TestQuestionAttemptRepository.IncorrectTestAnswerRC ;
import com.sandy.sconsole.dao.repository.TestQuestionBindingRepository ;
import com.sandy.sconsole.dao.repository.TestQuestionBindingRepository.TopicTestQuestionCount ;
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
    private TestAttemptLapSnapshotRepository talsRepo = null ;
    
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
            if( config.isRecordTestAttempt() ) {
                for( TestQuestionAttempt attempt : attempts ) {
                    tqaRepo.save( attempt ) ; 
                    
                    TestQuestion question = tqRepo.findById( attempt.getTestQuestionId() ).get() ;
                    question.setAttempted( true ) ;
                    tqRepo.save( question ) ;
                }
                log.info( "Test attempts saved" ) ;
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
    
    @PostMapping( "/TestAttempt/LapSnapshot" )
    public ResponseEntity<ResponseMsg> saveTestAttemptLapSnapshot(
            @RequestBody List<TestAttemptLapSnapshot> snapshots ) {
        
        try {
            log.debug( "Saving test attempt lap snapshots." + 
                       getSnapshotsLog( snapshots ) ) ;
            
            if( config.isRecordTestAttempt() ) {
                talsRepo.saveAll( snapshots ) ;
            }
            else {
                log.info( "Application has been configured not to save test lap attemps." ) ;
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
    
    private String getSnapshotsLog( List<TestAttemptLapSnapshot> snapshots ) {
        StringBuilder builder = new StringBuilder( "\n\n" ) ;
        for( TestAttemptLapSnapshot snapshot : snapshots ) {
            builder.append( snapshot.toString() ).append( "\n" ) ;
        }
        builder.append( "\n" ) ;
        return builder.toString() ;
    }
    
    @PostMapping( "/ClickStreamEvent" ) 
    public ResponseEntity<ResponseMsg> saveClickStreamEvent(
            @RequestBody ClickStreamEvent event ) {
        
        try {
            if( config.isRecordTestAttempt() ) {
                event.setCreationTimestamp( new Timestamp( System.currentTimeMillis() ) );
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
            List<TestQuestionAttempt> questionAttempts = null ;
            
            events = cseRepo.findAllByTestAttemptIdOrderById( testAttemptId ) ;
            
            TestAttempt testAttempt = taRepo.findById( testAttemptId ).get() ;
            questions = tqbRepo.getTestQuestionsForTestConfig( testAttempt.getTestConfig().getId() ) ;

            questionAttempts = tqaRepo.findAllByTestAttemptId( testAttemptId ) ;
            
            List<List<? extends Object>> response = new ArrayList<>() ;
            response.add( events ) ;
            response.add( questions ) ;
            response.add( questionAttempts ) ;
            
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

    @GetMapping( "/TestAttempt/LapDetails/{testAttemptId}" )
    public ResponseEntity<List<List<? extends Object>>> getLapDetailsRawData( 
                @PathVariable Integer testAttemptId ) {
        
        try {
            log.debug( "Fetching lap details raw data." ) ;
            
            List<TestQuestion> questions = null ;
            List<TestQuestionAttempt> questionAttempts = null ;
            List<TestAttemptLapSnapshot> lapSnapshots = null ;
            List<ClickStreamEvent> events = null ;
            
            TestAttempt testAttempt = taRepo.findById( testAttemptId ).get() ;
            
            events = cseRepo.findLapEvents( testAttemptId ) ;
            questions = tqbRepo.getTestQuestionsForTestConfig( testAttempt.getTestConfig().getId() ) ;
            questionAttempts = tqaRepo.findAllByTestAttemptId( testAttemptId ) ;
            lapSnapshots = talsRepo.findAllByTestAttemptIdOrderById( testAttemptId ) ;
            
            List<List<? extends Object>> response = new ArrayList<>() ;
            response.add( events ) ;
            response.add( questions ) ;
            response.add( questionAttempts ) ;
            response.add( lapSnapshots ) ;
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( response ) ;
        }
        catch( Exception e ) {
            log.error( "Error fetching test question attempts.", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }

    @GetMapping( "/TestAttempt/TopicWiseTestQuestionErrorDetails" )
    public ResponseEntity<List<TopicWiseTestQuestionErrorDetails>> 
                getTopicWiseTestQuestionErrorDetails( 
                        @RequestParam(name = "timeHorizon") Integer timeHorizon ) {
        
        try {
            log.debug( "Fetching topic wise test question error details." ) ;
            log.debug( "Time horizon = " + timeHorizon ) ;
            
            Date horizonDate = DateUtils.addMonths( new Date(), -1*timeHorizon ) ;
            
            List<TopicTestQuestionCount> topicQCounts ;
            List<IncorrectTestAnswerRC> wrongAnswerRCs ;
            List<TopicWiseTestQuestionErrorDetails> details = null ;
            details = new ArrayList<>() ;
            
            topicQCounts = tqbRepo.getTestQuestionCountPerTopic( horizonDate ) ;
            wrongAnswerRCs = tqaRepo.getIncorrectTestAnswersRC( horizonDate ) ;
            
            populateTopicWiseQuestionErrorDetails( details, topicQCounts, 
                                                   wrongAnswerRCs ) ;
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( details ) ;
        }
        catch( Exception e ) {
            log.error( "Error fetching test question attempts.", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
    
    private void populateTopicWiseQuestionErrorDetails( 
            List<TopicWiseTestQuestionErrorDetails> details,
            List<TopicTestQuestionCount> topicQCounts,
            List<IncorrectTestAnswerRC> wrongAnswerRCs ) {
        
        Map<Integer, TopicWiseTestQuestionErrorDetails> map = new HashMap<>() ;
        TopicWiseTestQuestionErrorDetails detail = null ;
        Map<Integer, String> qRCMap = null ;
        Map<String, List<Integer>> rcClusters = null ;
        
        for( TopicTestQuestionCount ttqc : topicQCounts ) {
            detail = new TopicWiseTestQuestionErrorDetails( ttqc ) ;
            map.put( ttqc.getTopicId(), detail ) ;
            details.add( detail ) ;
        }
        
        for( IncorrectTestAnswerRC rc : wrongAnswerRCs ) {
            
            List<Integer> questionList = null ;
            
            qRCMap = map.get( rc.getTopicId() ).getErrorDetails() ;
            rcClusters = map.get( rc.getTopicId() ).getRcClusters() ;
            
            Integer questionId = rc.getTestQuestionId() ;
            String rootCause = rc.getRootCause() ;
            
            if( StringUtil.isEmptyOrNull( rootCause ) ) {
                rootCause = "-UNASSIGNED-" ;
            }
            
            qRCMap.put( questionId, rc.getRootCause() ) ;
            
            questionList = rcClusters.get( rootCause ) ;
            if( questionList == null ) {
                questionList = new ArrayList<>() ;
                rcClusters.put( rootCause, questionList ) ;
            }
            questionList.add( questionId ) ;
        }
    }
}
