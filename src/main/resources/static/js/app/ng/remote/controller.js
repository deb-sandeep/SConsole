sConsoleApp.controller( 'RemoteController', function( $scope, $http ) {
    
    $scope.otaStatus = "ota-idle" ;
    $scope.keyActivationInfo = {
        FF_B      : null,
        UP        : null,
        FF_F      : null,
        LEFT      : null,
        RIGHT     : null,
        DOWN      : null,
        SELECT    : null,
        CANCEL    : null,
        PLAYPAUSE : null,
        STOP      : null,
        FN_A      : null,
        FN_B      : null,
        FN_C      : null,
        FN_D      : null,
        FN_E      : null,
        FN_F      : null,
        FN_G      : null,
        FN_H      : null
    }
    
    var deactivatedKeyInfo = {
        FF_B      : null,
        UP        : null,
        FF_F      : null,
        LEFT      : null,
        RIGHT     : null,
        DOWN      : null,
        SELECT    : null,
        CANCEL    : null,
        PLAYPAUSE : null,
        STOP      : null,
        FN_A      : null,
        FN_B      : null,
        FN_C      : null,
        FN_D      : null,
        FN_E      : null,
        FN_F      : null,
        FN_G      : null,
        FN_H      : null
    } ;
    
    setInterval( function() {
        loadKeyActivationInfo() ;
    }, 2000 ) ;
    
    $scope.btnPressed = function( btnType, btnCode ) {
        console.log( btnType + " - " + btnCode ) ;
        callRemoteControlAPI( btnType, btnCode ) ;
    }
    
    $scope.getOTAStatus = function() {
        return $scope.otaStatus ;
    }
    
    function loadKeyActivationInfo() {

        console.log( "Loading key activation info." ) ;
        
        $scope.otaStatus = "ota-req-sent" ;
        $http.get( '/RemoteControl/FnKeyLabels' )
             .then( 
            function( data ){
                $scope.otaStatus = "ota-idle" ;
                if( data.status == 200 ) {
                    $scope.keyActivationInfo = data.data ;
               }
            }, 
            function( error ){
                $scope.otaStatus = "ota-idle" ;
                console.log( "Error getting function key labels." + error ) ;
                $scope.keyActivationInfo = deactivatedKeyInfo ;
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
                if( data.status == 200 ) {
                    $scope.keyActivationInfo = data.data ;
               }
            }, 
            function( error ){
                $scope.otaStatus = "ota-idle" ;
                console.log( "Error posting button press." + error ) ;
                $scope.keyActivationInfo = deactivatedKeyInfo ;
            }
        ) ;
    }
} ) ;