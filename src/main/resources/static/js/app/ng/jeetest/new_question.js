sConsoleApp.controller( 'NewQuestionController', 
		                function( $scope, $http, $routeParams ) {
    
	$scope.message = "Hello from NewQuestionController. ID " + $routeParams.id ;
} ) ;