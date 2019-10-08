function BulkQEntry( entry ) {
	this.qRef     = entry.qRef ;
	this.qType    = entry.qType ;
	this.aText    = entry.aText ;
	this.latLevel = entry.latLevel ;
	this.projTime = entry.projTime ;
	this.saved    = entry.saved ;
	this.imgNames = entry.imgNames ;
	this.imgPaths = entry.imgPaths ;
	this.hidden   = false ;
	
	this.nextEntry = null ;
}

sConsoleApp.controller( 'BulkEditController', 
		                function( $scope, $http, $sce, $routeParams, $window, $timeout ) {
	
	var editHelper = new EditHelper() ;
	
	$scope.$parent.navBarTitle = "Create Questions in Bulk" ;
	$scope.validationErrors = [] ;
	$scope.baseInput = {
		subjectName : "",
		topic : null,
		book : null,
		baseQRef : ""
	} ;
	
	$scope.entries = [] ;
	$scope.hideSaved = true ;
	
	// --- [START] Controller initialization
	
	// First load the drop down master data from the server. The drop down
	// master data will consist of subjects, topics, books etc.
	$scope.$parent.loadQBMMasterData() ;
	
	// --- [END] Controller initialization
	
	// --- [START] Scope functions
	$scope.applyBaseCriteria = function() {
		$scope.validationErrors.length = 0 ;
		validateBaseCriteria() ;
		
		if( $scope.validationErrors.length > 0 ) {
			$( '#validationErrorsDialog' ).modal( 'show' ) ;
		}
		else {
			getBulkQuestionMetaData() ;
		}
	}
	
	$scope.closeValidationErrorsDialog = function() {
		$scope.validationErrors.length = 0 ;
		$( '#validationErrorsDialog' ).modal( 'hide' ) ;
	}
	
	$scope.saveEntry = function( entry ) {
		$scope.validationErrors.length = 0 ;
		if( editHelper.isAnswerSemanticallyValid( entry.aText, entry.qType, 
				                                  $scope.validationErrors ) ) {
			saveEntryOnServer( entry ) ;
		}
		else {
			for( var i=0; i<$scope.validationErrors.length; i++ ) {
				$scope.validationErrors[i] = entry.qRef + " : " + $scope.validationErrors[i] ;
			}
			$( '#validationErrorsDialog' ).modal( 'show' ) ;
		}
	}
	
	$scope.deleteBulkEntryMetaData = function( entryIndex ) {
		
    	console.log( "Deleting bulk entry meta data." ) ;

    	var entry = $scope.entries[ entryIndex ] ;
    	
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/DeleteTestQuestionImages', entry.imgPaths )
        .then( 
            function( response ){
                console.log( "Successfully deleted question images." ) ;
                $scope.entries.splice( entryIndex, 1 ) ;
            }, 
            function( error ){
                console.log( "Error deleting question images." + error ) ;
                var errMsg = "Server error." ;
                $scope.$parent.addErrorAlert( "Could not delete question images. " + errMsg ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}
	
	$scope.qTypeChanged = function( entry ) {
		var nextEntry = entry.nextEntry ;
		var projTime = 120 ;
		
		if( $scope.baseInput.subjectName == "IIT - Chemistry" ) {
			if( entry.qType == 'MCA' ) {
				projTime = 180 ;
			}
			else if ( entry.qType == 'NT' ) {
				projTime = 180 ;
			}
		}
		else {
			if( entry.qType == 'MCA' ) {
				projTime = 180 ;
			}
			else if ( entry.qType == 'NT' ) {
				projTime = 240 ;
			}
		}
		
		entry.projTime = projTime ;
		while( nextEntry != null ) {
			nextEntry.qType = entry.qType ;
			nextEntry.projTime = projTime ;
			nextEntry = nextEntry.nextEntry ;
		}
	}
	
	$scope.answerEntered = function( entry ) {
	    if( entry.aText.indexOf( ',' ) != -1 ) {
	        entry.qType = 'MCA' ;
	        entry.projTime = 240 ;
	    }
	    
	    if( entry.aText.includes( '-' ) && 
	        entry.aText.indexOf( '-' ) != 0 ) {
	        
	        var parts = entry.aText.split( '-' ) ;
	        entry.aText = parts[0] ;
	        if( parts.length > 1 ) {
	            entry.latLevel = parseInt( parts[1] ) ;
	        }
	        if( parts.length > 2 ) {
	            entry.projTime = parseInt( parts[2] ) ;
	        }
	    }
	}
	
	$scope.hideEntry = function( entry ) {
		entry.hidden = true ;
	}
	
	// --- [START] Internal functions
	function validateBaseCriteria() {
		
		if( $scope.baseInput.subjectName.trim() == "" ) {
			$scope.validationErrors.push( "Subject name should be specified." ) ;
		}
		
		if( $scope.baseInput.topic == null ) {
			$scope.validationErrors.push( "Topic should be specified." ) ;
		}
		
		if( $scope.baseInput.book == null ) {
			$scope.validationErrors.push( "Book should be specified." ) ;
		}
	}

    function getBulkQuestionMetaData() {
    	
        $scope.interactingWithServer = true ;
        $http.get( '/BulkQuestionsEntryMeta', {
        	params:{
        		subjectName : $scope.baseInput.subjectName,
        		topicId : $scope.baseInput.topic.id,
        		bookId : $scope.baseInput.book.id,
        		baseQRef : $scope.baseInput.baseQRef
        	}
        } )
        .then( 
                function( response ){
                    console.log( "Bulk question entry meta data received." ) ;
                    console.log( response ) ;
                    $scope.entries.length = 0 ;
                    var lastEntry = null ;
                    for( var i=0; i<response.data.length; i++ ) {
                    	var newEntry = new BulkQEntry( response.data[i] ) ;
                    	if( lastEntry != null ) {
                    		lastEntry.nextEntry = newEntry ;
                    	}
                        $scope.entries.push( newEntry ) ;
                        lastEntry = newEntry ;
                    }
                }, 
                function( error ){
                    console.log( "Server error" ) ;
                    console.log( error ) ;
                    $scope.addErrorAlert( "Could not load bulk entry meta data." ) ;
                }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
    
    function saveEntryOnServer( entry ) {
    	
    	console.log( "Saving entrh on server." ) ;
    	
    	var qText = "" ;
    	var tgtExam = ( entry.qType == "MCA" || 
    	                entry.qType == "LCT" || 
    	                entry.qType == "MMT" ) ? "ADV" : "MAIN" ;
    	
    	for( var i=0; i<entry.imgPaths.length; i++ ) {
    		qText += "{{@img " + entry.imgPaths[i] + "}}" ;
    		if( i < entry.imgPaths.length - 1 ) {
    			qText += "<br/>" ;
    		}
    	}
    	
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/TestQuestion', {
            subject               : {
            	name : $scope.baseInput.subjectName,
            },
            topic                 : $scope.baseInput.topic,
            book                  : $scope.baseInput.book,
            targetExam            : tgtExam,
            questionType          : entry.qType,
            questionRef           : entry.qRef,
            lateralThinkingLevel  : entry.latLevel,
            projectedSolveTime    : entry.projTime,
            questionText          : qText,
            lctContext            : null,
            questionFormattedText : null,
            answerText            : entry.aText,
            synched               : false,
            attempted             : false,
            creationTime          : null,
            lastUpdateTime        : null
        })
        .then( 
            function( response ){
                console.log( "Successfully saved question." ) ;
                entry.saved = true ;
            }, 
            function( error ){
                console.log( "Error saving question on server." + error ) ;
                var errMsg = "Server error." ;
                if( error.status == 412 ) {
                	errMsg = "Duplicate question found." ;
                }
                $scope.$parent.addErrorAlert( "Could not save question. " + errMsg ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
} ) ;