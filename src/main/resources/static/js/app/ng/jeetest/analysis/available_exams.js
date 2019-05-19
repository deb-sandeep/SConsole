sConsoleApp.controller( 'AvailableTestAttemptsController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Available Test Attempts" ;
	$scope.attemptSummaries = [] ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	fetchAvailableTestAttempts() ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.displayTestAttemptDetails = function( testAttempt ) {
		$location.path( "/attemptDetails/" + testAttempt.id ) ;
	}
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
    function fetchAvailableTestAttempts() {
    	
    	console.log( "Fetching test attempt summaries from server." ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.get( "/TestAttempt" )
        .then( 
            function( response ){
                console.log( response ) ;
                $scope.attemptSummaries = response.data ;
            }, 
            function( error ){
                console.log( "Error getting Test Attempt Summaries." ) ;
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