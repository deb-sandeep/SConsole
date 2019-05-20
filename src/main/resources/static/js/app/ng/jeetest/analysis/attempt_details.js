sConsoleApp.controller( 'TestAttemptDetailsController', function( $scope, $http, $location, $routeParams ) {
    
	$scope.$parent.navBarTitle = "Test Attempt Details" ;
	$scope.testAttemptId = $routeParams.id ;
	$scope.questionAttempts = [] ;
	$scope.questions = [] ;
	
	$scope.selectedIndex = -1 ;
	$scope.selectedAttempt = null ;
	$scope.selectedQuestion = null ;
	
	$scope.totalScore = 0 ;
	$scope.totalMarks = 0 ;
	
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
    
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
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
                	
                	if( question.targetExam == "MAIN" && 
                		question.questionType == "SCA" ) {
                		$scope.totalMarks += 4 ;
                	}
                	
                	$scope.totalScore += attempt.score ;
                }
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
    
	// --- [END] Local functions
} ) ;