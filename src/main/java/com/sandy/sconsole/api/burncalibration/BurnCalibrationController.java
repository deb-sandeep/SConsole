package com.sandy.sconsole.api.burncalibration;

import java.util.List ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.* ;

import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.burncalibration.ActivationInput.ProblemActivation ;
import com.sandy.sconsole.core.api.APIResponse ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;
import com.sandy.sconsole.screenlet.study.TopicBurnInfo ;

@RestController
public class BurnCalibrationController {
    
    static final Logger log = Logger.getLogger( BurnCalibrationController.class ) ;
    
    @Autowired
    private ProblemRepository problemRepo = null ;
    
    @Autowired
    private ProblemAttemptRepository paRepo = null ;
    
    @Autowired
    private TopicRepository topicRepo = null ;
    
    @GetMapping( "/AllUnsolvedProblems" )
    public ResponseEntity<List<Problem>> getAllUnsolvedProblems( 
            @RequestParam( "topicId" ) Integer topicId ) {
        
        List<Problem> problems = null ;
        
        try {
            problems = problemRepo.findAllUnsolvedProblemsByTopicId( topicId ) ;
            return ResponseEntity.status( HttpStatus.OK ).body( problems ) ;
        }
        catch( Exception e ) {
            log.error( "Error during API execution", e ) ;
            return ResponseEntity.status( 500 ).body( null ) ;
        }
    }
    
    @GetMapping( "/HistoricBurnStats" )
    public ResponseEntity<List<HistoricBurnStat>> getHistoricBurnStats ( 
            @RequestParam( "topicId" ) Integer topicId ) {
        
        try {
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( paRepo.getHistoricBurnStats( topicId ) ) ;
        }
        catch( Exception e ) {
            log.error( "Error during API execution", e ) ;
            return ResponseEntity.status( 500 ).body( null ) ;
        }
    }

    @PostMapping( "/UpdateProblemActivations" )
    public ResponseEntity<APIResponse> updateActivations( 
                         @RequestBody ActivationInput activationInput ) 
        throws Exception {
        
        log.debug( "Updating problem activations" ) ;
        for( ProblemActivation input : activationInput.getProblemActivations() ) {
            problemRepo.updateActivation( input.getId(), input.getActive() ) ;
        }
        
        Topic topic = topicRepo.findById( activationInput.getTopicId() )
                               .get() ;
        TopicBurnInfo burnInfo = new TopicBurnInfo( topic ) ;
        
        SConsole.GLOBAL_EVENT_BUS
                .publishEvent( EventCatalog.TOPIC_BURN_CALIBRATED, burnInfo ) ; 
        
        return ResponseEntity.ok().body( new APIResponse( "Succss" ) ) ;
    }
    
}
