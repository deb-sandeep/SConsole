function HanlderHelper( fmtText ) {
	
	var bodyMarkup = "" ;
	var optionsMarkup = "" ;
	var optionMarkups = [] ;
	
	extractParts( fmtText ) ;
	
	this.getQuestionBodyMarkupText = function() {
		return bodyMarkup ;
	}
	
	this.getOptionsMarkupText = function() {
		return optionsMarkup ;
	}
	
	this.getOptionMarkupTexts = function() {
		return optionMarkups ;
	}
	
	function extractParts() {
		
		var optionContainer = "td" ;
		var index = fmtText.indexOf( "<table class='option-table-" ) ;
		if( index < 0 ) {
			optionContainer = null ;
			index = fmtText.indexOf( "<ol>" ) ;
			if( index >= 0 ) {
				optionContainer = "li" ;
			}
		}
		
		if( index >= 0 ) {
			bodyMarkup = fmtText.substring( 0, index ) ;
			optionsMarkup = fmtText.substring( index, fmtText.length-1 ) ;
			extractOptionMarkups( optionsMarkup, optionContainer ) ;
		}
		else {
			bodyMarkup = fmtText ;
		}
	}
	
	function extractOptionMarkups( optText, container ) {
		var startTag = "<" + container + ">" ;
		var endTag = "</" + container + ">" ;
		
		var startIndex = optText.indexOf( startTag, 0 ) ;
		while( startIndex > 0 ) {
			var endIndex = optText.indexOf( endTag, startIndex ) ;
			var optionText = optText.substring( startIndex + startTag.length, endIndex ) ;
			
			optionMarkups.push( optionText ) ;
			
			startIndex = optText.indexOf( startTag, endIndex + endTag.length ) ;
		}
	}
}