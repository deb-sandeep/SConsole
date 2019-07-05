sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/config_main/summaryDashboard",
		controller : "SummaryDashboardController"
	})
	.when("/editTest/:id", {
		templateUrl : "/jeetest/config_main/editTestConfig",
		controller : "EditTestController"
	})
});
