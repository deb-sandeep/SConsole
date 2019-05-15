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

sConsoleApp.directive( 'renderQuestionForAttempt', function() {

	return {
		restrict : 'E',
		link : function( $scope, element, attributes ) {
			
			$scope.$watch( 'currentQuestion', function( newValue, oldValue ){
				
				if( element.children.length > 0 &&
					$scope.currentQuestion != null &&
					$scope.currentQuestion.question.questionFormattedText != null ) {
					
					var questionEx = $scope.currentQuestion ;
					var question = questionEx.question ;
					
					element.empty() ;
					element.append( 
						DIV( 
							P( {
								innerHTML : question.questionFormattedText,
								id : 'q-for-attempt'
							}),
							questionEx.interactionHandler.getUserInterface() 
						) 
					) ;
					
					MathJax.Hub.Queue( ["Typeset", MathJax.Hub, element.children()[0]] ) ;
				}
			}) ; 
        }
	} ;
}) ;

sConsoleApp.directive( 'renderTestSummaryQuestion', function() {

	return {
		restrict : 'E',
		link : function( $scope, element, attributes ) {
			
			$scope.$watch( 'selectedQuestion', function( newValue, oldValue ){
				
				if( element.children.length > 0 &&
					$scope.selectedQuestion != null &&
					$scope.selectedQuestion.question.questionFormattedText != null ) {
					element.empty() ;
					element.append( DIV( P( {innerHTML : $scope.selectedQuestion.question.questionFormattedText } ) ) ) ;
					MathJax.Hub.Queue( ["Typeset", MathJax.Hub, element.children()[0]] ) ;
				}
			}) ; 
        }
	} ;
}) ;