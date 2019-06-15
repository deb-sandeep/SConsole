function SCAInteractionHandler( questionEx, $rootScope ) {
	
	var dom = null ;
	
	var option1 = null ;
	var option2 = null ;
	var option3 = null ;
	var option4 = null ;
	
	var selectedOption = null ;
	
	var helper = new HanlderHelper( questionEx.question.questionFormattedText ) ;
	
	this.getQuestionBodyElement = function() {
		return P( { innerHTML : helper.getQuestionBodyMarkupText() } ) ; 
	}
	
	this.getOptionsBodyElement = function() {
		return P( { innerHTML : helper.getOptionsMarkupText() } ) ;
	}

	this.getUserResponseElement = function() {
		
		if( this.dom == null ) {
			option1 = getOption( "1" ) ;
			option2 = getOption( "2" ) ;
			option3 = getOption( "3" ) ;
			option4 = getOption( "4" ) ;
			
			this.dom = DIV( { 
					id : "sca-response-ui",
				}, 
				TABLE( { width : '100%' },
					TR( 
						TD( option1, " 1 )" ), 
						TD( option2, " 2 )" ),
						TD( option3, " 3 )" ),
						TD( option4, " 4 )" )
					) 
				) 
			) ;	
		}
		return this.dom ;
	} ;
	
	this.clearResponse = function() {
		option1.checked = false ;
		option2.checked = false ;
		option3.checked = false ;
		option4.checked = false ;
		selectedOption = null ;
		questionEx.attemptState = AttemptState.prototype.NOT_ANSWERED ;
	}
	
	this.isAnswered = function() {
		return ( selectedOption != null ) ;
	}
	
	this.getTotalMarks = function() {
		return 4 ;
	}
	
	this.getAnswer = function() {
    	if( questionEx.attemptState == AttemptState.prototype.ATTEMPTED ||
        	questionEx.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
    		return selectedOption ;
       	}
    	return "" ;
	}
	
	function getOption( id ) {
		return INPUT( {
			type : 'radio',
			name : 'sca-option',
			value : id,
			click : function() {
				optionClicked( this ) ;
			}
		} ) ;
	}
	
	function optionClicked( input ) {
		selectedOption = input.value ;
		questionEx.attemptState = AttemptState.prototype.NOT_ANSWERED ;
		$rootScope.$broadcast( 'refreshAttemptSummary', questionEx ) ;
	}
}