package com.sandy.sconsole.api.jeetest.config;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.DeleteMapping ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.sconsole.api.jeetest.qbm.QBMMasterData ;
import com.sandy.sconsole.dao.entity.TestConfigIndex ;
import com.sandy.sconsole.dao.entity.TestQuestionBinding ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.repository.TestConfigIndexRepository ;
import com.sandy.sconsole.dao.repository.TestQuestionBindingRepository ;
import com.sandy.sconsole.util.ResponseMsg ;

@RestController
public class TestConfiguratorRestController {
    
    static final Logger log = Logger.getLogger( TestConfiguratorRestController.class ) ;
    
    @Autowired
    private TestConfigIndexRepository tciRepo = null ;
    
    @Autowired
    private TestQuestionBindingRepository tqbRepo = null ;
    
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
    
    @GetMapping( "/TestConfiguration/{id}" )
    public ResponseEntity<TestConfiguration> getTestConfiguration( 
                                                @PathVariable Integer id ) {
        try {
            log.debug( "Loading a test configuration. Id = " + id ) ;
            TestConfiguration config = loadConfig( id ) ;
            if( config != null ) {
                return ResponseEntity.status( HttpStatus.OK )
                                     .body( config ) ;
            }
            else {
                log.error( "Error loading test configuration. No records found" ) ;
                return ResponseEntity.status( HttpStatus.BAD_REQUEST )
                                     .body( null ) ;
            }
        }
        catch( Exception e ) {
            log.error( "Error loading test configuration", e ) ;
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
    
    @DeleteMapping( "/TestConfiguration/{id}" )
    public ResponseEntity<ResponseMsg> deleteTestConfiguration( 
                                                 @PathVariable Integer id ) {
        try {
            log.debug( "Deleting test configuration. Id = " + id ) ;
            deleteConfig( id ) ;
            return ResponseEntity.status( HttpStatus.OK )
                    .body( ResponseMsg.SUCCESS ) ;
        }
        catch( Exception e ) {
            log.error( "Error deleting test configuration", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
    
    // -------------------- Utility private functions ------------------------
    
    private void deleteConfig( Integer id )
        throws Exception {
        
        TestConfigIndex ci = null ;
        
        // Check if this is an existing configuration.. if so, check 
        // if this has been attempted.. if so, throw an exception. This
        // situation should not arise but my OCD is kicking in..
        ci = tciRepo.findById( id ).get() ;
        if( ci.getAttempted() ) {
            log.error( "Can't modify an attempted test" ) ;
            throw new Exception( "Can't modify an attempted test." ) ;
        }
        
        // Let's delete all the question mapping rows if this is a new config
        log.debug( "Deleting all existing test question associations" ) ;
        tqbRepo.deleteByTestConfigId( ci.getId() ) ;
        
        tciRepo.deleteById( id ) ;
    }
    
    private TestConfiguration loadConfig( Integer id ) 
        throws Exception {
        
        TestConfiguration config = null ;
        
        TestConfigIndex tci = tciRepo.findById( id ).get() ;
        if( tci != null ) {
            config = new TestConfiguration() ;
            config.setId( id ) ;
            config.setExamType( tci.getExamType() ) ;
            
            List<TestQuestionBinding> bindings = tqbRepo.findAllByTestConfigId( id ) ;
            if( bindings != null && !bindings.isEmpty() ) {
                for( TestQuestionBinding binding : bindings ) {
                    String sName = binding.getSubject().getName() ;
                    if( sName.equals( QBMMasterData.S_TYPE_PHY ) ) {
                        config.getPhyQuestions().add( binding.getQuestion() ) ;
                    }
                    else if( sName.equals( QBMMasterData.S_TYPE_CHEM ) ) {
                        config.getChemQuestions().add( binding.getQuestion() ) ;
                    }
                    else if( sName.equals( QBMMasterData.S_TYPE_MATHS ) ) {
                        config.getMathQuestions().add( binding.getQuestion() ) ;
                    }
                }
            }
        }
        
        return config ;
    }
    
    private Integer saveConfig( TestConfiguration config ) 
        throws Exception {
        
        TestConfigIndex ci = null ;
        
        // Check if this is an existing configuration.. if so, check 
        // if this has been attempted.. if so, throw an exception. This
        // situation should not arise but my OCD is kicking in..
        if( config.getId() > 0 ) {
            ci = tciRepo.findById( config.getId() ).get() ;
            if( ci.getAttempted() ) {
                log.error( "Can't modify an attempted test" ) ;
                throw new Exception( "Can't modify an attempted test." ) ;
            }
            
            // Let's delete all the question mapping rows if this is a new config
            log.debug( "Deleting all existing test question associations" ) ;
            tqbRepo.deleteByTestConfigId( ci.getId() ) ;
        }
        else {
            ci = new TestConfigIndex() ;
        }
        
        // Save the test config index - get the id
        ci.setExamType( config.getExamType() ) ;
        ci.setNumPhyQuestions( config.getPhyQuestions().size() ) ;
        ci.setNumChemQuestions( config.getChemQuestions().size() ) ;
        ci.setNumMathQuestions( config.getMathQuestions().size() ) ;
        
        // Recreate all question mapping rows
        List<TestQuestionBinding> tqbList = new ArrayList<>() ;
        int duration = 0 ;
        duration += assembleTestQuestionBinding( tqbList, config.getPhyQuestions() ) ;
        duration += assembleTestQuestionBinding( tqbList, config.getChemQuestions() ) ;
        duration += assembleTestQuestionBinding( tqbList, config.getMathQuestions() ) ;
        
        // Save the config index
        ci.setDuration( duration ) ;
        ci = tciRepo.save( ci ) ;
        
        // Associate the config index with the question bindings
        for( TestQuestionBinding tqb : tqbList ) {
            tqb.setTestConfig( ci ) ;
        }
        
        // Save the mappings
        tqbRepo.saveAll( tqbList ) ;
        
        return ci.getId() ;
    }
    
    private int assembleTestQuestionBinding( List<TestQuestionBinding> tqbList,
                                             List<TestQuestion> questionList ) {

        int duration = 0 ;
        for( int i=0; i<questionList.size(); i++ ) {
            
            TestQuestion q = questionList.get( i ) ;
            
            duration += q.getProjectedSolveTime() ;
            
            TestQuestionBinding tqb = new TestQuestionBinding() ;
            tqb.setSubject( q.getSubject() ) ;
            tqb.setTopic( q.getTopic() ) ;
            tqb.setQuestion( q ) ;
            tqb.setSequence( i+1 ) ;
            
            tqbList.add( tqb ) ;
        }
        
        return duration ;
    }
}
