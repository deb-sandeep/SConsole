function AttemptState(){}

AttemptState.prototype.NOT_VISITED               = "q-not-visited" ;
AttemptState.prototype.NOT_ANSWERED              = "q-not-answered" ;
AttemptState.prototype.ATTEMPTED                 = "q-attempted" ;
AttemptState.prototype.MARKED_FOR_REVIEW         = "q-marked-for-review" ;
AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW = "q-ans-and-marked-for-review" ;

function QuestionEx( q, attemptLaps ) {
	
	this.index = 0 ;
	this.question = q ;
	this.timeSpent = 0 ;
	this.prevQuestion = null ;
	this.nextQuestion = null ;
	this.attemptState = AttemptState.prototype.NOT_VISITED ;
	this.interactionHandler = null ;
	this.rootCause = null ;
	this.lapDetails = {} ;
	
	var graceAwarded = false ;
	var graceScore = 0 ;
	
	// --------- Initialization ---------------
	initialize( this ) ;
	// ----------------------------------------

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
    		
    	    if( this.question.questionType == "SCA" ) {
    	        return this.getSCAScore() ;
    	    }
    	    else if( this.question.questionType == "NT" ) {
    	        return this.getNTScore() ;
    	    }
    	}
    	return 0 ;
    }
    
    this.getSCAScore = function() {
        if( this.question.answerText == this.interactionHandler.getAnswer() ) {
            return 4 ;
        }
        else {
            return -1 ;
        }
    }
    
    this.getNTScore = function() {
        var providedAnsText = this.interactionHandler.getAnswer() ;
        var answerRange = this.question.answerText ;
        
        var upperBound = null ;
        var lowerBound = null ;
        
        var rangeSepIndex = answerRange.indexOf( '~' ) ;
        if( rangeSepIndex != -1 ) {
            var lbText = answerRange.substring( 0, rangeSepIndex ) ;
            var ubText = answerRange.substring( rangeSepIndex+1 ) ;
            
            upperBound = parseFloat( ubText ) ;
            lowerBound = parseFloat( lbText ) ;
        }
        else {
            var val = parseFloat( answerRange ) ;
            upperBound = val + 0.1 ;
            lowerBound = val - 0.1 ;
        }
        
        var ansValue = parseFloat( providedAnsText ) ;
        if( ansValue >= lowerBound && ansValue <= upperBound ) {
            return 4 ;
        }
        return 0 ;
    }
    
    this.isCorrectlyAnswered = function() {
    	
    	if( graceAwarded ) {
    		return true ;
    	}
    	
    	if( this.attemptState == AttemptState.prototype.ATTEMPTED ||
    		this.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
    		
    		var score = this.getScore() ;
    		return score == 4 ;
        }
    	
    	return false ;
    }
    
    function initialize( questionEx ) {
        for( var i=0; i<attemptLaps.length; i++ ) {
            questionEx.lapDetails[ attemptLaps[i] ] = {
                timeSpent : 0,
                attemptState : null
            }
        }
    }
}