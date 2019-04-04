sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/newQuestion"
	})
	.when("/searchQuestions", {
		templateUrl : "/jeetest/searchQuestions"
	})
	.when("/newQuestion", {
		templateUrl : "/jeetest/newQuestion"
	})
});

sConsoleApp.controller( 'QBMController', function( $scope, $http ) {
    
} ) ;