package com.sandy.sconsole.api.jeetest.qbm;

import java.math.BigDecimal ;
import java.math.BigInteger ;
import java.net.InetAddress ;
import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Map ;
import java.util.NoSuchElementException ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.dao.DataIntegrityViolationException ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.DeleteMapping ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RequestParam ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.common.util.StringUtil ;
import com.sandy.sconsole.api.jeetest.qbm.helper.BulkQuestionEntryHelper ;
import com.sandy.sconsole.api.jeetest.qbm.helper.TestQuestionSearchEngine ;
import com.sandy.sconsole.api.jeetest.qbm.helper.TestQuestionSynchronizer ;
import com.sandy.sconsole.api.jeetest.qbm.vo.BulkQEntry ;
import com.sandy.sconsole.api.jeetest.qbm.vo.QBMMasterData ;
import com.sandy.sconsole.api.jeetest.qbm.vo.QBTopicInsight ;
import com.sandy.sconsole.api.jeetest.qbm.vo.TestQuestionEx ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.TestQuestionBindingRepository ;
import com.sandy.sconsole.dao.repository.master.BookRepository ;
import com.sandy.sconsole.dao.repository.master.TestQuestionRepository ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;
import com.sandy.sconsole.util.QuestionTextFormatter ;
import com.sandy.sconsole.util.ResponseMsg ;

@RestController
public class QBMRestController {
    
    static final Logger log = Logger.getLogger( QBMRestController.class ) ;
    
    private static final String SERVER_HOST = "192.168.0.117:8080" ;
    
    @Autowired
    private TopicRepository topicRepo = null ;

    @Autowired
    private BookRepository bookRepo = null ;
    
    @Autowired
    private TestQuestionRepository tqRepo = null ;
    
    @Autowired
    private TestQuestionBindingRepository tqbRepo = null ;
    
    @GetMapping( "/QBTopicInsights" )
    public ResponseEntity<List<QBTopicInsight>> getQBInsights() {
        try {
            List<Object[]> results = tqRepo.getTopicBasedInsight() ;
            List<QBTopicInsight> insights = assembleInsights( results ) ;
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( insights ) ;
        }
        catch( Exception e ) {
            log.error( "Error getting QB insights", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
    
    @GetMapping( "/QBMMasterData" )
    public ResponseEntity<QBMMasterData> getQBMMasterData() {
        try {
            QBMMasterData qbmMaster = new QBMMasterData() ;
            
            for( Topic topic : topicRepo.findAll() ) {
                qbmMaster.addTopic( topic ) ;
            }
            
            for( Book book : bookRepo.findAll() ) {
                qbmMaster.addBook( book ) ;
            }
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( qbmMaster ) ;
        }
        catch( Exception e ) {
            log.error( "Error creating master data", e ) ;
            return ResponseEntity.status( 500 ).body( null ) ;
        }
    }
    
    @GetMapping( "/TestQuestion/{id}" )
    public ResponseEntity<TestQuestion> getQuestion( @PathVariable Integer id ) {
        
        TestQuestion testQuestion = null ;
        
        if( id == null || id <= 0 ) {
            testQuestion = new TestQuestion() ;
        }
        else {
            try {
                testQuestion = tqRepo.findById( id ).get() ;
            }
            catch( NoSuchElementException e ) {
                log.error( "No test question found with id = " + id ) ;
            }
        }
        
        ResponseEntity<TestQuestion> response = null ;
        if( testQuestion == null ) {
            response = ResponseEntity.status( HttpStatus.NOT_FOUND )
                                     .body( null ) ;
        }
        else {
            response = ResponseEntity.status( HttpStatus.OK )
                                     .body( testQuestion ) ;
        }
        return response ;
    }
    
    @GetMapping( "/TestQuestion/Topic/{topicId}" )
    public ResponseEntity<Map<String, List<TestQuestion>>> getUnassignedQuestionsByTopic( 
                                            @PathVariable Integer topicId,
                                            @RequestParam( "examType" ) String examType ) {
        
        ResponseEntity<Map<String, List<TestQuestion>>> response = null ;
        
        if( topicId == null ) {
            response = ResponseEntity.status( HttpStatus.BAD_REQUEST )
                                     .body( null ) ;
        }
        else {
            List<TestQuestion> questions = null ;
            questions = tqRepo.findActiveQuestionsForTopic( topicId ) ;
            
            Map<String,List<TestQuestion>> map = new HashMap<>() ;
            for( String qType : QBMMasterData.questionTypes ) {
                map.put( qType, new ArrayList<>() ) ;
            }
            
            List<TestQuestion> questionList = null ;
            for( TestQuestion q : questions ) {
                questionList = map.get( q.getQuestionType() ) ;
                if( examType.equals( QBMMasterData.EXAM_TYPE_MAIN ) ) {
                    if( q.getQuestionType().equals( QBMMasterData.Q_TYPE_SCA ) ) {
                        questionList.add( q ) ;
                    }
                }
                else {
                    questionList.add( q ) ;
                }
            }
            
            response = ResponseEntity.status( HttpStatus.OK )
                                     .body( map ) ;
        }
        return response ;
    }
    
    
    @DeleteMapping( "/TestQuestion/{id}" )
    public ResponseEntity<ResponseMsg> deleteQuestion( @PathVariable Integer id ) {
        
        if( id == null || id <= 0 ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND )
                                 .body( new ResponseMsg( "Question id can't be <=0" ) ) ;
        }
        tqRepo.deleteById( id ) ;
        return ResponseEntity.status( HttpStatus.OK )
                             .body( new ResponseMsg( "Successfully deleted" ) ) ;
    }
    
    @GetMapping( "/TestQuestion" )
    public ResponseEntity<List<TestQuestion>> searchQuestions(
            @RequestParam( value="subjects",              required=false ) String[] subjects,
            @RequestParam( value="selectedQuestionTypes", required=false ) String[] selectedQuestionTypes,
            @RequestParam( value="showOnlyUnsynched",     required=false ) Boolean showOnlyUnsynched,
            @RequestParam( value="excludeAttempted",      required=false ) Boolean excludeAttempted,
            @RequestParam( value="searchText",            required=false ) String searchText,
            @RequestParam( value="testConfigId",          required=false ) Integer testConfigId,
            @RequestParam( value="selectedTopics",        required=false ) Integer[] topicIds,
            @RequestParam( value="selectedBooks",         required=false ) Integer[] bookIds ) {
        
        TestQuestionSearchEngine searchEngine = null ;
        
        try {
            searchEngine = new TestQuestionSearchEngine( tqRepo, tqbRepo ) ;
            List<TestQuestion> results = searchEngine.search( 
                                                    subjects, 
                                                    selectedQuestionTypes, 
                                                    showOnlyUnsynched, 
                                                    excludeAttempted, 
                                                    searchText, 
                                                    testConfigId,
                                                    topicIds, 
                                                    bookIds ) ;
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( results ) ;
        }
        catch( Exception e ) {
            return ResponseEntity.status( 500 ).body( null ) ;
        }
    }
    
    @PostMapping( "/TestQuestion" )
    public ResponseEntity<TestQuestion> saveQuestion( @RequestBody TestQuestion question ) {
        
        // What do we do when saving a question?
        // * Format the question text
        //   * Update the question object with the formatted text
        //   * Null out the creation and update time
        //   * If the id is <= 0, null it out so that a new question is created
        // * Call on the repository to save the object
        //   * Return the saved object back to the caller.
        // Also, if the question is not being saved on the server, force the
        // synched flag to false.
        try {
            
            String questionText = question.getQuestionText() ;
            if( StringUtil.isNotEmptyOrNull( question.getLctContext() ) ) {
                questionText = "<div class='lct-context'>" + 
                               question.getLctContext() + 
                               "</div>" + 
                               question.getQuestionText() ;
            }
            
            question.setQuestionFormattedText( new QuestionTextFormatter().formatText( questionText ) ) ;
            question.setCreationTime( null ) ;
            question.setLastUpdateTime( null ) ;
            
            if( question.getId() <= 0 ) {
                question.setId( null ) ;
                
                String hashInput = question.getTargetExam() + 
                                   question.getSubject().getName() +  
                                   question.getTopic().getId() + 
                                   question.getBook().getId() + 
                                   question.getQuestionType() + 
                                   question.getQuestionRef() ;  
                
                question.setHash( StringUtil.getHash( hashInput ) ) ;
            }
            
            // Why do we do this? To handle the case where I am editing a 
            // local question which has been previously synched - if we 
            // don't turn off the synched marker, we won't be able to 
            // see this question in the list of unsynched questions.
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ipAddress = inetAddress.getHostAddress() ;
            if( !ipAddress.equals( "192.168.0.117" ) ) {
                question.setSynched( false ) ;
            }
        
            question = tqRepo.save( question ) ;
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( question ) ;
        }
        catch( DataIntegrityViolationException dive ) {
            log.error( "A question with same meta data is found. Can't update." ) ; 
            return ResponseEntity.status( HttpStatus.PRECONDITION_FAILED )
                    .             body( null ) ;
        }
        catch( Exception e ) {
            log.error( "Could not save question.", e ) ;
            return ResponseEntity.status( HttpStatus.PRECONDITION_FAILED )
                                 .body( null ) ;
        }
    }
    
    @PostMapping( "/FormattedText" )
    public ResponseEntity<Map<String,String>> getFormattedText ( 
                            @RequestBody String text ) {
        
        try {
            QuestionTextFormatter fmt = new QuestionTextFormatter() ;            
            Map<String, String> responseMap = new HashMap<String, String>() ;
            responseMap.put( "fmtText", fmt.formatText( text ) ) ;
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( responseMap ) ;
        }
        catch( Exception e ) {
            log.error( "Error formatting input", e ) ;
            return ResponseEntity.status( 500 ).body( null ) ;
        }
    }
    
    /**
     * This method is called upon by the browser to the local server. The 
     * local server then posts a requests to the pimon server for it to 
     * import the local questions.
     */
    @PostMapping( "/SyncTestQuestionsToPimon" )
    public ResponseEntity<ResponseMsg> syncQuestions ( @RequestBody Integer[] ids ) {
        
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ipAddress = inetAddress.getHostAddress() ;
            if( ipAddress.equals( "192.168.0.117" ) ) {
                return ResponseEntity.status( HttpStatus.BAD_REQUEST )
                                     .body( new ResponseMsg( "Already on server. Can't sync" ) ) ;
            }
            else {
                TestQuestionSynchronizer synchronizer = null ;
                synchronizer = new TestQuestionSynchronizer( SERVER_HOST ) ;
                synchronizer.syncQuestions( ids ) ;
                
                return ResponseEntity.status( HttpStatus.OK )
                                     .body( ResponseMsg.SUCCESS ) ;
            }
        }
        catch( Exception e ) {
            log.error( "Error synchronizing questions to server", e ) ;
            return ResponseEntity.status( 500 ).body( null ) ;
        }
    }
    
    /**
     * This function is called upon by some local server in the network in
     * an attempt to import the requested questions.
     */
    @PostMapping( "/ImportNewTestQuestions" ) 
    public ResponseEntity<ResponseMsg> importNewQuestions( 
                                @RequestBody List<TestQuestionEx> questions ) {
        
        TestQuestionSynchronizer synchronizer = new TestQuestionSynchronizer() ;
        try {
            synchronizer.importQuestions( questions ) ;
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( ResponseMsg.SUCCESS ) ;
        }
        catch( Exception e ) {
            log.error( "Error formatting input", e ) ;
            return ResponseEntity.status( 500 )
                                 .body( new ResponseMsg( e.getMessage() ) ) ;
        }
    }

    private List<QBTopicInsight> assembleInsights( List<Object[]> tupules ) {
        
        List<QBTopicInsight> insights = new ArrayList<>() ;
        Map<Integer, QBTopicInsight> insightsMap = new LinkedHashMap<>() ;
        
        for( Object[] tupule : tupules ) {
            Integer topicId      = (Integer)tupule[0] ;
            String  subjectName  = (String )tupule[1] ;
            String  topicName    = (String )tupule[2] ;
            String  questionType = (String )tupule[3] ;
            
            // Total number of questions in this topic's question bank
            Integer numQ = 0 ; 
            
            // Total number of questions attempted in this topic from the question bank
            Integer attQ = 0 ; 
            
            // Total number of questions assigned to tests from this topic's question bank
            Integer assQ = 0 ; 
            
            // The number of available questions for assignment.
            Integer avaQ = 0 ;
            
            QBTopicInsight insight = insightsMap.get( topicId ) ;
            if( insight == null ) {
                insight = new QBTopicInsight() ;
                insightsMap.put( topicId, insight ) ;
                
                for( String qType : QBMMasterData.questionTypes ) {
                    insight.getTotalQuestionsByType().put( qType, 0 ) ;
                    insight.getAttemptedQuestionsByType().put( qType, 0 ) ;
                    insight.getAvailableQuestionsByType().put( qType, 0 ) ;
                    insight.getAssignedQuestionsByType().put( qType, 0 ) ;
                }
            }
            
            insight.setTopicId( topicId ) ;
            insight.setSubjectName( subjectName ) ;
            insight.setTopicName( topicName ) ;
            
            if( questionType != null ) {
                numQ = ((BigInteger)tupule[4]).intValue() ;
                attQ = ((BigDecimal)tupule[5]).intValue() ;
                assQ = ((BigInteger)tupule[6]).intValue() ;
                avaQ = numQ - assQ ; 
                
                insight.setTotalQuestions( insight.getTotalQuestions() + numQ ) ;
                insight.setAttemptedQuestions( insight.getAttemptedQuestions() + attQ ) ;
                insight.setAssignedQuestions( insight.getAssignedQuestions() + assQ ) ;
                insight.setAvailableQuestions( insight.getAvailableQuestions() + avaQ ) ;
                
                insight.getTotalQuestionsByType().put( questionType, numQ ) ;
                insight.getAttemptedQuestionsByType().put( questionType, attQ ) ;
                insight.getAssignedQuestionsByType().put( questionType, assQ ) ;
                insight.getAvailableQuestionsByType().put( questionType, avaQ ) ;
            }
        }
        
        for( QBTopicInsight insight : insightsMap.values() ) {
            insights.add( insight ) ;
        }
        
        return insights ;
    }

    @GetMapping( "/BulkQuestionsEntryMeta" ) 
    public ResponseEntity<List<BulkQEntry>> getRevisionQuestions(
            @RequestParam( name="subjectName",  required=true ) String  subjectName,
            @RequestParam( name="topicId",      required=true ) Integer topicId,
            @RequestParam( name="bookId",       required=true ) Integer bookId,
            @RequestParam( name="baseQRef",     required=false) String  baseQRef ) {
        
        try {
            Topic topic = topicRepo.findById( topicId ).get() ;
            Book  book  = bookRepo.findById( bookId ).get() ;
            
            BulkQuestionEntryHelper helper = new BulkQuestionEntryHelper( tqRepo ) ;
            List<BulkQEntry> entries = helper.findBulkQuestionEntries( subjectName, topic, book, baseQRef ) ;
            
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( entries ) ;
        }
        catch( Exception e ) {
            log.error( "Error getting available test attempts.", e ) ;
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                 .body( null ) ;
        }
    }
}
