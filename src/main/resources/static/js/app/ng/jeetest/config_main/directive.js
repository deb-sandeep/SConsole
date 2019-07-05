sConsoleApp.directive( 'renderSelectedQuestion', function() {

	return {
		restrict : 'E',
		link : function( $scope, element, attributes ) {
			$scope.$watch( 'selectedQuestion', function( newValue, oldValue ){
				if( element.children.length > 0 &&
					$scope.selectedQuestion != null &&
					$scope.selectedQuestion.questionFormattedText != null ) {
					
					element.empty() ;
					element.append( DIV( P( {innerHTML : $scope.selectedQuestion.questionFormattedText } ) ) ) ;
					MathJax.Hub.Queue( ["Typeset", MathJax.Hub, element.children()[0]] ) ;
				}
			}) ; 
			
        }
	} ;
}) ;