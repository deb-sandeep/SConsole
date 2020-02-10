sConsoleApp.controller( 'TestAttemptDetailsController', function( $scope, $http, $location, $routeParams ) {
    
	$scope.$parent.navBarTitle = "Test Attempt Details (Test ID = " + $scope.$parent.selectedTestConfigId + ")" ;
	$scope.testAttemptId = $routeParams.id ;
	$scope.examType = $routeParams.examType ;
	$scope.questionAttempts = [] ;
	$scope.questions = [] ;
	
	$scope.selectedIndex = -1 ;
	$scope.selectedAttempt = null ;
	$scope.selectedQuestion = null ;
	$scope.graceScoreForSelectedQuestion = 4 ;
	
	$scope.totalScore = 0 ;
	$scope.totalMarks = 0 ;
	
	$scope.rcOptions = new RCOptions() ;
	$scope.rootCause = null ;
	
	$scope.showAttemptSummary = true ;
	
	$scope.testSummary = {
		overall : new RCACluster(),
		phy     : new RCACluster(),
		chem    : new RCACluster(),
		math    : new RCACluster()
	} ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	fetchTestQuestionAttempts( $scope.testAttemptId ) ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.questionAttemptSelected = function( index ) {
		console.log( "Question attempt selected." ) ;
		$scope.selectedIndex = index ;
		$scope.selectedQuestion = $scope.questions[ index ] ;
		$scope.selectedAttempt = $scope.questionAttempts[ index ] ;
	}
	
	$scope.getRowBackgroundClass = function( index ) {
		if( $scope.selectedIndex == index ) {
			return "selected-test-summary-row" ;
		}
		return "" ;
	}
	
    $scope.getAttemptStatusIconStyle = function( index ) {
    	
    	var attempt = $scope.questionAttempts[ index ] ;
    	
    	if( attempt.attemptStatus == "q-not-visited" ) {
    		return "not-visited" ;
    	}
    	else if( attempt.attemptStatus == "q-not-answered" ) {
    		return "not-answered" ;
    	}
    	else if( attempt.attemptStatus == "q-attempted" ) {
    		return "answered" ;
    	}
    	else if( attempt.attemptStatus == "q-marked-for-review" ) {
    		return "review" ;
    	}
    	else if( attempt.attemptStatus == "q-ans-and-marked-for-review" ) {
    		return "review-answered"
    	}
    }
    
    $scope.returnToAttemptIndex = function() {
    	$location.path( "/" ) ;  
    }
    
	$scope.showAwardGraceDialog = function( qAttemptIndex ) {
		console.log( "Showing award grace dialog..." ) ;
		$scope.questionAttemptSelected( qAttemptIndex ) ;
		$( '#graceInputDialog' ).modal( 'show' ) ;
	}
	
	$scope.showRootCauseDialog = function( qAttemptIndex ) {
		$scope.questionAttemptSelected( qAttemptIndex ) ;
		$scope.rootCause = $scope.rcOptions.getOption( $scope.selectedAttempt.rootCause ) ;
		$( '#rootCauseDialog' ).modal( 'show' ) ;
	}
	
	$scope.awardGraceToSelectedQuestion = function() {
		console.log( "Awarding grace to selected question ..." ) ;
		console.log( $scope.graceScoreForSelectedQuestion ) ;
		
		var preGraceScore = $scope.selectedAttempt.score ;
		var preGraceAttemptStatus = $scope.selectedAttempt.attemptStatus ;
		
		$scope.selectedAttempt.score = $scope.graceScoreForSelectedQuestion ;
		$scope.selectedAttempt.attemptStatus = "q-attempted" ;
		if( $scope.graceScoreForSelectedQuestion > 0 ) {
			$scope.selectedAttempt.isCorrect = true ;
		}
		else {
			$scope.selectedAttempt.isCorrect = false ;
		}
		
		// Nullify the pre grace score and add the new one
		$scope.totalScore += ( -1 * preGraceScore ) ;
		$scope.totalScore += $scope.graceScoreForSelectedQuestion ;
		
		refreshStats() ;
		
		updateGraceScoreOnServer( $scope.testAttemptId,
				                  $scope.selectedAttempt.testQuestionId,
				                  preGraceAttemptStatus,
				                  preGraceScore,
				                  $scope.selectedAttempt.attemptStatus,
				                  $scope.selectedAttempt.score ) ;
	}
	
	$scope.saveRootCause = function() {
		console.log( "Saving root cause." ) ;
		if( $scope.rootCause != null ) {
			$scope.selectedAttempt.rootCause = $scope.rootCause.id ;
			updateRootCauseOnServer( parseInt( $scope.testAttemptId ),
	                                 $scope.selectedAttempt.testQuestionId,
	                                 $scope.rootCause.id ) ;
			$( '#rootCauseDialog' ).modal( 'hide' ) ;
		}
		else {
			$scope.$parent.addErrorAlert( "Please provide root cause." ) ;
		}
	}
	
	$scope.toggleSummaryDisplay = function() {
		$scope.showAttemptSummary = !$scope.showAttemptSummary ;
	}
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
	function updateRootCauseOnServer( testAttemptId,
									  testQuestionId,
			                          rootCause ) {
    	console.log( "Updating root cause on server." ) ;
        
    	$scope.$parent.interactingWithServer = true ;
        $http.post( '/TestAttempt/UpdateRootCause', {
        	testAttemptId  : testAttemptId,
        	testQuestionId : testQuestionId,
        	rootCause      : rootCause
        } )
        .then ( 
            function( response ){
                console.log( "Successfully updated root cause" ) ;
                refreshStats() ;
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
                refreshStats() ;
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

    function fetchTestQuestionAttempts( testAttemptId ) {
    	
    	console.log( "Fetching test question attempts from server." ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.get( "/TestQuestionAttempt/TestAttempt/" + testAttemptId )
        .then( 
            function( response ){
                console.log( response ) ;
                $scope.questionAttempts = response.data[0] ;
                $scope.questions = response.data[1] ;
                
                for( var i=0; i<$scope.questions.length; i++ ) {
                	var question = $scope.questions[i] ;
                	var attempt = $scope.questionAttempts[i] ;
                	
                	question.targetExam = $scope.examType ;
                	
                	attempt.partialCorrect = false ;
                	if( attempt.isCorrect ) {
                	    if( attempt.score < getMarksForQuestion( question ) ) {
                	        attempt.partialCorrect = true ;
                	    }
                	}
                	
                	$scope.totalMarks += getMarksForQuestion( question ) ;
                	$scope.totalScore += attempt.score ;
                }
                
                refreshStats() ;
            }, 
            function( error ){
                console.log( "Error getting Test Question Attempts." ) ;
                console.log( error ) ;
                $scope.$parent.addErrorAlert( "Error getting Test Question Attempts." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    function refreshStats() {
    	
    	$scope.testSummary.overall.reset() ;
    	$scope.testSummary.phy.reset() ;
    	$scope.testSummary.chem.reset() ;
    	$scope.testSummary.math.reset() ;
    	
        for( var i=0; i<$scope.questions.length; i++ ) {
        	var question = $scope.questions[i] ;
        	var attempt = $scope.questionAttempts[i] ;
        	
        	$scope.testSummary.overall.updateStats( question, attempt, false ) ;
        	
        	if( question.subject.name == "IIT - Physics" ) {
            	$scope.testSummary.phy.updateStats( question, attempt, true ) ;
        	}
        	else if( question.subject.name == "IIT - Chemistry" ) {
            	$scope.testSummary.chem.updateStats( question, attempt, false ) ;
        	}
        	else if( question.subject.name == "IIT - Maths" ) {
            	$scope.testSummary.math.updateStats( question, attempt, false ) ;
        	}
        }
    }
    
	// --- [END] Local functions
} ) ;