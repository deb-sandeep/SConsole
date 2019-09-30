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
		$scope.$parent.selectedTestConfigId = testAttempt.testConfig.id ;
		$location.path( "/attemptDetails/" + testAttempt.id ) ;
	}
	
	$scope.displayTestAttemptTimeSequence = function( testAttempt ) {
		$location.path( "/testAttemptTimeSequence/" + testAttempt.id ) ;
	}
	
    $scope.displayTestAttemptLapDetails = function( testAttempt ) {
        $scope.$parent.selectedTestConfigId = testAttempt.testConfig.id ;
        $location.path( "/testAttemptLapDetails/" + 
                         testAttempt.id + "/" + 
                         testAttempt.testConfig.examType ) ;
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