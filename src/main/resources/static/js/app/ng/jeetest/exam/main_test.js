// TODO:
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
	
	// ---------------- local variables --------------------------------------
	
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	initializeController() ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Event listeners -------------------------------------------
	
	$scope.$on( 'refreshAttemptSummary', function( event, payload ){
		refreshAttemptSummary() ;
	}) ;
	
	// --- [END] Event listeners ---------------------------------------------
	
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
	
    $scope.submitAnswers = function() {
    	$scope.$parent.timerActive = false ;
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
    		
    		// Note that this route can get transitioned in from either the
    		// instructions page or from the submit confirmation page.
    		// In case we are coming in from the instruction page, the 
    		// questions would need to be freshly loaded and the test started
    		// afresh. However, if we are coming back from the submit confirmation
    		// page, we don't have to recreate the full state - only 
    		// the attempt summary and the section indexes.
    		
    		if( $scope.$parent.questions.length == 0 ) {
    			loadTestConfiguration() ;
    		}
    		else {
    			computeSectionIndices() ;
    			$scope.showQuestion( $scope.$parent.questions[0] ) ;
    		}
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
                computeSectionIndices() ;
                
                $scope.$parent.secondsRemaining = $scope.$parent.questions.length * 2 * 60 ;
                $scope.$parent.startTimer() ;
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
        	enhanceQuestions( phyQuestions ) ;
        }
        
        if( chemQuestions.length > 0 ) {
        	enhanceQuestions( chemQuestions ) ;
        }
        
        if( mathQuestions.length > 0 ) {
        	enhanceQuestions( mathQuestions ) ;
        }
    }
    
    function computeSectionIndices() {
    	
    	$scope.phySectionQuestionIndex  = -1 ;
    	$scope.chemSectionQuestionIndex = -1 ;
    	$scope.mathSectionQuestionIndex = -1 ;
    	
    	for( var i=0; i<$scope.$parent.questions.length; i++ ) {
    		var question = $scope.$parent.questions[i].question ;
    		
    		if( question.subject.name == 'IIT - Physics' && 
        		$scope.phySectionQuestionIndex == -1 ) {
        		$scope.phySectionQuestionIndex = i ;
    		}
    		else if( question.subject.name == 'IIT - Chemistry' && 
    		    $scope.chemSectionQuestionIndex == -1 ) {
    			$scope.chemSectionQuestionIndex = i ;
    		}
    		else if( question.subject.name == 'IIT - Maths' ) {
    			$scope.mathSectionQuestionIndex = i ;
    			break ;
    		}
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
	
	// --- [END] Local functions
} ) ;