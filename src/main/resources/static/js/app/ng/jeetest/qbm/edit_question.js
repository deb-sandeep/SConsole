sConsoleApp.controller( 'EditQuestionController', 
		                function( $scope, $http, $sce, $routeParams ) {
	
	$scope.$parent.navBarTitle = "Create / Edit Questions" ;
	$scope.question = null ;
	$scope.lastSavedQuestion = null ;
	$scope.isAutoPreviewOn = true ;
	$scope.formattedContent = null ;
	
	// --- [START] Controller initialization
	
	// First load the dropdown master data from the server. The drop down
	// master data will consist of subjects, topics, books etc.
	$scope.$parent.loadQBMMasterData() ;
	
	loadQuestionForEdit( $routeParams.id ) ;
	
	setInterval( function(){
		if( $scope.isAutoPreviewOn ) {
			generateFormattedTextAndRenderPreview() ;
		}
	}, 3000 ) ;
	
	// --- [END] Controller initialization
	
	// --- [START] Scope functions
	
	$scope.isExistingQuestion = function() {
		return ( $scope.question    != null ) && 
		       ( $scope.question.id > 0   ) ;
	}
	
	$scope.editNewQuestion = function() {
		console.log( "Editing a new quesiton." ) ;
		loadQuestionForEdit( -1 ) ;
	}
	
	$scope.discardChange = function() {
		console.log( "discardChange function called." ) ;
		$scope.question = jQuery.extend(true, {}, $scope.lastSavedQuestion ) ;
	}

	$scope.save = function() {
		console.log( "save function called." ) ;
		if( inputsValidated() ) {
			// First parameter  - boolean - shouldRenderPreview
			// Second parameter - boolean - createNewQuestion 
			saveQuestionOnServer( true, false ) ;
		}
	}

	$scope.saveAndCreateNew = function() {
		console.log( "saveAndCreateNew function called." ) ;
		if( inputsValidated() ) {
			saveQuestionOnServer( false, true ) ;
		}
	}

	$scope.preview = function() {
		console.log( "preview function called." ) ;
		// Get the question text formatted by the server
		// Do the following on response receipt
		// 		Save the formatted text as the question formatted text
		// 		Render the preview of the formatted text.
		generateFormattedTextAndRenderPreview() ;
	}
	// --- [START] Internal Questions
	
	function inputsValidated() {
		console.log( "Validating user inputs." ) ;
		
		var q = $scope.question ;
		var errorsFound = false ;
		
		if( q.subject == null || q.subject.name == null ) {
			errorsFound = true ;
			$scope.$parent.addErrorAlert( "Subject name should be specified." ) ;
		}
		
		if( q.topic == null ) {
			errorsFound = true ;
			$scope.$parent.addErrorAlert( "Topic should be specified." ) ;
		}
		
		if( q.book == null ) {
			errorsFound = true ;
			$scope.$parent.addErrorAlert( "Book should be specified." ) ;
		}
		
		if( q.questionRef == null || 
		    ( typeof q.questionType === 'undefined' ) || 
		    q.questionRef.trim().length == 0 ) {
			errorsFound = true ;
			$scope.$parent.addErrorAlert( "Question Reference should be specified." ) ;
		}
		
		if( q.questionText == null || 
		    ( typeof q.questionText === 'undefined' ) || 
		    q.questionText.trim().length == 0 ) {
			errorsFound = true ;
			$scope.$parent.addErrorAlert( "Question Text should be specified." ) ;
		}

		if( q.answerText == null || 
		    ( typeof q.answerText === 'undefined' ) || 
		    q.answerText.trim().length == 0 ) {
			errorsFound = true ;
			$scope.$parent.addErrorAlert( "Answer Text should be specified." ) ;
		}

		if( errorsFound ) {
			console.log( "Errors found in user inputs." ) ;
			return false ;
		}
		console.log( "No errors found in user inputs." ) ; 
		return true ;
	}
	
	function loadQuestionForEdit( questionId ) {
    	
    	if( typeof questionId === 'undefined' ) {
    		questionId = -1 ;
    	}
    	console.log( "Loading question for edit. ID = " + questionId ) ;
    	
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/TestQuestion/' + questionId )
        .then( 
                function( response ){
                    console.log( "Test Question received." ) ;
                    console.log( response ) ;
                    $scope.question = response.data ;
                    
                    if( questionId == -1 && $scope.lastSavedQuestion != null ) {
                    	$scope.question.subject               = $scope.lastSavedQuestion.subject ;
                    	$scope.question.topic                 = $scope.lastSavedQuestion.topic ;
                    	$scope.question.book                  = $scope.lastSavedQuestion.book ;
                        $scope.question.targetExam            = $scope.lastSavedQuestion.targetExam ;       
                        $scope.question.questionType          = $scope.lastSavedQuestion.questionType ;         
                        $scope.question.lateralThinkingLevel  = $scope.lastSavedQuestion.lateralThinkingLevel ;                 
                        $scope.question.projectedSolveTime    = $scope.lastSavedQuestion.projectedSolveTime ;
                        $scope.question.questionRef           = $scope.lastSavedQuestion.questionRef ;
                    } 
                    
                    if( $scope.question.questionFormattedText == null ) {
                    	$scope.question.questionFormattedText = "" ;
                    }
                    
                    $scope.lastSavedQuestion = jQuery.extend(true, {}, response.data) ;
                    renderPreview( $scope.question.questionFormattedText ) ;
                }, 
                function( error ){
                    console.log( "Error getting Test Question data." ) ;
                    console.log( error ) ;
                    $scope.$parent.addErrorAlert( "Could not fetch question." ) ;
                }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    function saveQuestionOnServer( previewRender, newQuestionEdit ) {
    	
    	console.log( "Saving question on server." ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/TestQuestion', $scope.question )
        .then( 
            function( response ){
                console.log( "Successfully saved question." ) ;
                $scope.question = response.data ;
                $scope.lastSavedQuestion = jQuery.extend(true, {}, response.data); ;
                if( newQuestionEdit ) {
                	$scope.editNewQuestion() ;
                }
                else if( previewRender ) {
                	renderPreview( $scope.question.questionFormattedText ) ;
                }
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
    
	function renderPreview( markupText ) {
		console.log( "Rendering preview with formatted text." ) ;
		$scope.formattedContent = markupText ;
	}
	
	function generateFormattedTextAndRenderPreview() {
		
        if( $scope.question.questionText == null ) {
        	return ;
        }
        
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/FormattedText', $scope.question.questionText )        
        .then( 
            function( response ){
                console.log( "Formatted text received." ) ;
                console.log( response ) ;
                $scope.question.questionFormattedText = response.data.fmtText ;
                renderPreview( response.data.fmtText ) ;
            }, 
            function( error ){
                console.log( "Error getting book details." + error ) ;
                $scope.$parent.addErrorAlert( "Error generating formatted text" ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}
} ) ;