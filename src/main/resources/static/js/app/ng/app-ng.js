var sConsoleApp = angular.module( 'sConsoleApp', ['ngRoute'] ) ;

sConsoleApp.directive( 'renderTestQuestion', function() {

	return {
		restrict : 'E',
		link : function( $scope, element, attributes ) {
			$scope.$watch( 'formattedContent', function( newValue, oldValue ){
				if( element.children.length > 0 && 
					$scope.formattedContent != null ) {
					console.log( "Directive kicking in." ) ;
					element.empty() ;
					element.append( DIV( P( {innerHTML : $scope.formattedContent} ) ) ) ;
					MathJax.Hub.Queue( ["Typeset", MathJax.Hub, element.children()[0]] ) ;
				}
			}) ; 
        }
	} ;
}) ;

