sConsoleApp.controller( 'ExamController', function( $scope, $http ) {
	
	$scope.alerts = [] ;
	$scope.navBarTitle = "Landing" ;
	$scope.interactingWithServer = false ;
	
	$scope.paletteHidden = false ;
	$scope.activeTest = null ;
	
	// This is populated with instances of QuestionEx
	$scope.questions = [] ;
	$scope.currentQuestion = null ;
	
	$scope.addErrorAlert = function( msgString ) {
	    $scope.alerts.push( { type: 'danger', msg: msgString } ) ;
	} ;
	
	$scope.dismissAlert = function( index ) {
		$scope.alerts.splice( index, 1 ) ;
	}

	$scope.toggleQuestionPalette = function() {
		
	    var palette = document.getElementById( "question-palette-panel" ) ;
	    var display = document.getElementById( "question-display-panel" ) ;
		
		if( $scope.paletteHidden ) {
			palette.style.display = "block" ;
			palette.style.width = "25%" ;
			display.style.width = "75%" ;
			$scope.paletteHidden = false ;
		}
		else {
			palette.style.display = "none" ;
			palette.style.width = "0%" ;
			display.style.width = "100%" ;
			$scope.paletteHidden = true ;
		}
	}
} ) ;