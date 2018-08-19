package com.sandy.sconsole.api.mdm.problem;

import org.apache.log4j.Logger ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.sconsole.api.mdm.MDMInput ;
import com.sandy.sconsole.core.api.APIRespose ;

@RestController
public class ProblemMasterController {
    
	private static final Logger log = Logger.getLogger( ProblemMasterController.class ) ;
    
    @PostMapping( "/ProblemMaster" )
    public APIRespose updateProblemMasterList( @RequestBody MDMInput input ) {
        log.debug( input.getInputPayload() );
        return new APIRespose( "Success" ) ;
    }
}
