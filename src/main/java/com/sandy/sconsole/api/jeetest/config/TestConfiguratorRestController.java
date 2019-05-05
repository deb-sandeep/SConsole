package com.sandy.sconsole.api.jeetest.config;

import java.util.List ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.sconsole.dao.entity.TestConfigIndex ;
import com.sandy.sconsole.dao.repository.TestConfigIndexRepository ;

@RestController
public class TestConfiguratorRestController {
    
    static final Logger log = Logger.getLogger( TestConfiguratorRestController.class ) ;
    
    @Autowired
    private TestConfigIndexRepository tciRepo = null ;
    
    @GetMapping( "/TestConfigurationIndex" )
    public ResponseEntity<List<TestConfigIndex>> getTestConfigIndexList() {
        try {
            log.debug( "Fetching test configuration summaries" ) ;
            List<TestConfigIndex> tciList = null ;
            tciList = tciRepo.findUnattemptedTests() ;
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( tciList ) ;
        }
        catch( Exception e ) {
            log.error( "Error getting test summary list", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
    
    @PostMapping( "/TestConfiguration" ) 
    public ResponseEntity<TestConfiguration> saveTestConfiguration(
                                    @RequestBody TestConfiguration config ) {
        
        try {
            log.debug( "Saving a test configuration." ) ;
            Integer id = saveConfig( config ) ;
            config.setId( id ) ;
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( config ) ;
        }
        catch( Exception e ) {
            log.error( "Error saving test configuration", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
    
    // -------------------- Utility private functions ------------------------
    
    private Integer saveConfig( TestConfiguration config ) 
        throws Exception {
        
        // Check if this is an existing configuration.. if so, check 
        // if this has been attempted.. if so, throw an exception.
        
        // Else save and send back the new ID
        
        return 0 ;
    }
}
