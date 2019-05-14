// TODO:
//      0. Refactor questions, attemptSummary, currentQuestion, secondsREmainng to parent
//		1. Reapply overflowY to body on moving to the next route
sConsoleApp.controller( 'JEEMainTestController', function( $scope, $rootScope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Main" ;
	
	$scope.phySectionQuestionIndex  = -1 ;
	$scope.chemSectionQuestionIndex = -1 ;
	$scope.mathSectionQuestionIndex = -1 ;
	
	$scope.attemptSummary = {
		notVisited : 0,
		notAnswered : 0,
		attempted : 0,
		markedForReview : 0,
		answeredAndMarkedForReview : 0
	} ;
	
	$scope.secondsRemaining = 0 ;
	
	// ---------------- local variables --------------------------------------
	
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	initializeController() ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.activateSection = function( subjectName ) {
		console.log( "Activating section = " + subjectName ) ;
		var indexToJump = -1 ;
		
		if( subjectName == 'IIT - Physics' ) {
			indexToJump = $scope.phySectionQuestionIndex ;
		}
		else if( subjectName == 'IIT - Chemistry' ) {
			indexToJump = $scope.chemSectionQuestionIndex ;
		}
		else if( subjectName == 'IIT - Maths' ) {
			indexToJump = $scope.mathSectionQuestionIndex ;
		}
		
		if( indexToJump > -1 ) {
			$scope.showQuestion( $scope.$parent.questions[ indexToJump ] ) ;
		}
	}
	
    $scope.saveAndNext = function() {
    	console.log( "Saving answer and showing next question." ) ;
    	if( $scope.$parent.currentQuestion.interactionHandler.isAnswered() ) {
    		$scope.$parent.currentQuestion.attemptState = AttemptState.prototype.ATTEMPTED ;
    		$scope.showQuestion( $scope.$parent.currentQuestion.nextQuestion ) ;
    	}
    	else {
    		alert( "Please answer the question before saving." ) ;
    	}
    }
    
    $scope.saveAndMarkForReview = function() {
    	console.log( "Saving answer and marking for review. Showing next question" ) ;
    	if( $scope.$parent.currentQuestion.interactionHandler.isAnswered() ) {
        	$scope.$parent.currentQuestion.attemptState = AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ;
        	$scope.showQuestion( $scope.$parent.currentQuestion.nextQuestion ) ;
    	}
    	else {
    		alert( "Please answer the question before saving." ) ;
    	}
    }
    
    $scope.clearResponse = function() {
    	console.log( "Clearing response." ) ;
    	$scope.$parent.currentQuestion.interactionHandler.clearResponse() ;
    }
    
    $scope.markForReviewAndNext = function() {
    	console.log( "Mark question for review and move to next" ) ;
    	$scope.$parent.currentQuestion.attemptState = AttemptState.prototype.MARKED_FOR_REVIEW ;
    	$scope.showQuestion( $scope.$parent.currentQuestion.nextQuestion ) ;
    }
    
    $scope.showNextQuestion = function() {
    	$scope.showQuestion( $scope.$parent.currentQuestion.nextQuestion ) ;
    }
    
    $scope.showPreviousQuestion = function() {
    	$scope.showQuestion( $scope.$parent.currentQuestion.prevQuestion ) ;
    }
    
    $scope.submitAnswers = function() {
    	
    }
    
    $scope.showQuestion = function( questionEx ) {
    	if( questionEx != null ) {
    		$scope.$parent.currentQuestion = questionEx ;
    		if( $scope.$parent.currentQuestion.attemptState == AttemptState.prototype.NOT_VISITED ) {
    			$scope.$parent.currentQuestion.attemptState = AttemptState.prototype.NOT_ANSWERED ;
    		}
    		refreshAttemptSummary() ;
    	}
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
    
    function initializeController() {
    	if( $scope.$parent.activeTest == null ) {
    		$location.path( "/" ) ;
    	}
    	else {
    		// Remove any scrollbars from the viewport - this is a full screen SPA
    		var elements = document.getElementsByTagName( "body" ) ;
    		elements[0].style.overflowY = "hidden" ;
    		
    		loadTestConfiguration() ;
    	}
    }
    
    function loadTestConfiguration() {
    	
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/TestConfiguration/' + $scope.$parent.activeTest.id )
        .then( 
            function( response ){
                console.log( "Successfully loaded test configuration." ) ;
                console.log( response.data ) ;
                preProcessQuestions( response.data ) ;
                
                $scope.secondsRemaining = $scope.$parent.questions.length * 2 * 60 ;
                setTimeout( handleTimerEvent, 1000 ) ;
                $scope.showQuestion( $scope.$parent.questions[0] ) ;
            }, 
            function( error ){
                console.log( "Error getting test configuration from server." + error ) ;
                $scope.$parent.addErrorAlert( "Could not fetch test." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    function preProcessQuestions( testConfig ) {
    	
        var phyQuestions  = testConfig.phyQuestions ;
        var chemQuestions = testConfig.chemQuestions ;
        var mathQuestions = testConfig.mathQuestions ;
        
        if( phyQuestions.length > 0 ) {
        	$scope.phySectionQuestionIndex = $scope.$parent.questions.length ;
        	enhanceQuestions( phyQuestions ) ;
        }
        
        if( chemQuestions.length > 0 ) {
        	$scope.chemSectionQuestionIndex = $scope.$parent.questions.length ;
        	enhanceQuestions( chemQuestions ) ;
        }
        
        if( mathQuestions.length > 0 ) {
        	$scope.mathSectionQuestionIndex = $scope.$parent.questions.length ;
        	enhanceQuestions( mathQuestions ) ;
        }
    }
    
    function enhanceQuestions( questions ) {
    	
    	for( var i=0; i<questions.length; i++ ) {
    		
    		var question = questions[i] ;
    		var questionEx = new QuestionEx( question ) ;
    		var lastQuestionEx = null ;
    		
    		if( $scope.$parent.questions.length > 0 ) {
    			lastQuestionEx = $scope.$parent.questions[ $scope.$parent.questions.length-1 ] ;
    			
    			lastQuestionEx.nextQuestion = questionEx ;
    			questionEx.prevQuestion = lastQuestionEx ;
    		}
    		
    		questionEx.index = $scope.$parent.questions.length ;
    		associateInteractionHandler( questionEx ) ;
    		
    		$scope.$parent.questions.push( questionEx ) ;
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
    
    function refreshAttemptSummary() {
    	
        $scope.attemptSummary.notVisited = 0 ;
        $scope.attemptSummary.notAnswered = 0 ;
        $scope.attemptSummary.attempted = 0 ;
        $scope.attemptSummary.markedForReview = 0 ;
        $scope.attemptSummary.answeredAndMarkedForReview = 0 ;    	
    	
    	for( var i=0; i<$scope.$parent.questions.length; i++ ) {
    		
    		var q = $scope.$parent.questions[i] ;
    		if( q.attemptState == AttemptState.prototype.NOT_VISITED ) {
    			$scope.attemptSummary.notVisited++ ;
    		}
    		else if( q.attemptState == AttemptState.prototype.NOT_ANSWERED ) {
    			$scope.attemptSummary.notAnswered++ ;
    		}
    		else if( q.attemptState == AttemptState.prototype.ATTEMPTED ) {
    			$scope.attemptSummary.attempted++ ;
    		}
    		else if( q.attemptState == AttemptState.prototype.MARKED_FOR_REVIEW ) {
    			$scope.attemptSummary.markedForReview++ ;
    		}
    		else if( q.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
    			$scope.attemptSummary.answeredAndMarkedForReview++ ;
    		}
    	}
    }
	
    function handleTimerEvent() {
    	$scope.secondsRemaining-- ;
    	if( $scope.secondsRemaining > 0 ) {
    		setTimeout( handleTimerEvent, 1000 ) ;
    		$scope.$digest() ;
    	}
    	else {
    		// TODO: Force submit the answers.
    	}
    }
    
	// --- [END] Local functions
} ) ;