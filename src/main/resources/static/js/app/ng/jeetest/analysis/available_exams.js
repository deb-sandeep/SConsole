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
		$location.path( "/attemptDetails/" + testAttempt.id + "/" + 
		                testAttempt.testConfig.examType ) ;
	}
	
	$scope.displayTestAttemptTimeSequence = function( testAttempt ) {
		$location.path( "/testAttemptTimeSequence/" + testAttempt.id ) ;
	}
	
    $scope.displayWrongAnswersAnalysis = function( testAttempt ) {
        $location.path( "/wrongAnswersAnalysis" ) ;
    }
	
    $scope.displayTestAttemptLapDetails = function( testAttempt ) {
        $scope.$parent.selectedTestConfigId = testAttempt.testConfig.id ;
        $location.path( "/testAttemptLapDetails/" + 
                         testAttempt.id + "/" + 
                         testAttempt.testConfig.examType ) ;
    }
    
    $scope.cloneTest = function( testId ) {
        cloneTest( testId ) ;
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
    
    function cloneTest( testId ) {
        
        console.log( "Cloning test id = " + testId ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.post( "/CloneTestConfiguration/" + testId )
        .then( 
            function( response ){
                console.log( response.data ) ;
                showTestCloneSuccessMsg( response.data ) ;
            }, 
            function( error ){
                console.log( "Could not clone test." ) ;
                console.log( error ) ;
                $scope.$parent.addErrorAlert( "Could not clone test." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    function showTestCloneSuccessMsg( test ) {
        
        var msg = "Test cloned successfully.\nID = " + test.id  ;
        if( test.examType == 'ADV' ) {
            if( test.phySectionNames.includes( "LCT" ) || 
                test.chemSectionNames.includes( "LCT" ) || 
                test.mathSectionNames.includes( "LCT" ) ) {
                
                msg += "\n" ;
                msg += "\nTest contains LCT. Needs manual intervention." ;
            } 
        }
        
        alert( msg ) ;
    }
    
	// --- [END] Local functions
} ) ;