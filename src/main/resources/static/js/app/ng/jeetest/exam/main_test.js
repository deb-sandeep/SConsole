// TODO:
//		1. Reapply overflowY to body on moving to the next route
//      2. Start Test - the spans need to be revisited - remove redundant styles

sConsoleApp.controller( 'JEEMainTestController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Main" ;
	$scope.paletteHidden = false ;
	
	// This is populated with instances of QuestionEx
	$scope.questions = [] ;
	
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
	
	$scope.currentQuestion = null ;
	
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
			$scope.showQuestion( $scope.questions[ indexToJump ] ) ;
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
	
    $scope.saveAndNext = function() {
    	console.log( "Saving answer and showing next question." ) ;
    	// TODO: Validate if answer provided
    	$scope.currentQuestion.attemptState = AttemptState.prototype.ATTEMPTED ;
    	$scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    }
    
    $scope.saveAndMarkForReview = function() {
    	console.log( "Saving answer and marking for review. Showing next question" ) ;
    	// TODO: Validate if answer provided
    	$scope.currentQuestion.attemptState = AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ;
    	$scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    }
    
    $scope.clearResponse = function() {
    	console.log( "Clearing response." ) ;
    	// Stay on the same question
    }
    
    $scope.markForReviewAndNext = function() {
    	console.log( "Mark question for review and move to next" ) ;
    	$scope.currentQuestion.attemptState = AttemptState.prototype.MARKED_FOR_REVIEW ;
    	$scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    }
    
    $scope.showNextQuestion = function() {
    	$scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    }
    
    $scope.showPreviousQuestion = function() {
    	$scope.showQuestion( $scope.currentQuestion.prevQuestion ) ;
    }
    
    $scope.submitAnswers = function() {
    	
    }
    
    $scope.showQuestion = function( questionEx ) {
    	if( questionEx != null ) {
    		$scope.currentQuestion = questionEx ;
    		if( $scope.currentQuestion.attemptState == AttemptState.prototype.NOT_VISITED ) {
    			$scope.currentQuestion.attemptState = AttemptState.prototype.NOT_ANSWERED ;
    		}
    		refreshAttemptSummary() ;
    	}
    }
    
    // --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
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
                $scope.showQuestion( $scope.questions[0] ) ;
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
        	$scope.phySectionQuestionIndex = $scope.questions.length ;
        	enhanceQuestions( phyQuestions ) ;
        }
        
        if( chemQuestions.length > 0 ) {
        	$scope.chemSectionQuestionIndex = $scope.questions.length ;
        	enhanceQuestions( chemQuestions ) ;
        }
        
        if( mathQuestions.length > 0 ) {
        	$scope.mathSectionQuestionIndex = $scope.questions.length ;
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
    		$scope.questions.push( questionEx ) ;
    		questionEx.index = $scope.questions.length ;
    	}
    }
    
    function refreshAttemptSummary() {
    	
        $scope.attemptSummary.notVisited = 0 ;
        $scope.attemptSummary.notAnswered = 0 ;
        $scope.attemptSummary.attempted = 0 ;
        $scope.attemptSummary.markedForReview = 0 ;
        $scope.attemptSummary.answeredAndMarkedForReview = 0 ;    	
    	
    	for( var i=0; i<$scope.questions.length; i++ ) {
    		
    		var q = $scope.questions[i] ;
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
	
	// --- [END] Local functions
} ) ;