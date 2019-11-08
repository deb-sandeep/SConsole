function MCAInteractionHandler( questionEx, $rootScope ) {
	
	var dom = null ;
	
	var option1 = null ;
	var option2 = null ;
	var option3 = null ;
	var option4 = null ;
	
	var selectedOptions = [0, 0, 0, 0] ;
	
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
					id : "mca-response-ui",
				}, 
				TABLE( { width : '100%' },
					TR( 
						TD( option1, " A )" ), 
						TD( option2, " B )" ),
						TD( option3, " C )" ),
						TD( option4, " D )" )
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
		selectedOptions.length = 0 ;
		questionEx.attemptState = AttemptState.prototype.NOT_ANSWERED ;
        $rootScope.$broadcast( 'refreshAttemptSummary', questionEx ) ;
	}
	
	this.isAnswered = function() {
		return selectedOptions.includes( 1 ) ;
	}
	
	this.getTotalMarks = function() {
		return 4 ;
	}
	
	this.getAnswer = function() {
	    var ans = "" ;
    	if( questionEx.attemptState == AttemptState.prototype.ATTEMPTED ||
        	questionEx.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
    	    
    	    for( var i=0; i<selectedOptions.length; i++ ) {
    	        if( selectedOptions[i] == 1 ) {
    	            ans += (i+1) + "," ;
    	        }
    	    }
    	    
    	    if( ans.length > 0 ) {
    	        ans = ans.substring( 0, ans.length-1 ) ;
    	    }
       	}
    	return ans ;
	}
	
	function getOption( id ) {
		return INPUT( {
			type : 'checkbox',
			name : 'mca-option',
			value : id,
			click : function() {
				optionClicked( this ) ;
			}
		} ) ;
	}
	
	function optionClicked( input ) {
	    var index = parseInt( input.value ) -1 ;
	    if( input.checked ) {
	        selectedOptions[ index ] = 1 ;
	    }
	    else {
	        selectedOptions[ index ] = 0 ;
	    }
		questionEx.attemptState = AttemptState.prototype.NOT_ANSWERED ;
		$rootScope.$broadcast( 'refreshAttemptSummary', questionEx ) ;
	}
}