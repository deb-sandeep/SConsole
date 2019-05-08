sConsoleApp.controller( 'JEEMainTestController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Main" ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	if( $scope.$parent.activeTest == null ) {
		$location.path( "/" ) ;
	}
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
	// --- [END] Local functions
} ) ;