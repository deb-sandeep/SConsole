sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/config/summaryDashboard",
		controller : "SummaryDashboardController"
	})
	.when("/newTest", {
		templateUrl : "/jeetest/config/newTestConfig",
		controller : "NewTestController"
	})
});
