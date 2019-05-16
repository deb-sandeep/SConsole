package com.sandy.sconsole.api.jeetest.exam;

import java.sql.Timestamp ;
import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.sconsole.dao.entity.ClickStreamEvent ;
import com.sandy.sconsole.dao.entity.TestAttempt ;
import com.sandy.sconsole.dao.entity.TestQuestionAttempt ;
import com.sandy.sconsole.dao.repository.ClickStreamEventRepository ;
import com.sandy.sconsole.dao.repository.TestAttemptRepository ;
import com.sandy.sconsole.dao.repository.TestQuestionAttemptRepository ;
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
    
    @PostMapping( "/TestAttempt" ) 
    public ResponseEntity<TestAttempt> saveTestAttempt(
                                    @RequestBody TestAttempt attempt ) {
        try {
            log.debug( "Saving a test attempt." ) ;
            
            if( attempt.getDateAttempted() == null ) {
                attempt.setDateAttempted( new Timestamp( new Date().getTime() ) );
            }
            
            TestAttempt savedAttempt = taRepo.save( attempt ) ;
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
            for( TestQuestionAttempt attempt : attempts ) {
                log.debug( "Saving attempt for quesiton - " + attempt.getTestQuestionId() ) ;
                tqaRepo.save( attempt ) ; 
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
            cseRepo.save( event ) ;
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( ResponseMsg.SUCCESS ) ;
        }
        catch( Exception e ) {
            log.error( "Error saving test configuration", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
}
