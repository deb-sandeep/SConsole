package com.sandy.sconsole.api.jeetest.config;

import java.util.ArrayList ;
import java.util.List ;

import com.sandy.sconsole.dao.entity.master.TestQuestion ;

public class TestConfiguration {

    private Integer id = -1 ;
    private String examType = null ;
    private List<TestQuestion> phyQuestions  = new ArrayList<>() ;
    private List<TestQuestion> chemQuestions = new ArrayList<>() ;
    private List<TestQuestion> mathQuestions = new ArrayList<>() ;
    
    public Integer getId() {
        return id ;
    }
    public void setId( Integer id ) {
        this.id = id ;
    }
    
    public String getExamType() {
        return examType ;
    }
    public void setExamType( String examType ) {
        this.examType = examType ;
    }
    
    public List<TestQuestion> getPhyQuestions() {
        return phyQuestions ;
    }
    public void setPhyQuestions( List<TestQuestion> phyQuestions ) {
        this.phyQuestions = phyQuestions ;
    }
    
    public List<TestQuestion> getChemQuestions() {
        return chemQuestions ;
    }
    public void setChemQuestions( List<TestQuestion> chemQuestions ) {
        this.chemQuestions = chemQuestions ;
    }
    
    public List<TestQuestion> getMathQuestions() {
        return mathQuestions ;
    }
    public void setMathQuestions( List<TestQuestion> mathQuestions ) {
        this.mathQuestions = mathQuestions ;
    }
}
