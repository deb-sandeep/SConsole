sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/config/summaryDashboard",
		controller : "SummaryDashboardController"
	})
});
