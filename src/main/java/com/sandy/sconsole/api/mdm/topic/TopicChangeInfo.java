package com.sandy.sconsole.api.mdm.topic;

import java.sql.Timestamp ;
import java.util.List ;

public class TopicChangeInfo {
    
    public static class ChangeInfo {
        
        private Integer topicId = null ;
        private Timestamp startDay = null ;
        private Timestamp endDay = null ;
        private Boolean active = null ;
        
        public Integer getTopicId() { return topicId ; }
        public void setTopicId( Integer topicId ) {
            this.topicId = topicId ;
        }
        
        public Timestamp getStartDay() { return startDay ; }
        public void setStartDay( Timestamp startDay ) {
            this.startDay = startDay ;
        }
        
        public Timestamp getEndDay() { return endDay ; }
        public void setEndDay( Timestamp endDay ) {
            this.endDay = endDay ;
        }
        
        public Boolean getActive() { return active ; }
        public void setActive( Boolean active ) {
            this.active = active ;
        }
    }
    
    private List<ChangeInfo> changedTopics = null ;

    public List<ChangeInfo> getChangedTopics() {
        return changedTopics ;
    }

    public void setChangedTopics( List<ChangeInfo> changedTopics ) {
        this.changedTopics = changedTopics ;
    }
}
