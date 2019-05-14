sConsoleApp.controller( 'ExamController', function( $scope, $http, $rootScope, $location ) {
	
	$scope.alerts = [] ;
	$scope.navBarTitle = "Landing" ;
	$scope.interactingWithServer = false ;
	
	$scope.paletteHidden = false ;
	$scope.activeTest = null ;
	
	// This is populated with instances of QuestionEx
	$scope.questions = [] ;
	$scope.currentQuestion = null ;
	$scope.secondsRemaining = 0 ;
	$scope.timerActive = false ;
	$scope.answersSubmitted = false ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.resetState = function() {
		$scope.activeTest = null ;
		$scope.alerts.length = 0 ;
		$scope.paletteHidden = false ;
		$scope.questions = [] ;
		$scope.currentQuestion = null ;
		$scope.secondsRemaining = 0 ;
		$scope.timerActive = false ;
	}
	
	$scope.addErrorAlert = function( msgString ) {
	    $scope.alerts.push( { type: 'danger', msg: msgString } ) ;
	} ;
	
	$scope.dismissAlert = function( index ) {
		$scope.alerts.splice( index, 1 ) ;
	}

    $scope.initializeController = function() {
    	if( $scope.activeTest == null ) {
    		$location.path( "/" ) ;
    	}
    	else {
    		// Remove any scrollbars from the viewport - this is a full screen SPA
    		var elements = document.getElementsByTagName( "body" ) ;
    		elements[0].style.overflowY = "hidden" ;
    		
    		// Note that this route can get transitioned in from either the
    		// instructions page or from the submit confirmation page.
    		// In case we are coming in from the instruction page, the 
    		// questions would need to be freshly loaded and the test started
    		// afresh. However, if we are coming back from the submit confirmation
    		// page, we don't have to recreate the full state - only 
    		// the attempt summary and the section indexes.
    		
    		if( $scope.questions.length == 0 ) {
    			loadTestConfiguration() ;
    		}
    		else {
    			$scope.$broadcast( 'computeSectionIndices' ) ;
    			$scope.showQuestion( $scope.questions[0] ) ;
    		}
    	}
    }
    
	$scope.toggleQuestionPalette = function() {
		
	    var palette = document.getElementById( "question-palette-panel" ) ;
	    var display = document.getElementById( "question-display-panel" ) ;
		
		if( $scope.paletteHidden ) {
			palette.style.display = "block" ;
			palette.style.width = "25%" ;
			display.style.width = "75%" ;
			$scope.paletteHidden = false ;
		}
		else {
			palette.style.display = "none" ;
			palette.style.width = "0%" ;
			display.style.width = "100%" ;
			$scope.paletteHidden = true ;
		}
	}
	
	$scope.startTimer = function() {
		$scope.timerActive = true ;
		setTimeout( handleTimerEvent, 1000 ) ;
	}

    $scope.saveAndNext = function() {
    	console.log( "Saving answer and showing next question." ) ;
    	if( $scope.currentQuestion.interactionHandler.isAnswered() ) {
    		$scope.currentQuestion.attemptState = AttemptState.prototype.ATTEMPTED ;
    		$scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    	}
    	else {
    		alert( "Please answer the question before saving." ) ;
    	}
    }
    
    $scope.saveAndMarkForReview = function() {
    	console.log( "Saving answer and marking for review. Showing next question" ) ;
    	if( $scope.currentQuestion.interactionHandler.isAnswered() ) {
        	$scope.currentQuestion.attemptState = AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ;
        	$scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    	}
    	else {
    		alert( "Please answer the question before saving." ) ;
    	}
    }
    
    $scope.clearResponse = function() {
    	console.log( "Clearing response." ) ;
    	$scope.currentQuestion.interactionHandler.clearResponse() ;
    }
    
    $scope.markForReviewAndNext = function() {
    	console.log( "Mark question for review and move to next" ) ;
    	$scope.currentQuestion.attemptState = AttemptState.prototype.MARKED_FOR_REVIEW ;
    	$scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    }
    
    $scope.showQuestion = function( questionEx ) {
    	if( questionEx != null ) {
    		$scope.currentQuestion = questionEx ;
    		if( $scope.currentQuestion.attemptState == AttemptState.prototype.NOT_VISITED ) {
    			$scope.currentQuestion.attemptState = AttemptState.prototype.NOT_ANSWERED ;
    		}
    		$scope.$broadcast( 'refreshAttemptSummary', questionEx ) ;
    	}
    }
    
    $scope.showNextQuestion = function() {
    	$scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    }
    
    $scope.showPreviousQuestion = function() {
    	$scope.showQuestion( $scope.currentQuestion.prevQuestion ) ;
    }
    
    $scope.scrollBottom = function() {
    	scrollToElement( "q_bottom" ) ;
    }
    
    $scope.scrollTop = function() {
    	scrollToElement( "q_top" ) ;
    }
    
    $scope.showQuestionPaper = function() {
    	console.log( "Showing question paper" ) ; 
    }
    
    $scope.hideQuestionPaper = function() {
    	console.log( "Hiding question paper" ) ; 
    }
    
    $scope.submitAnswers = function() {
    	console.log( "Submitting answers." ) ;
    	$scope.timerActive = false ;
    	$location.path( "/testResult" ) ;
    	$scope.answersSubmitted = true ;
    	
		var elements = document.getElementsByTagName( "body" ) ;
		elements[0].style.overflowY = "auto" ;
    }
    
	// --- [END] Scope functions

	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    function loadTestConfiguration() {
    	
        $scope.interactingWithServer = true ;
        $http.get( '/TestConfiguration/' + $scope.activeTest.id )
        .then( 
            function( response ){
                console.log( "Successfully loaded test configuration." ) ;
                console.log( response.data ) ;
                preProcessQuestions( response.data ) ;
                $scope.$broadcast( 'computeSectionIndices' ) ;
                
                $scope.secondsRemaining = $scope.questions.length * 2 * 60 ;
                $scope.startTimer() ;
                $scope.showQuestion( $scope.questions[0] ) ;
            }, 
            function( error ){
                console.log( "Error getting test configuration from server." + error ) ;
                $scope.addErrorAlert( "Could not fetch test." ) ;
            }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
    
    function preProcessQuestions( testConfig ) {
    	
        var phyQuestions  = testConfig.phyQuestions ;
        var chemQuestions = testConfig.chemQuestions ;
        var mathQuestions = testConfig.mathQuestions ;
        
        if( phyQuestions.length > 0 ) {
        	enhanceQuestions( phyQuestions ) ;
        }
        
        if( chemQuestions.length > 0 ) {
        	enhanceQuestions( chemQuestions ) ;
        }
        
        if( mathQuestions.length > 0 ) {
        	enhanceQuestions( mathQuestions ) ;
        }
    }
    
    function enhanceQuestions( questions ) {
    	
    	for( var i=0; i<questions.length; i++ ) {
    		
    		var question = questions[i] ;
    		var questionEx = new QuestionEx( question ) ;
    		var lastQuestionEx = null ;
    		
    		if( $scope.questions.length > 0 ) {
    			lastQuestionEx = $scope.questions[ $scope.questions.length-1 ] ;
    			
    			lastQuestionEx.nextQuestion = questionEx ;
    			questionEx.prevQuestion = lastQuestionEx ;
    		}
    		
    		questionEx.index = $scope.questions.length ;
    		associateInteractionHandler( questionEx ) ;
    		
    		$scope.questions.push( questionEx ) ;
    	}
    }
    
    function associateInteractionHandler( questionEx ) {
    	
    	if( questionEx.question.questionType == "SCA" ) {
    		questionEx.interactionHandler = new SCAInteractionHandler( questionEx, $rootScope ) ;
    	}
    	else {
    		console.log( "ERROR: Main can't have questions of type other " +
    				     "than SCA" ) ;
    		alert( "Non SCA type question found in Main exam." ) ;
    	}
    }
    
    function scrollToElement( id ) {
    	var myElement = document.getElementById( id ) ;
    	var topPos = myElement.offsetTop ;
    	document.getElementById( 'question-display-scroll-pane' ).scrollTop = topPos ;
    }
    
    function handleTimerEvent() {
    	$scope.secondsRemaining-- ;
    	if( $scope.secondsRemaining > 0 && $scope.timerActive ) {
    		setTimeout( handleTimerEvent, 1000 ) ;
    	}
    	else {
    		$scope.timerActive = false ;
    		$scope.secondsRemaining = 0 ;
    		if( !$scope.answersSubmitted ) {
    			$scope.submitAnswers() ;
    		}
    	}
    	$scope.$digest() ;
    }
	// --- [END] Local functions
} ) ;