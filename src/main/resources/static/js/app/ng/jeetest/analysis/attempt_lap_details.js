sConsoleApp.controller( 'TestAttemptLapDetailsController', function( $scope, $http, $location, $routeParams ) {
    
	$scope.$parent.navBarTitle = "Test Attempt Lap Details (Test ID = " + $scope.$parent.selectedTestConfigId + ")" ;
	$scope.testAttemptId = $routeParams.id ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	// --- [END] Local functions
} ) ;