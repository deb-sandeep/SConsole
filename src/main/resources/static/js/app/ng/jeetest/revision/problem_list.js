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
	
	$scope.selectQuestion = function( revQ ) {
		$scope.$parent.selectedProblem = revQ ;
	}
	
	$scope.getRevQBackgroundClass = function( revQ ) {
		if( revQ == $scope.$parent.selectedProblem ) {
			return "selected-rev-q-row" ;
		}
		return "" ;
	}
	
	// --- [END] Scope functions

	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    
	// --- [END] Local functions
} ) ;