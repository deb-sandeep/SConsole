sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/config_adv/summaryDashboard",
		controller : "AdvTestConfigSummaryDashboardController"
	})
	.when("/editTest/:id", {
		templateUrl : "/jeetest/config_adv/editTestConfig",
		controller : "EditAdvTestController"
	})
}) ;
