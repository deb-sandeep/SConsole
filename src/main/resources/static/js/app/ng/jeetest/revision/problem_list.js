sConsoleApp.controller( 'RevisionProblemListController', function( $scope, $http, $location ) {
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------

	// --- [END] Controller initialization -----------------------------------
	
	if( $scope.$parent.revisionProblems.length == 0 ) {
		$location.path( "/" ) ;
	}
	else {
		$scope.$parent.revisionInProgress = false ;
	}
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	// --- [END] Scope functions

	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    
	// --- [END] Local functions
} ) ;