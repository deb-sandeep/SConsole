function TextFormatter( $sce ) {

	this.format = function( inputText ) {

		// Why? Because once the input text is fomatted via $sce, it is transformed
		// into an object. There is no point in trying to process it again. This
		// scenario is relevant when the notes is being recomputed based on
		// user selected filter criteria.
		if( typeof inputText != 'string' ) {
			return inputText ;
		}

		var regexp = /{{([^{]*)}}/g ;
		var formattedStr = inputText ;
		var match = regexp.exec( inputText ) ;

		while( match != null ) {

			var handleBarContents  = match[1].match(/\S+/g) ;
			var hint = handleBarContents[0] ;
			
			handleBarContents.shift() ;
			var parameters = handleBarContents ;

			var replacementContent = getReplacementContent( hint, parameters ) ;
			formattedStr = formattedStr.replace( match[0], replacementContent ) ;

			match = regexp.exec( inputText ) ;
		}

		var formattedObj = formattedStr ;
		if( $sce != null ) {
			formattedObj = $sce.trustAsHtml( formattedStr ) ;
		}
		return P( { innerHTML : formattedObj } ) ;
	}

	function getReplacementContent( hint, parameters ) {

		var hintValue = parameters.join( ' ' ) ;

		var replacementContent = "[[ COULD NOT SUBSTITUTE " + hint + 
		                         " - " + parameters + " ]]" ;
		if( hint == "@img" ) {
			replacementContent = "<img src='/jeetest/images/" + hintValue + "'/>" ;
		}
		return replacementContent ;
	}
}
