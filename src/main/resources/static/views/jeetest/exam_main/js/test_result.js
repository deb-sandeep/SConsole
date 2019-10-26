sConsoleApp.controller( 'JEEMainTestResultController', function( $scope, $http, $location, $window ) {
    
	// ---------------- Scope variables --------------------------------------
	$scope.$parent.navBarTitle = "JEE Main Test Results" ;
	$scope.totalMarks = 0 ;
	$scope.totalScore = 0 ;
	$scope.totalNegativeMarks = 0 ;
	$scope.selectedQuestion = null ;
	$scope.graceScoreForSelectedQuestion = 4 ;
	
	$scope.rcOptions = new RCOptions() ;
	$scope.rootCause = null ;
	
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
	
	$scope.returnToDashboard = function() {
	    $window.location.href = "/jeetest" ;
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
	
	$scope.showAwardGraceDialog = function( questionEx ) {
		$scope.selectQuestion( questionEx ) ;
		$( '#graceInputDialog' ).modal( 'show' ) ;
	}
	
	$scope.showRootCauseDialog = function( questionEx ) {
		$scope.selectQuestion( questionEx ) ;
		$( '#rootCauseDialog' ).modal( 'show' ) ;
	}
	
	$scope.awardGraceToSelectedQuestion = function() {
		
		var preGraceScore = $scope.selectedQuestion.getScore() ;
		var preGraceAttemptState = $scope.selectedQuestion.attemptState ;
		if( preGraceScore != 0 ) {
			// This will ensure that we roll back any negative marking and
			// since this logic does not take into account the question type
			// and the attempt state, it is generic and will not break while
			// awarding grace tp JEE advanced questions 
			$scope.totalScore += ( -1 * preGraceScore ) ;
			$scope.totalNegativeMarks += preGraceScore ;
		}
		
		$scope.selectedQuestion.awardGrace( $scope.graceScoreForSelectedQuestion ) ;
		
		// Now add the freshly computed score to the total score.
		$scope.totalScore += $scope.selectedQuestion.getScore() ;
		
		updateGraceScoreOnServer( $scope.$parent.testAttempt.id,
					              $scope.selectedQuestion.question.id,
					              preGraceAttemptState,
					              preGraceScore,
					              $scope.selectedQuestion.attemptState,
					              $scope.selectedQuestion.getScore() ) ;
	}
	
	$scope.saveRootCause = function() {
		console.log( "Saving root cause." ) ;
		if( $scope.rootCause != null ) {
			$scope.selectedQuestion.rootCause = $scope.rootCause.id ;
			updateRootCauseOnServer( $scope.$parent.testAttempt.id,
									 $scope.selectedQuestion.question.id,
	                                 $scope.rootCause.id ) ;
			$( '#rootCauseDialog' ).modal( 'hide' ) ;
		}
		else {
			$scope.$parent.addErrorAlert( "Please provide root cause." ) ;
		}
	}
	
    // --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    function initializeController() {
    	if( $scope.$parent.questions.length == 0 ) {
    		$location.path( "/" ) ;
    	}
    	else {
    		computeResult() ;
    	}
    }
    
	function updateRootCauseOnServer( testAttemptId,
									  testQuestionId,
									  rootCause ) {
	    
		$scope.$parent.interactingWithServer = true ;
		$http.post( '/TestAttempt/UpdateRootCause', {
			testAttemptId  : testAttemptId,
			testQuestionId : testQuestionId,
			rootCause      : rootCause
		} )
		.then ( 
			function( response ){
				console.log( "Successfully updated root cause" ) ;
			}, 
			function( error ){
				console.log( "Error saving root cause on server." ) ;
				$scope.$parent.addErrorAlert( "Could not save root cause." ) ;
			}
		)
		.finally(function() {
			$scope.$parent.interactingWithServer = false ;
		}) ;
	}
	
	function updateGraceScoreOnServer( testAttemptId, 
						               testQuestionId,
							           preGraceAttemptState,
							           preGraceScore,
							           postGraceAttemptState,
							           postGraceScore ) {
		
		console.log( "Updating grace score on server." ) ;
		
		$scope.$parent.interactingWithServer = true ;
		$http.post( '/TestAttempt/UpdateGraceScore', {
			testAttemptId : testAttemptId,
			testQuestionId : testQuestionId,
			preGraceAttemptState : preGraceAttemptState,
			preGraceScore : preGraceScore,
			postGraceAttemptState : postGraceAttemptState,
			postGraceScore : postGraceScore
		} )
		.then ( 
			function( response ){
				console.log( "Successfully updated grace score" ) ;
			}, 
			function( error ){
				console.log( "Error saving grace score on server." ) ;
				$scope.$parent.addErrorAlert( "Could not save grace score." ) ;
			}
		)
		.finally(function() {
			$scope.$parent.interactingWithServer = false ;
		}) ;
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
        
        $scope.$parent.endTestAttempt() ;
        saveTestQuestionAttempts( testQuestionAttempts ) ;
    }
    
    function saveTestQuestionAttempts( attempts ) {
    	
    	console.log( "Saving test question attempt." ) ;
    	
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/TestAttempt/TestQuestionAttempts', attempts )
        .then ( 
            function( response ){
                console.log( "Successfully saved test question attempts." ) ;
        		$scope.$parent.saveClickStreamEvent( 
					        				ClickStreamEvent.prototype.SUBMIT, 
					        				null ) ;
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