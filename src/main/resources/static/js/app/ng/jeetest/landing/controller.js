sConsoleApp.controller( 'LandingController', function( $scope, $window ) {
	
	$scope.go = function( url ) {
		$window.location.href = url ;
	}
} ) ;