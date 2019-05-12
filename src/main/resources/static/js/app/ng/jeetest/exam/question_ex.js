function AttemptState(){}

AttemptState.prototype.NOT_VISITED               = "q-not-visited" ;
AttemptState.prototype.NOT_ANSWERED              = "q-not-answered" ;
AttemptState.prototype.ATTEMPTED                 = "q-attempted" ;
AttemptState.prototype.MARKED_FOR_REVIEW         = "q-marked-for-review" ;
AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW = "q-ans-and-marked-for-review" ;

function QuestionEx( q ) {
	
	this.index = 0 ;
	this.question = q ;
	this.prevQuestion = null ;
	this.nextQuestion = null ;
	this.attemptState = AttemptState.prototype.NOT_VISITED ;

    this.getStatusStyle = function() {
    	return this.attemptState ;
    }
}