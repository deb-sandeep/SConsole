sConsoleApp.controller( 'JEETestResultController', function( $scope, $http, $location ) {
    
	// ---------------- Scope variables --------------------------------------
	$scope.$parent.navBarTitle = "Test Results" ;
	$scope.totalMarks = 0 ;
	$scope.totalScore = 0 ;
	$scope.totalNegativeMarks = 0 ;
	$scope.selectedQuestion = null ;
	
	// ---------------- local variables --------------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	initializeController() ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Event listeners -------------------------------------------
	
	// --- [END] Event listeners ---------------------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.returnToTestIndex = function() {
		$location.path( "/" ) ;
	}
	
	$scope.selectQuestion = function( questionEx ) {
		$scope.selectedQuestion = questionEx ;
	}
	
	$scope.getRowBackgroundClass = function( questionEx ) {
		if( questionEx == $scope.selectedQuestion ) {
			return "selected-test-summary-row" ;
		}
		return "" ;
	}
	
	
	
    // --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    function initializeController() {
    	if( $scope.$parent.activeTest == null || 
    		$scope.$parent.questions.length == 0 ) {
    		$location.path( "/" ) ;
    	}
    	else {
    		computeResult() ;
    	}
    }
    
    function computeResult() {
    	for( var i=0; i<$scope.$parent.questions.length; i++ ) {
    		
    		var questionEx = $scope.$parent.questions[i] ;
    		var marks = questionEx.getScore() ;
    		
    		$scope.totalMarks += questionEx.getTotalMarks() ;
    		$scope.totalScore += marks ;
    		if( marks < 0 ) {
    			$scope.totalNegativeMarks -= marks ;
    		}
    	}
    }
    
	// --- [END] Local functions
} ) ;