sConsoleApp.controller( 'EditQuestionController', 
		                function( $scope, $http, $routeParams ) {
	
	var questionId = $routeParams.id ;
    
	$scope.message = "Test message from New Question Controller. ID = " + questionId ;
	$scope.interactingWithServer = false ;
	$scope.qbmMasterData = null ;
	$scope.question = null ;
	$scope.lastSavedQuestion = null ;
	
	// --- [START] Controller initialization
	
	// First load the dropdown master data from the server. The drop down
	// master data will consist of subjects, topics, books etc.
	loadQBMMasterData() ;
	
	loadQuestionForEdit( questionId ) ;
	// --- [END] Controller initialization
	
	// --- [START] Scope functions
	
	$scope.editNewQuestion = function() {
		console.log( "Editing a new quesiton." ) ;
		loadQuestionForEdit( -1 ) ;
	}
	
	// --- [START] Internal Questions
	
    function loadQBMMasterData() {
        
        console.log( "Loading QBM master data from server." ) ;
        
        $scope.interactingWithServer = true ;
        $http.get( '/QBMMasterData' )
        .then( 
                function( response ){
                    console.log( "QBM master data received." ) ;
                    console.log( response ) ;
                    $scope.qbmMasterData = response.data ;
                }, 
                function( error ){
                    console.log( "Error getting QBM master data." ) ;
                    console.log( error ) ;
                    $scope.$parent.addErrorAlert( "Could not load master data." ) ;
                }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
    
    function loadQuestionForEdit( questionId ) {
    	
    	if( typeof questionId === 'undefined' ) {
    		questionId = -1 ;
    	}
    	console.log( "Loading question for edit. ID = " + questionId ) ;
    	
        $scope.interactingWithServer = true ;
        $http.get( '/TestQuestion/' + questionId )
        .then( 
                function( response ){
                    console.log( "Test Question received." ) ;
                    console.log( response ) ;
                    $scope.question = response.data ;
                    $scope.lastSavedQuestion = jQuery.extend(true, {}, response.data); ;
                }, 
                function( error ){
                    console.log( "Error getting Test Question data." ) ;
                    console.log( error ) ;
                    $scope.$parent.addErrorAlert( "Could not fetch question." ) ;
                }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
} ) ;