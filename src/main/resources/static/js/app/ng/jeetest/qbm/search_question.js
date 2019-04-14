sConsoleApp.controller( 'SearchQuestionController', function( $scope, $http ) {
    
	$scope.$parent.navBarTitle = "Search Questions." ;
	
	// --- [START] Controller initialization
	
	// First load the master data from the server. The drop down
	// master data will consist of subjects, topics, books etc.
	$scope.$parent.loadQBMMasterData() ;
	
	
	// --- [END] Controller initialization
} ) ;