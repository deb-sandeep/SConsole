sConsoleApp.directive( 'renderTestQuestionOnEditPage', function() {

	return {
		restrict : 'E',
		link : function( $scope, element, attributes ) {
			
			$scope.$watch( 'formattedContent', function( newValue, oldValue ){
				
				if( element.children.length > 0 &&
					$scope.question != null &&
					$scope.question.questionFormattedText != null ) {
					element.empty() ;
					element.append( DIV( P( {innerHTML : $scope.question.questionFormattedText } ) ) ) ;
					MathJax.Hub.Queue( ["Typeset", MathJax.Hub, element.children()[0]] ) ;
				}
			}) ; 
        }
	} ;
}) ;

sConsoleApp.directive( 'renderTestQuestionOnSearchPage', function() {

	return {
		restrict : 'E',
		link : function( $scope, element, attributes ) {
			if( element.children.length > 0 &&
				$scope.question != null &&
				$scope.question.questionFormattedText != null ) {
				
				element.empty() ;
				element.append( DIV( P( {innerHTML : $scope.question.questionFormattedText } ) ) ) ;
				MathJax.Hub.Queue( ["Typeset", MathJax.Hub, element.children()[0]] ) ;
			}
        }
	} ;
}) ;