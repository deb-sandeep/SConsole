sConsoleApp.controller( 'RevisionStudyController', function( $scope, $http, $location ) {
	
	var currentIndex = 0 ;
	
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
		$scope.$parent.revisionInProgress = true ;
		$scope.numQuestionsRemaining = $scope.$parent.revisionProblems.length ;
		$scope.numQuestionsRevised = 0 ;
		$scope.currentQuestion = $scope.$parent.revisionProblems[0] ;
	}
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	// --- [END] Scope functions

	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    
	// --- [END] Local functions
} ) ;