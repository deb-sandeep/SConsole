package com.sandy.sconsole.api.jeetest.config;

import java.util.ArrayList ;
import java.util.List ;

import com.sandy.sconsole.dao.entity.TestConfigIndex ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;

public class TestConfiguration {

    private Integer id = -1 ;
    private String examType = null ;
    private TestConfigIndex tci = null ;
    
    private List<TestQuestion> phyQuestions  = new ArrayList<>() ;
    private List<TestQuestion> chemQuestions = new ArrayList<>() ;
    private List<TestQuestion> mathQuestions = new ArrayList<>() ;
    
    // Why the convoluted structure? To ensure minimal disruption to existing
    // logic. 
    
    // The section names are in ascending order of the section indexes.    
    private List<String> phySectionNames  = new ArrayList<>() ;
    private List<String> chemSectionNames = new ArrayList<>() ;
    private List<String> mathSectionNames = new ArrayList<>() ;
    
    private List<List<Integer>> phySecQuestionIndices  = new ArrayList<>() ;
    private List<List<Integer>> chemSecQuestionIndices = new ArrayList<>() ;
    private List<List<Integer>> mathSecQuestionIndices = new ArrayList<>() ;
    
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
    
    public void setTestConfigIndex( TestConfigIndex tci ) {
        this.tci = tci ;
    }
    public TestConfigIndex getTestConfigIndex() {
        return this.tci ;
    }
    
    public List<String> getPhySectionNames() {
        return phySectionNames ;
    }
    public void setPhySectionNames( List<String> phySectionNames ) {
        this.phySectionNames = phySectionNames ;
    }
    
    public List<String> getChemSectionNames() {
        return chemSectionNames ;
    }
    public void setChemSectionNames( List<String> chemSectionNames ) {
        this.chemSectionNames = chemSectionNames ;
    }
    
    public List<String> getMathSectionNames() {
        return mathSectionNames ;
    }
    public void setMathSectionNames( List<String> mathSectionNames ) {
        this.mathSectionNames = mathSectionNames ;
    }
    
    public List<List<Integer>> getPhySecQuestionIndices() {
        return phySecQuestionIndices ;
    }
    public void setPhySecQuestionIndices(
            List<List<Integer>> phySecQuestionIndices ) {
        this.phySecQuestionIndices = phySecQuestionIndices ;
    }
    
    public List<List<Integer>> getChemSecQuestionIndices() {
        return chemSecQuestionIndices ;
    }
    public void setChemSecQuestionIndices(
            List<List<Integer>> chemSecQuestionIndices ) {
        this.chemSecQuestionIndices = chemSecQuestionIndices ;
    }
    
    public List<List<Integer>> getMathSecQuestionIndices() {
        return mathSecQuestionIndices ;
    }
    public void setMathSecQuestionIndices(
            List<List<Integer>> mathSecQuestionIndices ) {
        this.mathSecQuestionIndices = mathSecQuestionIndices ;
    }
}
