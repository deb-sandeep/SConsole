package com.sandy.sconsole.api.jeetest.qbm;

import java.util.List ;

import com.sandy.sconsole.dao.entity.master.Book ;
import com.sandy.sconsole.dao.entity.master.Topic ;

public class TestQuestionSearchCriteria {

    private List<String> selectedSubjects = null ;
    private List<Topic> selectedTopics = null ;
    private List<Book> selectedBooks = null ;
    private List<String> selectedQuestionTypes = null ;
    private Boolean showOnlyUnsynched = false ;
    private Boolean excludeAttempted = true ;
    private String searchText = "" ;
    
    public List<String> getSelectedSubjects() {
        return selectedSubjects ;
    }
    
    public void setSelectedSubjects( List<String> selectedSubjects ) {
        this.selectedSubjects = selectedSubjects ;
    }
    
    public List<Topic> getSelectedTopics() {
        return selectedTopics ;
    }
    
    public void setSelectedTopics( List<Topic> selectedTopics ) {
        this.selectedTopics = selectedTopics ;
    }
    
    public List<Book> getSelectedBooks() {
        return selectedBooks ;
    }
    
    public void setSelectedBooks( List<Book> selectedBooks ) {
        this.selectedBooks = selectedBooks ;
    }
    
    public List<String> getSelectedQuestionTypes() {
        return selectedQuestionTypes ;
    }
    
    public void setSelectedQuestionTypes( List<String> selectedQuestionTypes ) {
        this.selectedQuestionTypes = selectedQuestionTypes ;
    }
    
    public Boolean getShowOnlyUnsynched() {
        return showOnlyUnsynched ;
    }
    
    public void setShowOnlyUnsynched( Boolean showOnlyUnsynched ) {
        this.showOnlyUnsynched = showOnlyUnsynched ;
    }
    
    public Boolean getExcludeAttempted() {
        return excludeAttempted ;
    }
    
    public void setExcludeAttempted( Boolean excludeAttempted ) {
        this.excludeAttempted = excludeAttempted ;
    }
    
    public String getSearchText() {
        return searchText ;
    }
    
    public void setSearchText( String searchText ) {
        this.searchText = searchText ;
    }
}
