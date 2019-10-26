sConsoleApp.controller( 'JEEAdvTestController', function( $scope, $http ) {

    // These are the strategy based attempt laps
    // L1  - Level 1
    // L2  - Level 2
    // AMR - Answers marked for review
    // L3P - Level 3 prioritization
    // L2  - Level 3
    var attemptLaps = [ "L1", "L2", "AMR", "L3P", "Purple", "L3" ] ;
    
    $scope.$parent.navBarTitle = "Advanced" ;
    
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
    }
    
    // --- [END] Scope functions
    
    // -----------------------------------------------------------------------
    // --- [START] Local functions -------------------------------------------
    
    function computeSectionIndices() {
        console.log( "UNIMP : Computing section indices." ) ;
    }
    
    function refreshAttemptSummary() {
        
        console.log( "Refreshing attempt summary." ) ;
        
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