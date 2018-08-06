sConsoleApp.controller( 'RemoteController', function( $scope, $http ) {
    
    $scope.otaStatus = "ota-idle" ;
    
    $scope.btnPressed = function( btnType, btnCode ) {
        console.log( btnType + " - " + btnCode ) ;
        callRemoteControlAPI( btnType, btnCode ) ;
    }
    
    $scope.getOTAStatus = function() {
        return $scope.otaStatus ;
    }
    
    function callRemoteControlAPI( btnType, btnCode ) {

        $scope.otaStatus = "ota-req-sent" ;
        $http.post( '/RemoteControl', { 
            btnType : btnType,
            btnCode : btnCode
        })
        .then( 
            function( data ){
                $scope.otaStatus = "ota-idle" ;
                console.log( "Successfully posted button press." ) ;
            }, 
            function( error ){
                $scope.otaStatus = "ota-idle" ;
                console.log( "Error posting button press." + error ) ;
            }
        ) ;
    }
} ) ;