package com.sandy.sconsole.api.burncalibration;

import java.util.List ;

public class ActivationInput {
    
    public static class ProblemActivation {
        private Integer id = null ;
        private Boolean active = false ;
        
        public Integer getId() { return id ; }
        public void setId( Integer id ) { this.id = id ; }
        
        public Boolean getActive() { return active ; }
        public void setActive( Boolean active ) { this.active = active ; }
    }
    
    private Integer topicId = null ;
    private List<ProblemActivation> problemActivations = null ;

    public List<ProblemActivation> getProblemActivations() {
        return problemActivations ;
    }

    public void setProblemActivations(
            List<ProblemActivation> problemActivations ) {
        this.problemActivations = problemActivations ;
    }

    public Integer getTopicId() {
        return topicId ;
    }

    public void setTopicId( Integer topicId ) {
        this.topicId = topicId ;
    }
}
