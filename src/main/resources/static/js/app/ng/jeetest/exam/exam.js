sConsoleApp.controller( 'ExamController', function( $scope, $http ) {
	
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
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.initialize = function() {
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
    
	// --- [END] Scope functions

	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    function scrollToElement( id ) {
    	var myElement = document.getElementById( id ) ;
    	var topPos = myElement.offsetTop ;
    	document.getElementById( 'question-display-scroll-pane' ).scrollTop = topPos ;
    }
    
    function handleTimerEvent() {
    	$scope.secondsRemaining-- ;
    	if( $scope.secondsRemaining > 0 && $scope.timerActive ) {
    		setTimeout( handleTimerEvent, 1000 ) ;
    		$scope.$digest() ;
    	}
    	else {
    		$scope.timerActive = false ;
    		// TODO: Force submit the answers.
    	}
    }
	// --- [END] Local functions
} ) ;