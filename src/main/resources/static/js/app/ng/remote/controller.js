sConsoleApp.controller( 'RemoteController', function( $scope, $http ) {
    
    $scope.otaStatus = "ota-idle" ;
    $scope.fnLabels = {
        A : "",
        B : "",
        C : "",
        D : "",
        E : "",
        F : "",
        G : "",
        H : ""
    }
    
    loadInitialFnBtnLabels() ;
    
    $scope.btnPressed = function( btnType, btnCode ) {
        console.log( btnType + " - " + btnCode ) ;
        callRemoteControlAPI( btnType, btnCode ) ;
    }
    
    $scope.getOTAStatus = function() {
        return $scope.otaStatus ;
    }
    
    function loadInitialFnBtnLabels() {

        $scope.otaStatus = "ota-req-sent" ;
        $http.get( '/RemoteControl/FnKeyLabels' )
             .then( 
            function( data ){
                $scope.otaStatus = "ota-idle" ;
                console.log( data ) ;
                if( data.status == 200 ) {
                    $scope.fnLabels.A = data.data.A ;
                    $scope.fnLabels.B = data.data.B ;
                    $scope.fnLabels.C = data.data.C ;
                    $scope.fnLabels.D = data.data.D ;
                    $scope.fnLabels.E = data.data.E ;
                    $scope.fnLabels.F = data.data.F ;
                    $scope.fnLabels.G = data.data.G ;
                    $scope.fnLabels.H = data.data.H ;                
               }
            }, 
            function( error ){
                $scope.otaStatus = "ota-idle" ;
                console.log( "Error getting function key labels." + error ) ;
            }
        ) ;
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
                console.log( data ) ;
                if( data.status == 200 ) {
                    $scope.fnLabels.A = data.data.A ;
                    $scope.fnLabels.B = data.data.B ;
                    $scope.fnLabels.C = data.data.C ;
                    $scope.fnLabels.D = data.data.D ;
                    $scope.fnLabels.E = data.data.E ;
                    $scope.fnLabels.F = data.data.F ;
                    $scope.fnLabels.G = data.data.G ;
                    $scope.fnLabels.H = data.data.H ;                
               }
            }, 
            function( error ){
                $scope.otaStatus = "ota-idle" ;
                console.log( "Error posting button press." + error ) ;
            }
        ) ;
    }
} ) ;