package com.sandy.sconsole.api.jeetest.config;

import java.sql.Timestamp ;
import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

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

import com.sandy.sconsole.api.jeetest.qbm.vo.QBMMasterData ;
import com.sandy.sconsole.core.util.SConsoleUtil ;
import com.sandy.sconsole.dao.entity.TestConfigIndex ;
import com.sandy.sconsole.dao.entity.TestQuestionBinding ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.TestConfigIndexRepository ;
import com.sandy.sconsole.dao.repository.TestQuestionBindingRepository ;
import com.sandy.sconsole.dao.repository.master.TestQuestionRepository ;
import com.sandy.sconsole.util.ResponseMsg ;

@RestController
public class TestConfiguratorRestController {
    
    static final Logger log = Logger.getLogger( TestConfiguratorRestController.class ) ;
    
    private static final String SERVER_HOST = "192.168.0.117:8080" ;
    
    @Autowired
    private TestConfigIndexRepository tciRepo = null ;
    
    @Autowired
    private TestQuestionBindingRepository tqbRepo = null ;
    
    @Autowired
    private TestQuestionRepository tqRepo = null ;
    
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
    
    @GetMapping( "/TestConfiguration/{id}/Topics" )
    public ResponseEntity<Map<String, List<String>>> getTopicsForTest( 
                                                @PathVariable Integer id ) {
        try {
            log.debug( "Loading topics for test. Id = " + id ) ;
            List<Topic> topics = tqbRepo.getTopicsForTest( id ) ;
            
            Map<String, List<String>> subTopicsMap = new HashMap<>() ;
            subTopicsMap.put( QBMMasterData.S_TYPE_PHY,   new ArrayList<>() ) ;
            subTopicsMap.put( QBMMasterData.S_TYPE_CHEM,  new ArrayList<>() ) ;
            subTopicsMap.put( QBMMasterData.S_TYPE_MATHS, new ArrayList<>() ) ;
            
            for( Topic topic : topics ) {
                String sName = topic.getSubject().getName() ;
                List<String> topicList = subTopicsMap.get( sName ) ;
                topicList.add( topic.getTopicName() ) ;
            }
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( subTopicsMap ) ;
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
    
    @PostMapping( "/CloneTestConfiguration/{testId}" ) 
    public ResponseEntity<TestConfiguration> cloneTestConfiguration(
            @PathVariable(name="testId", required=true) Integer testId  ) {
        
        try {
            log.debug( "Cloning a test configuration." ) ;
            TestConfiguration config = loadConfig( testId ) ;
            TestConfiguration clonedConfig = cloneConfig( config ) ;
            Integer newId = saveConfig( clonedConfig ) ;
            clonedConfig = loadConfig( newId ) ;
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( clonedConfig ) ;
        }
        catch( Exception e ) {
            log.error( "Error saving test configuration", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
    
    /**
     * This method is called upon by the browser to the local server. The 
     * local server then posts the locally configured test to PiMon. This feature
     * enables us to create tests offline on a local app and then sync it
     * to PiMon once connectivity is established.
     */
    @PostMapping( "/SyncTestToPimon/{testId}" )
    public ResponseEntity<ResponseMsg> syncTestToPiMon( 
                @PathVariable(name="testId", required=true) Integer testId ) {
        
        try {
            if( SConsoleUtil.isOperatingOnPiMon() ) {
                return ResponseEntity.status( HttpStatus.BAD_REQUEST )
                                     .body( new ResponseMsg( "Already on server. Can't sync" ) ) ;
            }
            else {
                TestSynchronizer synchronizer = null ;
                TestConfiguration config = loadConfig( testId ) ;
                synchronizer = new TestSynchronizer( SERVER_HOST ) ;
                synchronizer.syncTest( config ) ;
                
                config.getTestConfigIndex().setSynched( true ) ;
                tciRepo.save( config.getTestConfigIndex() ) ;
                
                return ResponseEntity.status( HttpStatus.OK )
                                     .body( ResponseMsg.SUCCESS ) ;
            }
        }
        catch( Exception e ) {
            log.error( "Error synchronizing questions to server", e ) ;
            return ResponseEntity.status( 500 ).body( null ) ;
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
            config.setShortName( tci.getShortName() ) ;
            config.setTestConfigIndex( tci ) ;
            
            List<TestQuestionBinding> bindings = tqbRepo.findAllByTestConfigId( id ) ;
            
            if( bindings != null && !bindings.isEmpty() ) {
                
                List<TestQuestion> masterQList     = null ;
                List<String>       sectionNames    = null ;
                List<Integer>      sectionQIndices = null ;
                
                for( TestQuestionBinding binding : bindings ) {
                    
                    String  subjectName  = binding.getSubject().getName() ;
                    String  sectionName  = binding.getSectionName() ;
                    Integer sectionIndex = binding.getSectionIndex() ;
                    Integer qMasterIndex = -1 ;
                    
                    TestQuestion question = binding.getQuestion() ;
                    
                    masterQList = getMasterQuestionList( subjectName, config ) ;
                    sectionNames = getSectionNames( subjectName, config ) ;
                    sectionQIndices = getSectionQIndices( subjectName, sectionIndex, config ) ;
                    
                    qMasterIndex = masterQList.size() ;
                    
                    masterQList.add( question ) ;
                    sectionQIndices.add( qMasterIndex ) ;
                    
                    if( !sectionNames.contains( sectionName ) ) {
                        sectionNames.add( sectionName ) ;
                    }
                }
            }
        }
        return config ;
    }
    
    private List<TestQuestion> getMasterQuestionList( String subjectName,
                                                      TestConfiguration config ) {
        
        if( subjectName.equals( QBMMasterData.S_TYPE_PHY ) ) {
            return config.getPhyQuestions() ;
        }
        else if( subjectName.equals( QBMMasterData.S_TYPE_CHEM ) ) {
            return config.getChemQuestions() ;
        }
        else if( subjectName.equals( QBMMasterData.S_TYPE_MATHS ) ) {
            return config.getMathQuestions() ;
        }
        else {
            throw new RuntimeException( "Unknown subject name " + subjectName ) ;
        }
    }
    
    private List<String> getSectionNames( String subjectName, 
                                          TestConfiguration config ) {
        
        if( subjectName.equals( QBMMasterData.S_TYPE_PHY ) ) {
            return config.getPhySectionNames() ;
        }
        else if( subjectName.equals( QBMMasterData.S_TYPE_CHEM ) ) {
            return config.getChemSectionNames() ;
        }
        else if( subjectName.equals( QBMMasterData.S_TYPE_MATHS ) ) {
            return config.getMathSectionNames() ;
        }
        else {
            throw new RuntimeException( "Unknown subject name " + subjectName ) ;
        }
    }
    
    private List<Integer> getSectionQIndices( String subjectName,
                                              int sectionIndex,
                                              TestConfiguration config ) {
        
        List<List<Integer>> sectionIndexLists = null ;
        List<Integer> indexList = null ;
        
        if( subjectName.equals( QBMMasterData.S_TYPE_PHY ) ) {
            sectionIndexLists = config.getPhySecQuestionIndices() ;
        }
        else if( subjectName.equals( QBMMasterData.S_TYPE_CHEM ) ) {
            sectionIndexLists = config.getChemSecQuestionIndices() ;
        }
        else if( subjectName.equals( QBMMasterData.S_TYPE_MATHS ) ) {
            sectionIndexLists = config.getMathSecQuestionIndices() ;
        }
        else {
            throw new RuntimeException( "Unknown subject name " + subjectName ) ;
        }
        
        if( sectionIndexLists.size() >= sectionIndex ) {
            indexList = sectionIndexLists.get( sectionIndex-1 ) ;
        }
        else {
            indexList = new ArrayList<>() ;
            sectionIndexLists.add( indexList ) ;
        }
        return indexList ;
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
            ci.setCreationDate( new Timestamp( System.currentTimeMillis() ) ) ;
        }
        
        ci.setShortName( config.getShortName() ) ;
        ci.setExamType( config.getExamType() ) ;
        ci.setNumPhyQuestions( config.getPhyQuestions().size() ) ;
        ci.setNumChemQuestions( config.getChemQuestions().size() ) ;
        ci.setNumMathQuestions( config.getMathQuestions().size() ) ;
        ci.setTotalMarks( config.getTotalMarks() );
        ci.setDuration( config.getDuration() ) ;
        ci.setProjectedSolveTime( config.getProjectedSolveTime() ) ;
        ci.setLastUpdateDate( new Timestamp( System.currentTimeMillis() ) ) ;
        
        if( SConsoleUtil.isOperatingOnPiMon() ) {
            ci.setSynched( true ) ;
        }
        
        ci = tciRepo.save( ci ) ;
        
        // Recreate all question mapping rows and save them.
        List<TestQuestionBinding> tqbList = null ;
        tqbList = assembleTestQuestionBindings( config.getAllQuestions(), ci ) ;
        tqbRepo.saveAll( tqbList ) ;
        
        return ci.getId() ;
    }

    private List<TestQuestionBinding> assembleTestQuestionBindings( 
                                             List<TestQuestion> questionList,
                                             TestConfigIndex ci ) {
        
        List<TestQuestionBinding> tqbList = new ArrayList<>() ;
        
        String subjectName = null ;
        int sectionIndex = 1 ;
        String sectionName = null ;
        int sequence = 1 ;
        
        for( int i=0; i<questionList.size(); i++ ) {
            
            TestQuestion q = questionList.get( i ) ;
            
            if( subjectName == null || 
                !subjectName.equals( q.getSubject().getName() ) ) {
                
                subjectName = q.getSubject().getName() ;
                sectionName = q.getQuestionType() ;
                sectionIndex = 1 ;
                sequence = 1 ;
            }
            else if( !q.getQuestionType().equals( sectionName ) ) {
                sectionName = q.getQuestionType() ;
                sectionIndex++ ;
                sequence = 1 ;
            }
            else {
                sequence++ ;
            }
            
            TestQuestionBinding tqb = new TestQuestionBinding() ;
            tqb.setTestConfig( ci ) ;
            tqb.setTopic( q.getTopic() ) ;
            tqb.setQuestion( q ) ;
            tqb.setSubject( q.getSubject() ) ;
            tqb.setSectionIndex( sectionIndex ) ;
            tqb.setSectionName( sectionName ) ;
            tqb.setSequence( sequence ) ;
            
            tqbList.add( tqb ) ;
        }
        return tqbList ;
    }
    
    private TestConfiguration cloneConfig( TestConfiguration config ) 
        throws Exception {
        
        TestCloner cloner = new TestCloner( tqRepo ) ;
        
        config.setId( -1 ) ;
        config.getAllQuestions().clear() ;
        config.setCustomDuration( config.getDuration() ) ;
        config.setTestConfigIndex( null ) ;
        
        cloner.cloneQuestions( config.getPhyQuestions() ) ;
        cloner.cloneQuestions( config.getChemQuestions() ) ;
        cloner.cloneQuestions( config.getMathQuestions() ) ;
        
        return config ;
    }
}
