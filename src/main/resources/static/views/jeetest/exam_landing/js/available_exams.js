sConsoleApp.controller( 'AvailableExamsController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Available Exams" ;
	$scope.testSummaries = [] ;
	$scope.includedTopics = {
		'IIT - Physics'   : [],
		'IIT - Chemistry' : [],
		'IIT - Maths'     : []
	} ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	fetchTestConfigurations() ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.showTopics = function( test ) {
		console.log( "Showing topics for test = " + test.id ) ;
		$scope.includedTopics[ 'IIT - Physics'   ] = [] ;
		$scope.includedTopics[ 'IIT - Chemistry' ] = [] ;
		$scope.includedTopics[ 'IIT - Maths'     ] = [] ;
		fetchTopicsForTest( test.id ) ;
	}
	
	$scope.takeTest = function( test ) {
		$scope.$parent.resetState() ;
		$scope.$parent.activeTest = test ;
		
		if( test.examType == "MAIN" ) {
			$location.path( "/instructionsMain" ) ;
		}
		else {
			$location.path( "/instructionsAdv" ) ;
		}
	}
	
	$scope.getNumQuestions = function( test ) {
		return test.numPhyQuestions + 
		       test.numChemQuestions + 
		       test.numMathQuestions ;
	}
	
	$scope.actionVisible = function( test ) {
		return $scope.getNumQuestions( test ) > 0 ;
	}
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
	function fetchTopicsForTest( id ) {
		
    	console.log( "Fetching topics for test. ID = " + id ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.get( "/TestConfiguration/" + id + "/Topics" )
        .then( 
            function( response ){
                console.log( "Topics for test ßreceived." ) ;
                console.log( response ) ;
        		$scope.includedTopics[ 'IIT - Physics'   ] = response.data[ 'IIT - Physics'  ] ;
        		$scope.includedTopics[ 'IIT - Chemistry' ] = response.data[ 'IIT - Chemistry' ] ;
        		$scope.includedTopics[ 'IIT - Maths'     ] = response.data[ 'IIT - Maths' ] ;
            }, 
            function( error ){
                console.log( "Error getting topics for test." ) ;
                console.log( error ) ;
                $scope.$parent.addErrorAlert( "Error getting topics for test." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}
	
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