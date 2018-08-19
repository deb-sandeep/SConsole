package com.sandy.sconsole.api.mdm.problem;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.common.util.StringUtil ;
import com.sandy.sconsole.api.mdm.MDMInput ;
import com.sandy.sconsole.core.api.APIResponse ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.BookRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;

@RestController
public class ProblemMasterController {
    
    private static final Logger log = Logger.getLogger( ProblemMasterController.class ) ;
    
    @Autowired
    private ProblemRepository problemRepo = null ;
    
    @Autowired
    private BookRepository bookRepo = null ;
    
    @Autowired
    private TopicRepository topicRepo = null ;

    @PostMapping( "/ProblemMaster" )
    public ResponseEntity<APIResponse> updateProblemMasterList( 
                                                 @RequestBody MDMInput input ) {
        
        String inputPayload = input.getInputPayload() ;
        if( StringUtil.isEmptyOrNull( inputPayload ) ) {
            return ResponseEntity.badRequest()
                                 .body( new APIResponse( "MDM payload missing" ) ) ;
        }
        
        APIResponse response = null ;
        try {
            response = processInputPayload( inputPayload ) ;
        }
        catch( Exception e ) {
            log.error( "Exception !", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( new APIResponse( e.getMessage() ) ) ;
        }
        
        return ResponseEntity.ok().body( response ) ;
    }
    
    private APIResponse processInputPayload( String input ) 
        throws Exception {
        
        String lines[] = input.split("\\r?\\n") ;
        List<ExerciseMeta> exMetaList = generateExerciseMeta( lines ) ;
        
        if( exMetaList.isEmpty() ) {
            return new APIResponse( "No exercise to process" ) ;
        }
        
        for( ExerciseMeta exMeta : exMetaList ) {
            
            if( problemRepo.getExMetaProcessCount( exMeta.toString() ) == 0 ) {
                log.debug( "Processing meta exercise " + exMeta ) ;
                processMetaExercise( exMeta ) ;
                problemRepo.saveMetaExercise( exMeta.toString() ) ;
            }
        }
        
        return new APIResponse( "Success" ) ;
    }
    
    public List<ExerciseMeta> generateExerciseMeta( String[] inputLines ) {
        
        List<ExerciseMeta> exMetaList = new ArrayList<>() ;
        for( String input : inputLines ) {
            ExerciseMeta exMeta = new ExerciseMeta( input ) ;
            if( exMeta.getTotalNumProblems() > 0 ) {
                exMetaList.add( exMeta ) ;
            }
        }
        return exMetaList ;
    }
    
    private void processMetaExercise( ExerciseMeta ex ) 
        throws Exception {
        
        Topic topic = topicRepo.findBySubjectNameAndTopicName( ex.subjectName,
                                                               ex.topic ) ;
        Book book = bookRepo.findByBookShortName( ex.bookName ) ;
        
        List<ProblemMeta> metaProblems = ex.generateMetaProblems() ;
        
        for( ProblemMeta metaProblem : metaProblems ) {
            
            Problem problem = new Problem() ;
            problem.setBook( book ) ;
            problem.setTopic( topic ) ;
            problem.setChapterId( ex.chapterId ) ;
            problem.setProblemTag( metaProblem.getProblemTag() ) ;
            
            if( StringUtil.isNotEmptyOrNull( metaProblem.getExerciseSubName() ) ) {
                problem.setExerciseName( ex.exerciseName + "-" + 
                                         metaProblem.getExerciseSubName() ) ;
            }
            else {
                problem.setExerciseName( ex.exerciseName ) ;
            }
            
            problemRepo.save( problem ) ;
        }
    }
}
