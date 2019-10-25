sConsoleApp.controller( 'InstructionsController', function( $scope, $http, $location, $window ) {
    
	$scope.$parent.navBarTitle = "Exam Instructions" ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	if( $scope.$parent.activeTest == null ) {
		$location.path( "/" ) ;
	}
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.startTest = function( testType ) {
	    $window.location.href = "/jeetest/exam/" + testType + "Test" ;
	}
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
	// --- [END] Local functions
} ) ;