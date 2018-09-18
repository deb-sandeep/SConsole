sConsoleApp.controller( 'ScreenshotController', function( $scope, $http ) {
    
    $scope.loading = false ;
    $scope.message = null ;
    $scope.imgName = "screenshot_icon.png" ;

    $scope.captureScreenshot = function() {
        $scope.loading = true ;
        $http.get( '/ScreenCapture' )
        .then( 
                function( data ){
                    console.log( "Data received." ) ;
                    console.log( data ) ;
                    $scope.imgName = data.data.imgName ;
                }, 
                function( error ){
                    console.log( "Error getting topic details." ) ;
                    console.log( error ) ;
                    $scope.message = error ;
                }
        )
        .finally(function() {
            $scope.loading = false ;
        }) ;
    }
}) ;