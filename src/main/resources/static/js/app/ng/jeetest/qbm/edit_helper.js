function EditHelper() {
	
	this.constructQImagePath = function( isFullText ) {
		
		var subjectName = $scope.question.topic.subject.name ;
		
		var path = subjectName + "/" +
		           $scope.question.topic.topicName + "/" +
				   $scope.question.book.bookShortName + "/" ;
		
		var imgName = "" ;
		if( subjectName == "IIT - Chemistry" ) {
			imgName = "Chem_" ;
		}
		else if( subjectName == "IIT - Physics" ) {
			imgName = "Phy_" ;
		}
		else if( subjectName == "IIT - Maths" ) {
			imgName = "Math_" ;
		}
		
		if( isFullText ) {
			imgName += "Q_" ;
		}
		
		if( $scope.question.questionRef != null ) {
			imgName += $scope.question.questionRef.split( "/" ).join( "_" ) ;
			imgName += ".png" ;
			
			path += imgName ;
			return path ;
		}
		return null ;
	}
	
	this.isAnswerSemanticallyValid = function( ansText, qType, validationErrors ) {
		if( qType == "SCA" || qType == "MMT" ) {
			return checkValueIsIntegerAndBetween1to4( ansText, validationErrors ) ;
		}
		else if( qType == "MCA" ) {
			// Check if multiple answers are provided separated by comma
			// Check each part is numeric and in [1,4]
			// Check there are no duplicates
			return checkMultipleOptions( ansText, validationErrors ) ;
		}
		else if( qType == "NT" ) {
			// 1. If the answer contains a - in the middle, it implies that a 
			//    range is specified. If so validate that each part of the range
			//    is a number and the first part is less than the second
			// 2. Check if answer is a number
			return checkNumericType( ansText, validationErrors ) ;
		}
		else if( qType == "LCT" ) {
			// If the answer contains comma, it implies that it is a multiple
			// choice answer. If so, the MCA rules appy
			if( ansText.indexOf( "," ) > 0 ) {
				return checkMultipleValues( ansText, validationErrors ) ;
			}
			else {
				// In case no comma is there, SCA rules apply
				return checkValueIsIntegerAndBetween1to4( ansText, validationErrors ) ;
			}
		}
		
		return true ;
	}
	
	this.nextQRef = function( curQRef ) {
		var splitChar = '.' ;
		var splitIdx = curQRef.lastIndexOf( '.' ) ;
		
		if( splitIdx == -1 ) {
			splitChar = '/' ;
			splitIdx = curQRef.lastIndexOf( '/' ) ;
			if( splitIdx == -1 ) {
				return curQRef ;
			}
		}
		
		var prefix = curQRef.substring( 0, splitIdx ) ;
		var strCounter = curQRef.substring( splitIdx + 1 ) ;
		
		if( isNaN( strCounter ) ) {
			return curQRef ;
		}
		var counter = parseInt( strCounter ) ;
		var newQRef = prefix + splitChar + (++counter).toString() ;
		
		return newQRef ;
	}

	// --------------- Internal functions -----------------------------------
	
	function checkValueIsIntegerAndBetween1to4( text, validationErrors ) {
		
		if( text.trim() == "" ) {
			validationErrors.push( "Answer can not be blank" ) ;
			return false ;
		}
		
		if( isNaN( text ) ) {
			validationErrors.push( "Answer should be an integer" ) ;
			return false ;
		}
		
		var val = parseInt( text.trim() ) ;
		if( val === NaN ) {
			validationErrors.push( "Answer should be an integer between 1-4" ) ;
			return false ;
		}
		else {
			if( val < 1 || val > 4 ) {
				validationErrors.push( "Answer should be an integer between 1-4" ) ;
				return false ;
			}
		}
		return true ;
	}
	
	function checkMultipleOptions( text, validationErrors ) {
		var parts = text.split( "," ) ;
        for( var i=0; i<parts.length; i++ ) {
            if( !checkValueIsIntegerAndBetween1to4( parts[i], validationErrors ) ) {
                return false ;
            }
        }
		return true ;
	}
	
	function checkNumericType( text, validationErrors ) {
		var parts = text.split( "~" ) ;
		if( parts.length > 2 ) {
			validationErrors.push( "Answer is NT but has more than two range markers" ) ;
			return false ;
		}
		else {
			for( var i=0; i<parts.length; i++ ) {
				if( isNaN( parts[i].trim() ) ) {
					validationErrors.push( "Answer or (part) is not a number." ) ;
					return false ;
				}
				
				if( parseFloat( parts[i].trim() ) == NaN ) {
					validationErrors.push( "NT answer (or part) is not a number." ) ;
					return false ;
				}
			}
		}
		return true ;
	}
}