sConsoleApp.controller( 'QBMController', function( $scope, $http ) {
	
	$scope.alerts = [] ;
    
	$scope.addErrorAlert = function( msgString ) {
	    $scope.alerts.push( { type: 'danger', msg: msgString } ) ;
	} ;
	
	$scope.dismissAlert = function( index ) {
		$scope.alerts.splice( index, 1 ) ;
	}
} ) ;