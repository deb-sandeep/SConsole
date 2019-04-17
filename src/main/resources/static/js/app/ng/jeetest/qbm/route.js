sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/qbm/editQuestion",
		controller : "EditQuestionController"
	})
	.when("/searchQuestions", {
		templateUrl : "/jeetest/qbm/searchQuestions",
		controller : "SearchQuestionController"
	})
	.when("/qbInsight", {
		templateUrl : "/jeetest/qbm/qbInsight",
		controller : "QBInsightController"
	})
	.when("/editQuestion/:id", {
		templateUrl : "/jeetest/qbm/editQuestion",
		controller : "EditQuestionController"
	})
});

