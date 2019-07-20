sConsoleApp.controller( 'AdvTestConfiguratorController', function( $scope, $http ) {
	
	$scope.alerts = [] ;
	$scope.navBarTitle = "JEE Advanced Test Configurator" ;
	$scope.interactingWithServer = false ;
	$scope.qbmMasterData = null ;
	
	$scope.addErrorAlert = function( msgString ) {
	    $scope.alerts.push( { type: 'danger', msg: msgString } ) ;
	} ;
	
	$scope.dismissAlert = function( index ) {
		$scope.alerts.splice( index, 1 ) ;
	}

	$scope.dismissAllAlerts = function() {
		$scope.alerts.length = 0 ;
	}

    $scope.loadQBMMasterData = function() {
    	
    	if( $scope.qbmMasterData != null ) return ;
        
        console.log( "Loading QBM master data from server." ) ;
        
        $scope.interactingWithServer = true ;
        $http.get( '/QBMMasterData' )
        .then( 
                function( response ){
                    console.log( "QBM master data received." ) ;
                    console.log( response ) ;
                    $scope.qbmMasterData = response.data ;
                }, 
                function( error ){
                    console.log( "Error getting QBM master data." ) ;
                    console.log( error ) ;
                    $scope.addErrorAlert( "Could not load master data." ) ;
                }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
} ) ;