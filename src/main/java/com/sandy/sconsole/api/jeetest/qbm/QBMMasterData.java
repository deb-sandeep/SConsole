package com.sandy.sconsole.api.jeetest.qbm;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Topic ;

public class QBMMasterData {

    public static String[]  targetExams     = { "MAIN", "ADV" } ;
    public static String[]  questionTypes   = { "SCA", "MCA", "IT", "RNT", "MMT" } ;
    public static String[]  subjectNames    = { "IIT - Physics", "IIT - Chemistry", "IIT - Maths" } ;
    public static Integer[] lateralLevel    = { 1, 2, 3, 4, 5 } ;
    public static Integer[] approxSolveTime = { 15, 30, 60, 90, 120, 180, 240, 300, 600 } ; 

    private Map<String, List<Topic>> topics = new HashMap<>() ;
    private Map<String, List<Book>>  books  = new HashMap<>() ;
    
    public String[] getTargetExams() {
        return targetExams ;
    }

    public String[] getQuestionTypes() {
        return questionTypes ;
    }
    
    public String[] getSubjectNames() {
        return subjectNames ;
    }
    
    public Map<String, List<Topic>> getTopics() {
        return topics ;
    }
    
    public void setTopics( Map<String, List<Topic>> topics ) {
        this.topics = topics ;
    }
    
    public void addTopic( Topic topic ) {
        List<Topic> topicList = topics.get( topic.getSubject().getName() ) ;
        if( topicList == null ) {
            topicList = new ArrayList<>() ;
            topics.put( topic.getSubject().getName(), topicList ) ;
        }
        topicList.add( topic ) ;
    }
    
    public Map<String, List<Book>> getBooks() {
        return books ;
    }
    
    public void setBooks( Map<String, List<Book>> books ) {
        this.books = books ;
    }
    
    public void addBook( Book book ) {
        List<Book> bookList = books.get( book.getSubject().getName() ) ;
        if( bookList == null ) {
            bookList = new ArrayList<>() ;
            books.put( book.getSubject().getName(), bookList ) ;
        }
        bookList.add( book ) ;
    }
    
    public Integer[] getLateralLevel() {
        return lateralLevel ;
    }
    
    public Integer[] getApproxSolveTime() {
        return approxSolveTime ;
    }
}
