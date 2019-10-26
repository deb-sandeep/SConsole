function NTUserInterface( handler ) {
	
	var textField = null ;
	
	this.uiDOM = null ;
	
	this.initialize = function() {
	    
		this.uiDOM = DIV( { align : 'left', id : '_ntui' },
			this.getTableDOM() 
		 ) ;
	}
	
	this.getUserInputValue = function() {
	    return textField.value ;
	}
	
	this.clearUserInput = function() {
	    textField.value = "" ;
	}
	
	this.getTableDOM = function() {
	    
		var tableDOM = TABLE( { class : 'ntui_table' } ) ;
		var tBody = tableDOM.createTBody() ;
		
		this.appendTextFieldRow( tBody ) ;
		this.appendSpanningButtonRow( tBody, "Backspace" ) ;
		this.appendNumeralRows( tBody, [
			[ '7', '8', '9' ],
			[ '4', '5', '6' ],
			[ '1', '2', '3' ],
			[ '0', '.', '-' ]
		]) ;
        this.appendSpanningButtonRow( tBody, "Clear" ) ;
		
		return tableDOM ;
	}
	
	this.appendTextFieldRow = function( tBody ) {
	    
		var tr = tBody.insertRow() ;
		var cell = tr.insertCell() ;
		
		textField = INPUT( {
			class     : "ntui_textfield",
			type      : "text",
			size      : "10",
			maxlength : "10",
			disabled  : "true"
		} ) ;
		
		cell.colSpan = 3 ;
		cell.appendChild( textField ) ;
	}
	
	this.appendSpanningButtonRow = function( tBody, actionCmd ) {

		var tr = tBody.insertRow() ;
		var cell = tr.insertCell() ;
		var handler = this.handleSpanningBtnClick ;
		
		var button = BUTTON( { class : "btn btn-default ntui_btn" } ) ;
		button.innerHTML = actionCmd ;
		button.onclick = function() {
		    handler( actionCmd ) ;
		} ;
		
		cell.colSpan = 3 ;
		cell.appendChild( button ) ;
	}
	
	this.appendNumeralRows = function( tBody, meta ) {
		
		for( var row=0; row<meta.length; row++ ) {
			var tr = tBody.insertRow() ;
			for( var col=0; col<meta[row].length; col++ ) {
				var cell = tr.insertCell() ;
				
				var button = BUTTON( { class : "btn btn-default ntui_btn" } ) ;
				button.innerHTML = meta[row][col] ;
				
				this.attachEventHandler( button, meta[row][col] ) ;
				
				cell.appendChild( button ) ;
			}
		}
	}
	
	this.attachEventHandler = function( button, keyVal ) {
	    
		var onclickCallbackFn = this.handleNumericButtonPress ;
		button.onclick = function() {
			onclickCallbackFn( keyVal ) ;
		}
	}
	
	this.handleSpanningBtnClick = function( actionCmd ) {
		if( actionCmd == "Backspace" ) {
		    var value = textField.value ;
		    if( value.length > 0 ) {
		        value = value.substring( 0, value.length-1 ) ;
		        textField.value = value ;
		        handler.answerChanged( value ) ;
		    }
		}
		else if( actionCmd == "Clear" ) {
		    textField.value = "" ;
		    handler.answerChanged( value ) ;
		} 
	}
	
	this.handleNumericButtonPress = function( keyCode ) {
		var value = textField.value ;
		if( keyCode == '.' ) {
		    if( value.indexOf( '.' ) == -1 ) {
		        value += '.' ;
		    }
		}
		else if( keyCode == "-" ) {
		    if( value.length > 0 ) {
		        if( value.charAt(0) == '-' ) {
		            value = value.substring( 1 ) ;
		        }
		        else {
		            value = '-' + value ;
		        }
		    }
		}
		else {
		    value += keyCode ;
		}
		
		if( value != textField.value ) {
		    textField.value = value ;
		    handler.answerChanged( value ) ;
		}
	}
}

function NTInteractionHandler( questionEx, $rootScope ) {
	
	var helper = new HanlderHelper( questionEx.question.questionFormattedText ) ;
	var ui = new NTUserInterface( this ) ;
	var providedAnswer = null ;
	
	ui.initialize() ;
	
	this.getQuestionBodyElement = function() {
		return P( { innerHTML : helper.getQuestionBodyMarkupText() } ) ; 
	}
	
	this.getOptionsBodyElement = function() {
		return P( { innerHTML : helper.getOptionsMarkupText() } ) ;
	}

	this.getUserResponseElement = function() {
		return ui.uiDOM ;
	} ;
	
	this.clearResponse = function() {
		questionEx.attemptState = AttemptState.prototype.NOT_ANSWERED ;
		ui.clearUserInput() ;
	}
	
	this.answerChanged = function( newValue ) {
        questionEx.attemptState = AttemptState.prototype.NOT_ANSWERED ;
        providedAnswer = newValue ;
        $rootScope.$broadcast( 'refreshAttemptSummary', questionEx ) ;
	}
	
	this.isAnswered = function() {
	    var ansProvided = ( providedAnswer != null ) && 
                          ( providedAnswer.trim() != "" ) ;
	    if( ansProvided ) {
	        var numVal = parseFloat( providedAnswer.trim() ) ;
	        if( isNaN( providedAnswer ) ) {
	            return false ;
	        }
	        return true ;
	    }
		return false ;
	}
	
	this.getTotalMarks = function() {
		return 4 ;
	}
	
	this.getAnswer = function() {
    	if( questionEx.attemptState == AttemptState.prototype.ATTEMPTED ||
        	questionEx.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
    		return providedAnswer ;
       	}
    	return null ;
	}
}