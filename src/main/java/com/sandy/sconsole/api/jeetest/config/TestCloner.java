package com.sandy.sconsole.api.jeetest.config;

import java.util.Collections ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;
import java.util.Stack ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.repository.master.TestQuestionRepository ;

public class TestCloner {
    
    static final Logger log = Logger.getLogger( TestCloner.class ) ;
    
    private TestQuestionRepository tqRepo = null ;
    
    private Map<Integer, Map<String, Stack<TestQuestion>>> questionMap = new HashMap<>() ;

    public TestCloner( TestQuestionRepository tqRepo ) {
        this.tqRepo = tqRepo ;
    }

    public void cloneQuestions( List<TestQuestion> questions ) 
        throws Exception {
        
        for( int i=0; i<questions.size(); i++ ) {
            boolean cloneFound = false ;
            
            TestQuestion originalQ = questions.get( i ) ;
            String qType = originalQ.getQuestionType() ;
            
            TestQuestion clonedQ = cloneTestQuestion( originalQ.getTopic().getId(),
                                                      qType ) ;
            if( clonedQ != null ) {
                questions.set( i, clonedQ ) ;
                cloneFound = true ;
            }
            else {
                for( int j=0; j<questions.size(); j++ ) {
                    TestQuestion question = questions.get( j ) ;
                    clonedQ = cloneTestQuestion( question.getTopic().getId(), qType ) ;
                    if( clonedQ != null ) {
                        questions.set( i, clonedQ ) ;
                        cloneFound = true ;
                        break ;
                    }
                }
            }
            
            if( !cloneFound ) {
                throw new Exception( "Unassigned question not found" ) ;
            }
        }
    }
    
    private TestQuestion cloneTestQuestion( Integer topicId,
                                            String questionType ) {
        
        Stack<TestQuestion> questions = null ;
        questions = fetchUnassignedQuestions( topicId, questionType ) ;
        if( !questions.isEmpty() ) {
            return questions.pop() ;
        }
        return null ;
    }

    private Stack<TestQuestion> fetchUnassignedQuestions( Integer topicId,
                                                          String questionType ) {
        
        Map<String, Stack<TestQuestion>> questionTypeMap = null ;
        questionTypeMap = questionMap.get( topicId ) ;
        if( questionTypeMap == null ) {
            questionTypeMap = new HashMap<>() ;
            questionMap.put( topicId, questionTypeMap ) ;
        }
        
        Stack<TestQuestion> questions = questionTypeMap.get( questionType ) ;
        if( questions == null ) {
            questions = new Stack<>() ;
            List<TestQuestion> qList = tqRepo.findActiveQuestionsForTopicAndType( 
                                                     topicId, questionType ) ;
            Collections.shuffle( qList ) ;
            questions.addAll( qList ) ;
            questionTypeMap.put( questionType, questions ) ;
        }
        return questions ;
    }
}
