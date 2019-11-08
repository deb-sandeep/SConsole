sConsoleApp.controller( 'JEEMainTestController', function( $scope, $http ) {

    // These are the strategy based attempt laps
    // L1  - Level 1
    // L2  - Level 2
    // AMR - Answers marked for review
    // L3P - Level 3 prioritization
    // L2  - Level 3
    var attemptLaps = [ "L1", "L2P", "L2", "AMR", "L3P", "Purple", "L3" ] ;
    
	$scope.$parent.navBarTitle = "Main" ;
	
	$scope.phySectionQuestionIndex  = -1 ;
	$scope.chemSectionQuestionIndex = -1 ;
	$scope.mathSectionQuestionIndex = -1 ;
	
	$scope.attemptSummary = {
		notVisited : 0,
		notAnswered : 0,
		attempted : 0,
		markedForReview : 0,
		answeredAndMarkedForReview : 0
	} ;
	
	// ---------------- local variables --------------------------------------
	
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	$scope.$parent.initializeController( attemptLaps ) ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Event listeners -------------------------------------------
	
	$scope.$on( 'refreshAttemptSummary', function( event, payload ){
		refreshAttemptSummary() ;
	}) ;
	
	$scope.$on( 'computeSectionIndices', function( event, payload ){
		computeSectionIndices() ;
	}) ;

	// --- [END] Event listeners ---------------------------------------------
	
	// --- [START] Scope functions -------------------------------------------
	
	$scope.activateSection = function( subjectName ) {
		console.log( "Activating section = " + subjectName ) ;
		var indexToJump = -1 ;
		
		$scope.$parent.saveClickStreamEvent( 
				ClickStreamEvent.prototype.SECTION_CHANGE, 
				null ) ;
		
		if( subjectName == 'IIT - Physics' ) {
			indexToJump = $scope.phySectionQuestionIndex ;
		}
		else if( subjectName == 'IIT - Chemistry' ) {
			indexToJump = $scope.chemSectionQuestionIndex ;
		}
		else if( subjectName == 'IIT - Maths' ) {
			indexToJump = $scope.mathSectionQuestionIndex ;
		}
		
		if( indexToJump > -1 ) {
			$scope.showQuestion( $scope.$parent.questions[ indexToJump ] ) ;
		}
	}
	
    // --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    function computeSectionIndices() {
    	
    	$scope.phySectionQuestionIndex  = -1 ;
    	$scope.chemSectionQuestionIndex = -1 ;
    	$scope.mathSectionQuestionIndex = -1 ;
    	
    	for( var i=0; i<$scope.$parent.questions.length; i++ ) {
    		var question = $scope.$parent.questions[i].question ;
    		
    		if( question.subject.name == 'IIT - Physics' && 
        		$scope.phySectionQuestionIndex == -1 ) {
        		$scope.phySectionQuestionIndex = i ;
    		}
    		else if( question.subject.name == 'IIT - Chemistry' && 
    		    $scope.chemSectionQuestionIndex == -1 ) {
    			$scope.chemSectionQuestionIndex = i ;
    		}
    		else if( question.subject.name == 'IIT - Maths' ) {
    			$scope.mathSectionQuestionIndex = i ;
    			break ;
    		}
    	}
    }
    
    function refreshAttemptSummary() {
    	
        $scope.attemptSummary.notVisited = 0 ;
        $scope.attemptSummary.notAnswered = 0 ;
        $scope.attemptSummary.attempted = 0 ;
        $scope.attemptSummary.markedForReview = 0 ;
        $scope.attemptSummary.answeredAndMarkedForReview = 0 ;    	
    	
    	for( var i=0; i<$scope.$parent.questions.length; i++ ) {
    		
    		var q = $scope.$parent.questions[i] ;
    		if( q.attemptState == AttemptState.prototype.NOT_VISITED ) {
    			$scope.attemptSummary.notVisited++ ;
    		}
    		else if( q.attemptState == AttemptState.prototype.NOT_ANSWERED ) {
    			$scope.attemptSummary.notAnswered++ ;
    		}
    		else if( q.attemptState == AttemptState.prototype.ATTEMPTED ) {
    			$scope.attemptSummary.attempted++ ;
    		}
    		else if( q.attemptState == AttemptState.prototype.MARKED_FOR_REVIEW ) {
    			$scope.attemptSummary.markedForReview++ ;
    		}
    		else if( q.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
    			$scope.attemptSummary.answeredAndMarkedForReview++ ;
    		}
    	}
    }
	
	// --- [END] Local functions
} ) ;