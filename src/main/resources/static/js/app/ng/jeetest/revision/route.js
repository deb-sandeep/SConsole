sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		template : "<h3>Search results will apear here</h3>",
	})
	.when("/problemList", {
		templateUrl : "/jeetest/revision/problemList",
		controller : "RevisionProblemListController"
	})
	.when("/revisionStudy", {
		templateUrl : "/jeetest/revision/revisionStudy",
		controller : "RevisionStudyController"
	})
});
