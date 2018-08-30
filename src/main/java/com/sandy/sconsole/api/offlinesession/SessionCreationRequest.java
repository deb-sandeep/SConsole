package com.sandy.sconsole.api.offlinesession;

import java.util.Date ;
import java.util.List ;

public class SessionCreationRequest {
    
    public static class ProblemOutcome {
        private Integer problemId = null ;
        private String  outcome   = null ;
        private Integer duration  = null ;
        
        public Integer getProblemId() {
            return problemId ;
        }
        public void setProblemId( Integer problemId ) {
            this.problemId = problemId ;
        }
        public String getOutcome() {
            return outcome ;
        }
        public void setOutcome( String outcome ) {
            this.outcome = outcome ;
        }
        public Integer getDuration() {
            return duration ;
        }
        public void setDuration( Integer duration ) {
            this.duration = duration ;
        }
    }

    private String  sessionType = null ;
    private String  subject     = null ;
    private Integer topicId     = null ;
    private Integer bookId      = null ;
    private Integer duration    = null ;
    private Date    startTime   = null ;
    
    private List<ProblemOutcome> outcome = null ;

    public String getSessionType() {
        return sessionType ;
    }

    public void setSessionType( String sessionType ) {
        this.sessionType = sessionType ;
    }

    public String getSubject() {
        return subject ;
    }

    public void setSubject( String subject ) {
        this.subject = subject ;
    }

    public Integer getTopicId() {
        return topicId ;
    }

    public void setTopicId( Integer topicId ) {
        this.topicId = topicId ;
    }

    public Integer getBookId() {
        return bookId ;
    }

    public void setBookId( Integer bookId ) {
        this.bookId = bookId ;
    }

    public Integer getDuration() {
        return duration ;
    }

    public void setDuration( Integer duration ) {
        this.duration = duration ;
    }

    public Date getStartTime() {
        return startTime ;
    }

    public void setStartTime( Date startTime ) {
        this.startTime = startTime ;
    }

    public List<ProblemOutcome> getOutcome() {
        return outcome ;
    }

    public void setOutcome( List<ProblemOutcome> outcome ) {
        this.outcome = outcome ;
    }
}
