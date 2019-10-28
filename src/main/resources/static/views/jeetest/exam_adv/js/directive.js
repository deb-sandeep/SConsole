sConsoleApp.directive( 'attachHoverHandlers', function() {

    return function( $scope, element, attributes ) {
        console.log( "Hellow" ) ;
        if( $scope.$last ) {
           $scope.$parent.initializeSectionHovers() ;
        }
    } ;
}) ;