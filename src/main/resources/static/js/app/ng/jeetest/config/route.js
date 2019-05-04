sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/config/summaryDashboard",
		controller : "SummaryDashboardController"
	})
	.when("/editTest/:id", {
		templateUrl : "/jeetest/config/editTestConfig",
		controller : "EditTestController"
	})
});
