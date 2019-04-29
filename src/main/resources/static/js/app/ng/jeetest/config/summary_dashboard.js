sConsoleApp.controller( 'SummaryDashboardController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Test Summary Dashboard" ;
	$scope.testSummaries = [] ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	fetchTestConfigurations() ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.deleteTest = function( test ) {
		console.log( "Delete test clicked." ) ;
	}
	
	$scope.editTest = function( test ) {
		console.log( "Edit test clicked." ) ;
	}
	
	$scope.newTest = function() {
		console.log( "New test clicked." ) ;
		$location.path( "/newTest" ) ;
	}
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
    function fetchTestConfigurations() {
    	
    	console.log( "Fetching test configuration summaries from server." ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.get( "/TestConfigurationIndex" )
        .then( 
            function( response ){
                console.log( "Test Configuration summaries received." ) ;
                console.log( response ) ;
                $scope.testSummaries = response.data ;
            }, 
            function( error ){
                console.log( "Error getting Test Configuration summaries." ) ;
                console.log( error ) ;
                $scope.$parent.addErrorAlert( "Could not test summaries." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
	// --- [END] Local functions
} ) ;