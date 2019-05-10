sConsoleApp.controller( 'MockUIController', function( $scope, $http, $location ) {
    
	$scope.navBarTitle = "Main" ;
	$scope.paletteHidden = false ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	  
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.activateSection = function( subjectName ) {
		console.log( "Activating section = " + subjectName ) ;
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
	
    $scope.saveAndNext = function() {
    	console.log( "Saving answer and showing next question." ) ;
    	// Validate if answer provided
    }
    
    $scope.saveAndMarkForReview = function() {
    	console.log( "Saving answer and marking for review. Showing next question" ) ;
    	// Validate if answer provided
    }
    
    $scope.clearResponse = function() {
    	console.log( "Clearing response." ) ;
    	// Stay on the same question
    }
    
    $scope.markForReviewAndNext = function() {
    	console.log( "Mark question for review and move to next" ) ;
    }
    
    $scope.showNextQuestion = function() {
    	
    }
    
    $scope.showPreviousQuestion = function() {
    	
    }
    
    $scope.submitAnswers = function() {
    	
    }
    
    $scope.showQuestion = function( question ) {
    	
    }
    
    $scope.getStatusStyle = function( question ) {
    	
    }
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
	// --- [END] Local functions
} ) ;