sConsoleApp.controller( 'NewQuestionController', 
		                function( $scope, $http, $routeParams ) {
	
	var questionId = $routeParams.id ;
    
	$scope.message = "Test message from New Question Controller. ID = " + questionId ;
	$scope.interactingWithServer = false ;
	$scope.qbmMasterData = null ;
	
	// --- [START] Controller initialization
	
	// First load the dropdown master data from the server. The drop down
	// master data will consist of subjects, topics, books etc.
	loadQBMMasterData() ;
	
	// --- [END] Controller initialization
	loadQuestionForEdit( questionId ) ;

    function loadQBMMasterData() {
        
        console.log( "Loading QBM master data from server." ) ;
        
        $scope.interactingWithServer = true ;
        $http.get( '/QBMMasterData' )
        .then( 
                function( data ){
                    console.log( "QBM master data received." ) ;
                    console.log( data ) ;
                    $scope.qbmMasterData = data ;
                }, 
                function( error ){
                    console.log( "Error getting QBM master data." ) ;
                    console.log( error ) ;
                }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
    
    function loadQuestionForEdit( questionId ) {
    	
    	console.log( "Loading question for edit. ID = " + questionId ) ;
    	
    	// Validate if $routeParams.id is undefined or -1, else try to 
    	// get into modify mode by calling the server for loading the specified
    	// question.
    	if( (typeof questionId !== 'undefined') && (questionId > 0) ) {
    		console.log( "Loading question." ) ;
    	}
    	else {
    		console.log( "No question to load." ) ;
    	}
    }
} ) ;