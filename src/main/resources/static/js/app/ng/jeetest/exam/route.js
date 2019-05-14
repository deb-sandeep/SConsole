sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/exam/availableExams",
		controller : "AvailableExamsController"
	})
	.when("/instructionsMain", {
		templateUrl : "/jeetest/exam/instructionsMain",
		controller : "MainInstructionController"
	})
	.when("/instructionsAdv", {
		templateUrl : "/jeetest/exam/instructionsAdv",
		controller : "AdvInstructionController"
	})
	.when("/startMainTest", {
		templateUrl : "/jeetest/exam/mainTest",
		controller : "JEEMainTestController"
	})
	.when("/testResult", {
		templateUrl : "/jeetest/exam/testResult",
		controller : "JEETestResultController"
	})
});
