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
	this.rootCause = null ;
	
	var graceAwarded = false ;
	var graceScore = 0 ;

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
    
    this.awardGrace = function( score ) {
    	graceAwarded = true ;
    	graceScore = score ;
    	if( ( this.attemptState != AttemptState.prototype.ATTEMPTED ) ||
    		( this.attemptState != AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) ) {
    		this.attemptState = AttemptState.prototype.ATTEMPTED ;
    	}
    }
    
    this.getTotalMarks = function() {
    	return this.interactionHandler.getTotalMarks() ;
    }
    
    this.getScore = function() {
    	
    	if( graceAwarded ) {
    		return graceScore ;
    	}
    	
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
    	
    	if( graceAwarded ) {
    		return true ;
    	}
    	
    	if( this.attemptState == AttemptState.prototype.ATTEMPTED ||
    		this.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
    		
    		if( this.question.answerText == this.interactionHandler.getAnswer() ) {
    			return true ;
    		}
    	}
    	return false ;
    }
}