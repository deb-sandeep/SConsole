package com.sandy.sconsole.api.jeetest;

import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;
import java.util.NoSuchElementException ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
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
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.BookRepository ;
import com.sandy.sconsole.dao.repository.master.TestQuestionRepository ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;
import com.sandy.sconsole.util.QuestionTextFormatter ;
import com.sandy.sconsole.util.ResponseMsg ;

@RestController
public class QBMRestController {
    
    static final Logger log = Logger.getLogger( QBMRestController.class ) ;
    
    @Autowired
    private TopicRepository topicRepo = null ;

    @Autowired
    private BookRepository bookRepo = null ;
    
    @Autowired
    private TestQuestionRepository testQuestionRepo = null ;
    
    @GetMapping( "/QBMMasterData" )
    public ResponseEntity<QBMMasterData> getQBMMasterData() {
        try {
            log.debug( "Fetching QBM Master Data" ) ;
            
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
        
        log.debug( "Fetching test question with ID = " + id );
        
        TestQuestion testQuestion = null ;
        
        if( id == null || id <= 0 ) {
            testQuestion = new TestQuestion() ;
        }
        else {
            try {
                testQuestion = testQuestionRepo.findById( id ).get() ;
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
    
    @DeleteMapping( "/TestQuestion/{id}" )
    public ResponseEntity<ResponseMsg> deleteQuestion( @PathVariable Integer id ) {
        
        log.debug( "Deleting test question with ID = " + id );
        
        if( id == null || id <= 0 ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND )
                                 .body( new ResponseMsg( "Question id can't be <=0" ) ) ;
        }
        testQuestionRepo.deleteById( id ) ;
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
            @RequestParam( value="selectedTopics",        required=false ) Integer[] topicIds,
            @RequestParam( value="selectedBooks",         required=false ) Integer[] bookIds ) {
        
        TestQuestionSearchEngine searchEngine = null ;
        
        try {
            searchEngine = new TestQuestionSearchEngine( testQuestionRepo ) ;
            List<TestQuestion> results = searchEngine.search( 
                                                    subjects, 
                                                    selectedQuestionTypes, 
                                                    showOnlyUnsynched, 
                                                    excludeAttempted, 
                                                    searchText, 
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
        
        log.debug( "POST /TestQuestion called." ) ;
        log.debug( question ) ;
        
        // What do we do when saving a question?
        // * Format the question text
        //   * Update the question object with the formatted text
        //   * Null out the creation and update time
        //   * If the id is <= 0, null it out so that a new question is created
        // * Call on the repository to save the object
        //   * Return the saved object back to the caller.
        try {
            question.setQuestionFormattedText( 
                    new QuestionTextFormatter().formatText( question.getQuestionText() ) ) ;
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
                
                log.debug( "Hash input = " + hashInput ) ;
                log.debug( "Hash = " + StringUtil.getHash( hashInput ) ) ;
                
                question.setHash( StringUtil.getHash( hashInput ) ) ;
            }
        
            question = testQuestionRepo.save( question ) ;
            return ResponseEntity.status( HttpStatus.OK )
                                 .body( question ) ;
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
}
