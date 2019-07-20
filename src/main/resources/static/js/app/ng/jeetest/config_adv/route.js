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

// Structure of edit test
// Check for -1
// If not -1, check for sections etc and organize the controller
