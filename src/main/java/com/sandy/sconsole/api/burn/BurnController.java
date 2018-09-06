package com.sandy.sconsole.api.burn;

import java.util.List ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.* ;

import com.sandy.sconsole.api.burn.ActivationInput.ProblemActivation ;
import com.sandy.sconsole.core.api.APIResponse ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;

@RestController
public class BurnController {
    
    static final Logger log = Logger.getLogger( BurnController.class ) ;
    
    @Autowired
    private ProblemRepository problemRepo = null ;
    
    @Autowired
    private ProblemAttemptRepository paRepo = null ;
    
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
                         @RequestBody ActivationInput activationInput ) {
        
        log.debug( "Updating problem activations" ) ;
        for( ProblemActivation input : activationInput.getProblemActivations() ) {
            log.debug( "\tID = " + input.getId() + ", active = " + input.getActive() ) ;
            problemRepo.updateActivation( input.getId(), input.getActive() ) ;
        }
        
        return ResponseEntity.ok().body( new APIResponse( "Succss" ) ) ;
    }
    
}
