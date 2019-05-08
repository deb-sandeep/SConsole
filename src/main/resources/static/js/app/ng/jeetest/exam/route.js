sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/exam/availableExams",
		controller : "AvailableExamsController"
	})
});
