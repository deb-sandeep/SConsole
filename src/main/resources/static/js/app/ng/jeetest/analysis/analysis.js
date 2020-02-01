function getMarksForQuestion( question ) {
    if( question.targetExam == "MAIN" ) {
        if      ( question.questionType == "SCA" ) return 4 ;
        else if ( question.questionType == "NT"  ) return 4 ;
    }
    else if( question.targetExam == "ADV" ) {
        if      ( question.questionType == "SCA" ) return 3 ;
        else if ( question.questionType == "MCA" ) return 4 ;
        else if ( question.questionType == "NT"  ) return 3 ;
        else if ( question.questionType == "LCT" ) return 3 ;
        else if ( question.questionType == "MMT" ) return 3 ;
    }
    console.log( "ERROR: Marks logic for question not defined. " + 
                 "Question type = " + question.targetExam + ", " + 
                 question.questionType ) ;
    return -99999 ;
}


sConsoleApp.controller( 'ExamAnalysisController', function( $scope, $http, $location ) {
	
	$scope.alerts = [] ;
	$scope.navBarTitle = "Retrospective Analysis" ;
	$scope.interactingWithServer = false ;
	$scope.selectedTestConfigId = -1 ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------

	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.addErrorAlert = function( msgString ) {
	    $scope.alerts.push( { type: 'danger', msg: msgString } ) ;
	} ;
	
	$scope.dismissAlert = function( index ) {
		$scope.alerts.splice( index, 1 ) ;
	}
    
	// --- [END] Scope functions

	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    
	// --- [END] Local functions
} ) ;