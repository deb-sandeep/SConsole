function AttemptState(){}

AttemptState.prototype.NOT_VISITED               = "q-not-visited" ;
AttemptState.prototype.NOT_ANSWERED              = "q-not-answered" ;
AttemptState.prototype.ATTEMPTED                 = "q-attempted" ;
AttemptState.prototype.MARKED_FOR_REVIEW         = "q-marked-for-review" ;
AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW = "q-ans-and-marked-for-review" ;

function QuestionEx( q ) {
	
	this.index = 0 ;
	this.question = q ;
	this.timeSpent = 0 ;
	this.prevQuestion = null ;
	this.nextQuestion = null ;
	this.attemptState = AttemptState.prototype.NOT_VISITED ;
	this.interactionHandler = null ;

    this.getStatusStyle = function() {
    	return this.attemptState ;
    }
    
    this.getResultStatusIconStyle = function() {
    	if( this.attemptState == AttemptState.prototype.NOT_VISITED ) {
    		return "not-visited" ;
    	}
    	else if( this.attemptState == AttemptState.prototype.NOT_ANSWERED ) {
    		return "not-answered" ;
    	}
    	else if( this.attemptState == AttemptState.prototype.ATTEMPTED ) {
    		return "answered" ;
    	}
    	else if( this.attemptState == AttemptState.prototype.MARKED_FOR_REVIEW ) {
    		return "review" ;
    	}
    	else if( this.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
    		return "review-answered"
    	}
    }
    
    this.getTotalMarks = function() {
    	return this.interactionHandler.getTotalMarks() ;
    }
    
    this.getScore = function() {
    	if( this.attemptState == AttemptState.prototype.ATTEMPTED ||
    		this.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
    		
    		if( this.question.answerText == this.interactionHandler.getAnswer() ) {
    			return 4 ;
    		}
    		else {
    			return -1 ;
    		}
    	}
    	return 0 ;
    }
    
    this.isCorrectlyAnswered = function() {
    	if( this.attemptState == AttemptState.prototype.ATTEMPTED ||
    		this.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
    		
    		if( this.question.answerText == this.interactionHandler.getAnswer() ) {
    			return true ;
    		}
    	}
    	return false ;
    }
}