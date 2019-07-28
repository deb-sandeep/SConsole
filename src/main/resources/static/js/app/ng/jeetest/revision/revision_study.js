sConsoleApp.controller( 'RevisionStudyController', function( $scope, $http, $location ) {
	
	var currentIndex = 0 ;
	var timerOn = true ;
	
	$scope.numQuestionsRemaining = 0 ;
	$scope.numQuestionsRevised = 0 ;
	$scope.timeSpent = 0 ;
	$scope.currentQuestion = null ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------

	// --- [END] Controller initialization -----------------------------------
	
	if( $scope.$parent.revisionProblems.length == 0 ) {
		$location.path( "/" ) ;
	}
	else {
		rotateRevisionProblemsAsPerSelection() ;
		
		$scope.$parent.revisionInProgress = true ;
		$scope.numQuestionsRemaining = $scope.$parent.revisionProblems.length ;
		$scope.numQuestionsRevised = 0 ;
		$scope.currentQuestion = $scope.$parent.revisionProblems[0] ;
		
		timerOn = true ;
		setTimeout( handleSecondClick, 1000 ) ;
	}
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.revisionCompleted = function() {
		
    	console.log( "Setting problem as revised." ) ;
    	
        $scope.$parent.interactingWithServer = true ;
        
        var postData = new FormData();
        postData.append( "questionId", $scope.currentQuestion.questionId ) ;
        
        $http.post( '/SetRevised', postData, {
        	transformRequest: angular.identity,
        	headers: { 'Content-Type': undefined }
        })
        .then( 
            function( response ){
                console.log( "Successfully set revision status." ) ;
                $scope.numQuestionsRevised++ ;
                $scope.$parent.revisionProblems.splice( $scope.currentIndex, 1 ) ;
                $scope.numQuestionsRemaining = $scope.$parent.revisionProblems.length ;
                
                if( $scope.numQuestionsRemaining == 0 ) {
                	$location.path( "/" ) ;
                }
                else {
                	$scope.currentQuestion = $scope.$parent.revisionProblems[ currentIndex ] ;
                }
            }, 
            function( error ){
                console.log( "Error setting revision status." + error ) ;
                $scope.$parent.addErrorAlert( "Could not update revision status." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}
	
	$scope.skipRight = function() {
		if( currentIndex < $scope.$parent.revisionProblems.length - 1 ) {
			currentIndex++ ;
			$scope.currentQuestion = $scope.$parent.revisionProblems[ currentIndex ] ;
		}
	}
	
	$scope.skipLeft = function() {
		if( currentIndex > 0 ) {
			currentIndex-- ;
			$scope.currentQuestion = $scope.$parent.revisionProblems[ currentIndex ] ;
		}
	}
	
	$scope.toggleStar = function() {
		$scope.currentQuestion.starred = ( $scope.currentQuestion.starred == 1 ) ? 0 : 1 ;
		
    	console.log( "Toggling star status of problem." ) ;
    	
        $scope.$parent.interactingWithServer = true ;
        
        var postData = new FormData();
        postData.append( "questionId", $scope.currentQuestion.questionId ) ;
        postData.append( "starred",    $scope.currentQuestion.starred ) ;
        
        $http.post( '/ToggleStar', postData, {
        	transformRequest: angular.identity,
        	headers: { 'Content-Type': undefined }
        })
        .then( 
            function( response ){
                console.log( "Successfully toggled star status." ) ;
            }, 
            function( error ){
                console.log( "Error toggling star status." + error ) ;
                $scope.$parent.addErrorAlert( "Could toggling star status." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}
	
	$scope.endRevision = function() {
		$location.path( "/" ) ;
		timerOn = false ;
		$scope.$parent.revisionInProgress = false ;
	}

	// --- [END] Scope functions

	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
	function handleSecondClick() {
		if( timerOn ) {
			$scope.timeSpent++ ;
			$scope.$apply() ;
			setTimeout( handleSecondClick, 1000 ) ;
		}
	}
    
	function rotateRevisionProblemsAsPerSelection() {
		var selectedProblem = $scope.$parent.selectedProblem ;
		
		while( $scope.$parent.revisionProblems[0] != selectedProblem ) {
			var head = $scope.$parent.revisionProblems.shift() ;
			$scope.$parent.revisionProblems.push( head ) ;
		}
	}
	
	// --- [END] Local functions
} ) ;