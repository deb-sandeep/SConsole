sConsoleApp.controller( 'JEETestResultController', function( $scope, $http, $location ) {
    
	// ---------------- Scope variables --------------------------------------
	$scope.$parent.navBarTitle = "Test Results" ;
	$scope.totalMarks = 0 ;
	$scope.totalScore = 0 ;
	$scope.totalNegativeMarks = 0 ;
	$scope.selectedQuestion = null ;
	
	// ---------------- local variables --------------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	initializeController() ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Event listeners -------------------------------------------
	
	// --- [END] Event listeners ---------------------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.returnToTestIndex = function() {
		$location.path( "/" ) ;
	}
	
	$scope.selectQuestion = function( questionEx ) {
		$scope.selectedQuestion = questionEx ;
	}
	
	$scope.getRowBackgroundClass = function( questionEx ) {
		if( questionEx == $scope.selectedQuestion ) {
			return "selected-test-summary-row" ;
		}
		return "" ;
	}
	
    // --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    function initializeController() {
    	if( $scope.$parent.activeTest == null || 
    		$scope.$parent.questions.length == 0 ) {
    		$location.path( "/" ) ;
    	}
    	else {
    		computeResult() ;
    	}
    }
    
    function computeResult() {
    	
    	var numCorrectAnswers = 0 ;
    	var numWrongAnswers = 0 ;
        var numNotVisited = 0 ;
        var numNotAnswered = 0 ;
        var numAttempted = 0 ;
        var numMarkedForReview = 0 ;
        var numAnsAndMarkedForReview = 0 ;
        
        var testQuestionAttempts = [] ;
    	
    	for( var i=0; i<$scope.$parent.questions.length; i++ ) {
    		
    		var questionEx = $scope.$parent.questions[i] ;
    		var marks = questionEx.getScore() ;
    		
    		$scope.totalMarks += questionEx.getTotalMarks() ;
    		$scope.totalScore += marks ;
    		if( marks < 0 ) {
    			$scope.totalNegativeMarks -= marks ;
    		}
    		
    		if( questionEx.isCorrectlyAnswered() ) {
    			numCorrectAnswers++ ;
    		}
    		else {
    	    	if( questionEx.attemptState == AttemptState.prototype.ATTEMPTED ||
    	    		questionEx.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
    	    		numWrongAnswers++ ;
    	    	}
    		}
    	
            if( questionEx.attemptState == AttemptState.prototype.NOT_VISITED ) {
            	numNotVisited++ ;
            }
            else if( questionEx.attemptState == AttemptState.prototype.NOT_ANSWERED ) {
            	numNotAnswered++ ;
            }
            else if( questionEx.attemptState == AttemptState.prototype.ATTEMPTED ) {
            	numAttempted++ ;
            }
            else if( questionEx.attemptState == AttemptState.prototype.MARKED_FOR_REVIEW ) {
            	numMarkedForReview++ ;
            }
            else if( questionEx.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
            	numAnsAndMarkedForReview++ ;
            }
            
            testQuestionAttempts.push({
            	testAttemptId : $scope.$parent.testAttempt.id ,
            	testQuestionId : questionEx.question.id ,
            	attemptStatus : questionEx.attemptState,
            	answerProvided : questionEx.interactionHandler.getAnswer(),
            	isCorrect : questionEx.isCorrectlyAnswered(),
            	score : marks,
            	timeSpent : questionEx.timeSpent 
            }) ;
    	}
    	
    	var testAttempt = $scope.$parent.testAttempt ;
    	
        testAttempt.score = $scope.totalScore ;
        testAttempt.timeTaken = $scope.$parent.timeSpent ;
        testAttempt.numCorrectAnswers = numCorrectAnswers ;
        testAttempt.numWrongAnswers = numWrongAnswers ;
        testAttempt.numNotVisited = numNotVisited ;
        testAttempt.numNotAnswered = numNotAnswered ;
        testAttempt.numAttempted = numAttempted ;
        testAttempt.numMarkedForReview = numMarkedForReview ;
        testAttempt.numAnsAndMarkedForReview = numAnsAndMarkedForReview ;
        
        $scope.$parent.saveTestAttempt() ;
        saveTestQuestionAttempts( testQuestionAttempts ) ;
    }
    
    function saveTestQuestionAttempts( attempts ) {
    	
    	console.log( "Saving test question attempt." ) ;
    	
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/TestAttempt/TestQuestionAttempts', attempts )
        .then ( 
            function( response ){
                console.log( "Successfully saved test question attempts." ) ;
            }, 
            function( error ){
                console.log( "Error saving test question attempts on server." ) ;
                $scope.$parent.addErrorAlert( "Could not save test question attempts." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
	// --- [END] Local functions
} ) ;