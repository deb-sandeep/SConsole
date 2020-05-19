sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/qbm/qbInsight",
		controller : "QBInsightController"
	})
	.when("/searchQuestions", {
		templateUrl : "/jeetest/qbm/searchQuestions",
		controller : "SearchQuestionController"
	})
	.when("/qbInsight", {
		templateUrl : "/jeetest/qbm/qbInsight",
		controller : "QBInsightController"
	})
	.when("/bulkEdit", {
		templateUrl : "/jeetest/qbm/bulkEdit",
		controller : "BulkEditController"
	})
	.when("/editQuestion/:id", {
	    templateUrl : "/jeetest/qbm/editQuestion",
	    controller : "EditQuestionController"
	})
});

