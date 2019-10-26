sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
        templateUrl : "/jeetest/exam/mainTestExam",
        controller : "JEEMainTestController"
	})
	.when("/testResult", {
		templateUrl : "/jeetest/exam/mainTestResult",
		controller : "JEEMainTestResultController"
	})
});
