sConsoleApp.directive( 'attachHoverHandlers', function() {

    return function( $scope, element, attributes ) {
        if( $scope.$last ) {
           $scope.$parent.initializeSectionHovers() ;
        }
    } ;
}) ;