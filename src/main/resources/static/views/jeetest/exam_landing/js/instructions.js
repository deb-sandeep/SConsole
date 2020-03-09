sConsoleApp.controller( 'InstructionsController', function( $scope, $http, $location, $window ) {
    
	$scope.$parent.navBarTitle = "Exam Instructions" ;
	$scope.sectionDetails = [] ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	if( $scope.$parent.activeTest == null ) {
		$location.path( "/" ) ;
	}
	else {
	    loadTestConfiguration( $scope.$parent.activeTest.id ) ;
	}
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.startTest = function( testType ) {
	    $window.location.href = "/jeetest/exam/" + testType + "Test" + 
	                            "?id=" + $scope.$parent.activeTest.id ;
	}
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
    function loadTestConfiguration( testId ) {
        
        console.log( "Loading test configuration - " + testId ) ;
        
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/TestConfiguration/' + testId )
        .then( 
            function( response ){
                console.log( response.data ) ;
                var cfg = response.data ;
                for( var i=0; i<cfg.phySectionNames.length; i++ ) {
                    $scope.sectionDetails.push( 
                            cfg.phySectionNames[i] + 
                            " - " + 
                            cfg.phySecQuestionIndices[i].length ) ;
                }
            }, 
            function( error ){
                console.log( "Error getting test configuration from server." + error ) ;
                $scope.addErrorAlert( "Could not fetch test." ) ;
            }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }	
	// --- [END] Local functions
} ) ;