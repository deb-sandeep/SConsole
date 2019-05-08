sConsoleApp.controller( 'MainInstructionController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Instructions for JEE Main" ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	if( $scope.$parent.activeTest == null ) {
		$location.path( "/" ) ;
	}
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.startTest = function() {
		$location.path( "/startMainTest" ) ;
	}
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
	// --- [END] Local functions
} ) ;