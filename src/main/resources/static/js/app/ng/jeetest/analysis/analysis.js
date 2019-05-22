sConsoleApp.controller( 'ExamAnalysisController', function( $scope, $http, $location ) {
	
	$scope.alerts = [] ;
	$scope.navBarTitle = "Retrospective Analysis" ;
	$scope.interactingWithServer = false ;
	
	$scope.activeTest = null ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------

	$location.path( "/" ) ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.addErrorAlert = function( msgString ) {
	    $scope.alerts.push( { type: 'danger', msg: msgString } ) ;
	} ;
	
	$scope.dismissAlert = function( index ) {
		$scope.alerts.splice( index, 1 ) ;
	}
    
	// --- [END] Scope functions

	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    
	// --- [END] Local functions
} ) ;