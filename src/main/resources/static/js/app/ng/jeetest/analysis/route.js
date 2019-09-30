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
	.when("/testAttemptTimeSequence/:id", {
		templateUrl : "/jeetest/analysis/testAttemptTimeSequence",
		controller : "TestAttemptTimeSequenceController"
	})
    .when("/testAttemptLapDetails/:id/:examType", {
        templateUrl : "/jeetest/analysis/testAttemptLapDetails",
        controller : "TestAttemptLapDetailsController"
    })
});
