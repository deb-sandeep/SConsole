sConsoleApp.controller( 'ExamLandingController', function( $scope, $http, $rootScope, $location ) {
    
    // ---------------- Local variables --------------------------------------
    
    // ---------------- Scope variables --------------------------------------
	
	$scope.navBarTitle = "Landing" ;
	$scope.activeTest = null ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
    $scope.resetState = function() {
        $scope.activeTest = null ;
    }

    // --- [END] Scope functions

	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
	// --- [END] Local functions
} ) ;