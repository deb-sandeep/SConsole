sConsoleApp.controller( 'ExamController', function( $scope, $http ) {
	
	$scope.alerts = [] ;
	$scope.navBarTitle = "Landing" ;
	$scope.interactingWithServer = false ;
	
	$scope.addErrorAlert = function( msgString ) {
	    $scope.alerts.push( { type: 'danger', msg: msgString } ) ;
	} ;
	
	$scope.dismissAlert = function( index ) {
		$scope.alerts.splice( index, 1 ) ;
	}
} ) ;