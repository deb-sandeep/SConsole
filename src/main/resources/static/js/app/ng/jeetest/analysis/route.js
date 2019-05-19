sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/analysis/availableAttempts",
		controller : "AvailableTestAttemptsController"
	})
	.when("/attemptDetails/:id", {
		templateUrl : "/jeetest/analysis/attemptDetails",
		controller : "TestAttemptDetailsController"
	})
});
