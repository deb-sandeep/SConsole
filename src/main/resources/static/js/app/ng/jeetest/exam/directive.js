sConsoleApp.directive( 'renderQuestionOnFullQuestionPaper', function() {

	return {
		restrict : 'E',
		link : function( $scope, element, attributes ) {
			if( element.children.length > 0 &&
				$scope.questionEx.question != null &&
				$scope.questionEx.question.questionFormattedText != null ) {
				
				element.empty() ;
				element.append( DIV( P( {innerHTML : $scope.questionEx.question.questionFormattedText } ) ) ) ;
				MathJax.Hub.Queue( ["Typeset", MathJax.Hub, element.children()[0]] ) ;
			}
        }
	} ;
}) ;