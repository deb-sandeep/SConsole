function QuestionEx( q ) {
	
	this.question = q ;
	this.prevQuestion = null ;
	this.nextQuestion = null ;

    this.getStatusStyle = function() {
    	return "q-not-visited" ;
    }
}