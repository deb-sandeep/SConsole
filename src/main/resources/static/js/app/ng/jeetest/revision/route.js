sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/revision/problemList",
		controller : "RevisionProblemListController"
	})
});
