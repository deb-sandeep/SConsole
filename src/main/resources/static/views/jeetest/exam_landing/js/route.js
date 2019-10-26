sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/exam-landing/availableExams",
		controller : "AvailableExamsController"
	})
	.when("/instructionsMain", {
		templateUrl : "/jeetest/exam-landing/instructionsMain",
		controller : "InstructionsController"
	})
    .when("/instructionsAdv", {
        templateUrl : "/jeetest/exam-landing/instructionsAdv",
        controller : "InstructionsController"
    })
});
