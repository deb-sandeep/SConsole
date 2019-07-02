package com.sandy.sconsole.api.jeetest.qbm.helper;

import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.Iterator ;
import java.util.List ;

import com.sandy.common.util.StringUtil ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.repository.TestQuestionBindingRepository ;
import com.sandy.sconsole.dao.repository.master.TestQuestionRepository ;

public class TestQuestionSearchEngine {

    private TestQuestionRepository questionRepo = null ;
    private TestQuestionBindingRepository tqbRepo = null ;
    
    public TestQuestionSearchEngine( TestQuestionRepository questionRepo,
                                     TestQuestionBindingRepository tqbRepo ) {
        this.questionRepo = questionRepo ;
        this.tqbRepo = tqbRepo ;
    }
    
    public List<TestQuestion> search( String[] subjects,
                                      String[] selectedQuestionTypes,
                                      Boolean showOnlyUnsynched,
                                      Boolean excludeAttempted,
                                      String searchText,
                                      Integer testConfigId,
                                      Integer[] topicIds,
                                      Integer[] bookIds )
        throws Exception {
        
        List<TestQuestion> results = null ;
        
        if( testConfigId != null && testConfigId > 0 ) {
            results = tqbRepo.getTestQuestionsForTestConfig( testConfigId ) ;
        }
        else {
            if( showOnlyUnsynched ) {
                results = questionRepo.findUnsynched() ;
            }
            else if( bookIds != null && bookIds.length > 0 ) {
                results = questionRepo.findForBooks( bookIds ) ;
            }
            else if( topicIds != null && topicIds.length > 0 ) {
                results = questionRepo.findForTopics( topicIds ) ;
            }
            else if( selectedQuestionTypes != null && selectedQuestionTypes.length > 0 ) {
                results = questionRepo.findForQuestionTypes( selectedQuestionTypes ) ;
            }
            else if( excludeAttempted ) {
                results = questionRepo.findUnattempted() ;
            }
            else {
                results = new ArrayList<>() ;
                for( TestQuestion q : questionRepo.findAll() ) {
                    results.add( q ) ;
                }
            }
            
            filterBySubjects( subjects, results ) ;
            filterByQuestionTypes( selectedQuestionTypes, results ) ;
            filterSynched( results, showOnlyUnsynched ) ;
            filterAttempted( results, excludeAttempted ) ;
            filterByTopics( topicIds, results ) ;
            filterByBooks( bookIds, results ) ;
            filterBySearchText( searchText, results ) ;
        }
        
        return results ;
    }
    
    private void filterBySubjects( String[] subjects, List<TestQuestion> results ) {
        
        if( subjects == null || subjects.length == 0 ) { return ; }
        
        List<String> testSubset = Arrays.asList( subjects ) ;
        
        Iterator<TestQuestion> iter = results.iterator() ;
        while( iter.hasNext() ) {
            TestQuestion q = iter.next() ;
            if( !testSubset.contains( q.getSubject().getName() ) ) {
                iter.remove() ;
            }
        }
    }
    
    private void filterByQuestionTypes( String[] questionTypes, List<TestQuestion> results ) {
        
        if( questionTypes == null || questionTypes.length == 0 ) { return ; }
        
        List<String> testSubset = Arrays.asList( questionTypes ) ;
        
        Iterator<TestQuestion> iter = results.iterator() ;
        while( iter.hasNext() ) {
            TestQuestion q = iter.next() ;
            if( !testSubset.contains( q.getQuestionType() ) ) {
                iter.remove() ;
            }
        }
    }

    private void filterSynched( List<TestQuestion> results, 
                                Boolean showOnlyUnsynched ) {
        
        if( showOnlyUnsynched != null && !showOnlyUnsynched ) return ;
        
        Iterator<TestQuestion> iter = results.iterator() ;
        while( iter.hasNext() ) {
            TestQuestion q = iter.next() ;
            if( q.getSynched() ) {
                iter.remove() ;
            }
        }
    }

    private void filterAttempted( List<TestQuestion> results, 
                                  Boolean excludeAttempted ) {
        
        if( excludeAttempted != null && !excludeAttempted ) return ;
        
        Iterator<TestQuestion> iter = results.iterator() ;
        while( iter.hasNext() ) {
            TestQuestion q = iter.next() ;
            if( q.getAttempted() ) {
                iter.remove() ;
            }
        }
    }
    
    private void filterByTopics( Integer[] topicIds, 
                                 List<TestQuestion> results ) {
        
        if( topicIds == null || topicIds.length == 0 ) { return ; }
        
        List<Integer> testSubset = Arrays.asList( topicIds ) ;
        
        Iterator<TestQuestion> iter = results.iterator() ;
        while( iter.hasNext() ) {
            TestQuestion q = iter.next() ;
            if( !testSubset.contains( q.getTopic().getId() ) ) {
                iter.remove() ;
            }
        }
    }

    private void filterByBooks( Integer[] bookIds, 
                                List<TestQuestion> results ) {
        
        if( bookIds == null || bookIds.length == 0 ) { return ; }
        
        List<Integer> testSubset = Arrays.asList( bookIds ) ;
        
        Iterator<TestQuestion> iter = results.iterator() ;
        while( iter.hasNext() ) {
            TestQuestion q = iter.next() ;
            if( !testSubset.contains( q.getBook().getId() ) ) {
                iter.remove() ;
            }
        }
    }

    private void filterBySearchText( String searchText, 
                                     List<TestQuestion> results ) {
    
        if( StringUtil.isEmptyOrNull( searchText ) ) return ;
        
        Iterator<TestQuestion> iter = results.iterator() ;
        while( iter.hasNext() ) {
            TestQuestion q = iter.next() ;
            if( !q.getQuestionText().contains( searchText ) ) {
                iter.remove() ;
            }
        }
    }
}
