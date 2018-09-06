package com.sandy.sconsole.api.offlinesession;

import static com.sandy.sconsole.dao.entity.ProblemAttempt.* ;

import java.sql.Timestamp ;
import java.util.* ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.* ;

import com.sandy.sconsole.api.offlinesession.SessionCreationRequest.ProblemOutcome ;
import com.sandy.sconsole.dao.entity.LastSession ;
import com.sandy.sconsole.dao.entity.ProblemAttempt ;
import com.sandy.sconsole.dao.entity.Session ;
import com.sandy.sconsole.dao.entity.Session.SessionType ;
import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Problem ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.LastSessionRepository ;
import com.sandy.sconsole.dao.repository.ProblemAttemptRepository ;
import com.sandy.sconsole.dao.repository.SessionRepository ;
import com.sandy.sconsole.dao.repository.master.BookRepository ;
import com.sandy.sconsole.dao.repository.master.ProblemRepository ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;

@RestController
public class OfflineSessionCreationController {
    
    static final Logger log = Logger.getLogger( OfflineSessionCreationController.class ) ;
    
    @Autowired
    private TopicRepository topicRepo = null ;
    
    @Autowired
    private BookRepository bookRepo = null ;
    
    @Autowired
    private ProblemRepository problemRepo = null ;
    
    @Autowired
    private SessionRepository sessionRepo = null ;
    
    @Autowired
    private LastSessionRepository lsRepo = null ;
    
    @Autowired
    private ProblemAttemptRepository paRepo = null ;
    
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
            List<Integer> ids = bookRepo.findProblemBooksForTopic( topicId ) ;
            Iterable<Book> booksIter = bookRepo.findAllById( ids ) ;
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
            if( bookId == -1 ) {
                problems = problemRepo.findUnsolvedProblems( topicId ) ;
            }
            else {
                problems = problemRepo.findUnsolvedProblems( topicId, bookId ) ;
            }
            return ResponseEntity.status( HttpStatus.OK ).body( problems ) ;
        }
        catch( Exception e ) {
            return ResponseEntity.status( 500 ).body( null ) ;
        }
    }
    
    private Map<String, List<Topic>> getTopicsMap() {
        Iterable<Topic> topics = topicRepo.findAll() ;
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

    @PostMapping( "/Session" )
    public ResponseEntity<String> createOfflineSession( 
                                   @RequestBody SessionCreationRequest input ) {
        
        log.debug( "\n\n-------------------------------------" ) ;
        log.debug( "Saving an offline session." ) ;
        
        Session session = new Session() ;
        session.setSessionType( SessionType.decode( input.getSessionType() ) ) ;
        session.setTopic( topicRepo.findById( input.getTopicId() ).get() ) ;
        session.setDuration( input.getDuration() ) ;
        session.setAbsoluteDuration( input.getDuration() ) ;
        session.setStartTime( new Timestamp( input.getStartTime().getTime() ) ) ;
        session.setEndTime( new Timestamp( session.getStartTime().getTime() + 
                                           input.getDuration()*60*1000 ) );
        
        log.debug( "Basic session details - " ) ;
        log.debug( session.toString() ) ;
        
        log.debug( "Saving the session in DB" ) ;
        sessionRepo.save( session ) ;
        log.debug( "New session created. ID = " + session.getId() ) ;
        
        saveExerciseDetails( session, input ) ;
        updateLastSession( session, input ) ;
        
        return ResponseEntity.status( HttpStatus.OK ).body( "{\"message\":\"Success\"}" ) ;
    }
    
    private void saveExerciseDetails( Session session, SessionCreationRequest input ) {
        
        if( session.getSessionType() != SessionType.EXERCISE ) {
            return ;
        }
        
        log.debug( "Saving exercise details" ) ;
            
        List<ProblemOutcome> outcomes = input.getOutcome() ;
        
        Integer lastProblemId = outcomes.get( outcomes.size()-1 ).getProblemId() ;
        Problem lastProblem = problemRepo.findById( lastProblemId ).get() ;
        Book book = bookRepo.findById( input.getBookId() ).get() ;
        
        long startTime = session.getStartTime().getTime() ;
        int numSkipped = 0 ;
        int numSolved = 0 ;
        int numRedo = 0 ;
        int numPigeon = 0 ;
        int numIgnored = 0 ;
        
        for( ProblemOutcome outcome : outcomes ) {
            
            Integer problemId = outcome.getProblemId() ;
            Problem problem = problemRepo.findById( problemId ).get() ;
            
            ProblemAttempt attempt = new ProblemAttempt() ;
            attempt.setSession( session ) ;
            attempt.setProblem( problem ) ;
            attempt.setDuration( outcome.getDuration()*60 ) ;
            attempt.setOutcome( outcome.getOutcome() ) ;
            attempt.setStartTime( new Timestamp( startTime ) ) ;
            
            startTime += outcome.getDuration()*1000 ;
            
            attempt.setEndTime( new Timestamp( startTime ) ) ;
            
            String outcomeVal = outcome.getOutcome() ;
            if( outcomeVal.equals( OUTCOME_SKIP ) ) {
                numSkipped++ ;
                problem.setSkipped( true ) ;
            }
            else if( outcomeVal.equals( OUTCOME_SOLVED ) ) {
                numSolved++ ;
                problem.setSolved( true ) ;
            }
            else if( outcomeVal.equals( OUTCOME_REDO ) ) {
                numRedo++ ;
                problem.setRedo( true ) ;
            }
            else if( outcomeVal.equals( OUTCOME_PIGEON ) ) {
                numPigeon++ ;
                problem.setPigeoned( true ) ;
            }
            else if( outcomeVal.equals( OUTCOME_IGNORE ) ) {
                numIgnored++ ;
                problem.setIgnored( true ) ;
                problem.setSolved( true ) ;
            }
            
            problem.setStarred( outcome.getStarred() ) ;
            
            log.debug( "Updating problem master" ) ;
            problemRepo.save( problem ) ;
            
            log.debug( "Saving problem attempt - " ) ;
            log.debug( attempt.toString() ) ;
            paRepo.save( attempt ) ;
        }
        
        session.setBook( book ) ;
        session.setLastProblem( lastProblem ) ;
        session.setNumSkipped( numSkipped ) ;
        session.setNumSolved( numSolved ) ;
        session.setNumRedo( numRedo ) ;
        session.setNumPigeon( numPigeon ) ;
        session.setNumIgnored( numIgnored ) ;
        
        log.debug( "Updating session details with remaining info." ) ;
        log.debug( session.toString() ) ;
        
        sessionRepo.save( session ) ;
    }
    
    private void updateLastSession( Session session, SessionCreationRequest input ) {
        
        log.debug( "Updating last session information" ) ;
        
        LastSession ls = new LastSession() ;
        ls.setSubjectName( input.getSubject() ) ;
        ls.setSession( session ) ;
        
        LastSession lsInDB = lsRepo.findLastSessionForSubject( input.getSubject() ) ;
        if( lsInDB != null ) {
            
            if( session.getStartTime().getTime() > 
                lsInDB.getSession().getStartTime().getTime() ) {
                
                log.debug( "Setting this session as the last session" ) ;
                lsRepo.save( ls ) ;
            }
        }
        else {
            lsRepo.save( ls ) ;
        }
    }
}
