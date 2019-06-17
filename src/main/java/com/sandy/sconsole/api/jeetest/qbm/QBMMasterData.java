package com.sandy.sconsole.api.jeetest.qbm;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Topic ;

public class QBMMasterData {
    
    public static final String EXAM_TYPE_MAIN = "MAIN" ;
    public static final String EXAM_TYPE_ADV  = "ADV" ;
    
    public static final String Q_TYPE_SCA = "SCA" ;
    public static final String Q_TYPE_MCA = "MCA" ;
    public static final String Q_TYPE_NT  = "NT" ;
    public static final String Q_TYPE_LCT = "LCT" ;
    public static final String Q_TYPE_MMT = "MMT" ;
    
    public static final String S_TYPE_PHY   = "IIT - Physics" ;
    public static final String S_TYPE_CHEM  = "IIT - Chemistry" ;
    public static final String S_TYPE_MATHS = "IIT - Maths" ;

    public static String[]  targetExams     = { EXAM_TYPE_MAIN, EXAM_TYPE_ADV } ;
    public static String[]  questionTypes   = { Q_TYPE_SCA, Q_TYPE_MCA, Q_TYPE_NT, Q_TYPE_LCT, Q_TYPE_MMT } ;
    public static String[]  subjectNames    = { S_TYPE_PHY, S_TYPE_CHEM, S_TYPE_MATHS } ;
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
