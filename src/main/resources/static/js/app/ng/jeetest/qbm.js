sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/main"
	})
	.when("/red", {
		templateUrl : "/jeetest/red"
	})
	.when("/green", {
		templateUrl : "/jeetest/green"
	})
	.when("/blue", {
		templateUrl : "/jeetest/blue"
	});
});

sConsoleApp.controller( 'QBMController', function( $scope, $http ) {
    
} ) ;