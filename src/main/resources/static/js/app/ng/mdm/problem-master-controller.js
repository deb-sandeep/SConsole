sConsoleApp.controller( 'ProblemMasterController', function( $scope, $http ) {
    
    $scope.inputData = "Test" ;
    
    $scope.submit = function() {
        $http.post( '/ProblemMaster', { 
            inputPayload : $scope.inputData
        })
        .then( 
            function( data ){
                console.log( "Successfully posted button press." ) ;
            }, 
            function( error ){
                console.log( "Error posting button press." + error ) ;
            }
        ) ;
    }
} ) ;