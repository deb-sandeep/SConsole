sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
        templateUrl : "/jeetest/exam/mainTest",
        controller : "JEEMainTestController"
	})
	.when("/testResult", {
		templateUrl : "/jeetest/exam/mainTestResult",
		controller : "JEEMainTestResultController"
	})
});
