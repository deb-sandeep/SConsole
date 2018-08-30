package com.sandy.sconsole.api.offlinesession;

import java.util.* ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.* ;

import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.BookRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;

@RestController
public class OfflineSessionCreationController {
    
    static final Logger log = Logger.getLogger( OfflineSessionCreationController.class ) ;
    
    @Autowired
    private TopicRepository topicRepository = null ;
    
    @Autowired
    private BookRepository bookRepository = null ;
    
    @Autowired
    private ProblemRepository problemRepository = null ;
    
    @GetMapping( "/Topic" )
    public ResponseEntity<Map<String, List<Topic>>> getTopics() {
        try {
            return ResponseEntity.status( HttpStatus.OK )
                    .body( getTopicsMap() ) ;
        }
        catch( Exception e ) {
            return ResponseEntity.status( 500 ).body( null ) ;
        }
    }
    
    @GetMapping( "/Book" )
    public ResponseEntity<List<Book>> getBooks(
            @RequestParam( "topicId" ) Integer topicId ) {
        try {
            List<Integer> ids = bookRepository.findProblemBooksForTopic( topicId ) ;
            Iterable<Book> booksIter = bookRepository.findAllById( ids ) ;
            List<Book> books = new ArrayList<Book>() ;
            
            booksIter.forEach( books::add ) ;
            
            return ResponseEntity.status( HttpStatus.OK ).body( books ) ;
        }
        catch( Exception e ) {
            return ResponseEntity.status( 500 ).body( null ) ;
        }
    }
    
    @GetMapping( "/Problems" )
    public ResponseEntity<List<Problem>> getProblems( 
            @RequestParam( "topicId" ) Integer topicId, 
            @RequestParam( "bookId" ) Integer bookId ) {
        
        List<Problem> problems = null ;
        
        try {
            problems = problemRepository.findUnsolvedProblems( topicId, bookId ) ;
            return ResponseEntity.status( HttpStatus.OK ).body( problems ) ;
        }
        catch( Exception e ) {
            return ResponseEntity.status( 500 ).body( null ) ;
        }
    }
    
    @PostMapping( "/Session" )
    public ResponseEntity<String> createOfflineSession( 
                                   @RequestBody SessionCreationRequest input ) {

        return ResponseEntity.status( HttpStatus.OK ).body( "Burrahh" ) ;
    }
    
    private Map<String, List<Topic>> getTopicsMap() {
        Iterable<Topic> topics = topicRepository.findAll() ;
        Map<String, List<Topic>> topicMap = new HashMap<String, List<Topic>>() ;
        
        for( Topic topic : topics ) {
            List<Topic> topicList = topicMap.get( topic.getSubject().getName() ) ;
            if( topicList == null ) {
                topicList = new ArrayList<Topic>() ;
                topicMap.put( topic.getSubject().getName(), topicList ) ;
            }
            topicList.add( topic ) ;
        }
        
        return topicMap ;
    }
}
