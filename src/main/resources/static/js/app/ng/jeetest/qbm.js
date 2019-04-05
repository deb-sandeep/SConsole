sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/newQuestion",
		controller : "NewQuestionController"
	})
	.when("/searchQuestions", {
		templateUrl : "/jeetest/searchQuestions",
		controller : "SearchQuestionController"
	})
	.when("/newQuestion/:id", {
		templateUrl : "/jeetest/newQuestion",
		controller : "NewQuestionController"
	})
});

sConsoleApp.controller( 'QBMController', function( $scope, $http ) {
    
} ) ;