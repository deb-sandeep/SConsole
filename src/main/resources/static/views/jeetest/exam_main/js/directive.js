sConsoleApp.directive( 'renderQuestionOnFullQuestionPaper', function() {

	return {
		restrict : 'E',
		link : function( $scope, element, attributes ) {
			
			var questionEx = $scope.questionEx ;
			var question = questionEx.question ;
			
			if( element.children.length > 0 &&
				question != null &&
				question.questionFormattedText != null ) {
				
				var ih = questionEx.interactionHandler ;
				
				element.empty() ;
				element.append( 
					DIV( 
						ih.getQuestionBodyElement(),
						ih.getOptionsBodyElement()
					) 
				) ;
				
				MathJax.Hub.Queue( ["Typeset", MathJax.Hub, element.children()[0]] ) ;
			}
        }
	} ;
}) ;

sConsoleApp.directive( 'renderTestSummaryQuestion', function() {

	return {
		restrict : 'E',
		link : function( $scope, element, attributes ) {
			
			$scope.$watch( 'selectedQuestion', function( newValue, oldValue ){
				
				var questionEx = $scope.selectedQuestion ;
				
				if( element.children.length > 0 &&
					questionEx != null &&
					questionEx.question.questionFormattedText != null ) {
					
					var ih = questionEx.interactionHandler ;

					element.empty() ;
					element.append( 
						DIV( 
							ih.getQuestionBodyElement(),
							ih.getOptionsBodyElement()
						) 
					) ;
					
					MathJax.Hub.Queue( ["Typeset", MathJax.Hub, element.children()[0]] ) ;
				}
			}) ; 
        }
	} ;
}) ;

sConsoleApp.directive( 'renderQuestionForAttempt', function() {

	return {
		restrict : 'E',
		link : function( $scope, element, attributes ) {
			
			$scope.$watch( 'currentQuestion', function( newValue, oldValue ){
				
				var questionEx = $scope.currentQuestion ;
				
				if( element.children.length > 0 &&
					questionEx != null &&
					questionEx.question.questionFormattedText != null ) {
					
					var ih = questionEx.interactionHandler ;
					
					element.empty() ;
					element.append( 
						DIV( {
								id : 'q-for-attempt'
							}, 
							ih.getQuestionBodyElement(),
							ih.getOptionsBodyElement(),
							ih.getUserResponseElement()
						) 
					) ;
					
					MathJax.Hub.Queue( ["Typeset", MathJax.Hub, element.children()[0]] ) ;
				}
			}) ; 
        }
	} ;
}) ;

