sConsoleApp.controller( 'RevisionController', function( $scope, $http, $route, $location ) {
	
	$scope.alerts = [] ;
	$scope.interactingWithServer = false ;
	$scope.qbmMasterData = null ;
	
	$scope.searchCriteria = {
		subject : {
			name: null
		},
		topic : null, 
		book : null,
		ignoreReviewed : true
	} ;
	
	$scope.selectedProblem = null ;
	$scope.revisionProblems = [] ;
	$scope.revisionInProgress = false ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	loadQBMMasterData() ;

	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.addErrorAlert = function( msgString ) {
	    $scope.alerts.push( { type: 'danger', msg: msgString } ) ;
	} ;
	
	$scope.dismissAlert = function( index ) {
		$scope.alerts.splice( index, 1 ) ;
	}
    
	$scope.search = function() {
		$scope.selectedProblem = null ;
		fetchSearchResults() ;
	}
	
	$scope.startRevision = function() {
		$location.path( "/revisionStudy" ) ;
	}
	
    // --- [END] Scope functions

	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    function loadQBMMasterData() {
    	
    	if( $scope.qbmMasterData != null ) return ;
        
        console.log( "Loading QBM master data from server." ) ;
        
        $scope.interactingWithServer = true ;
        $http.get( '/QBMMasterData' )
        .then( 
                function( response ){
                    console.log( "QBM master data received." ) ;
                    console.log( response ) ;
                    $scope.qbmMasterData = response.data ;
                    $scope.searchCriteria.subject.name = $scope.qbmMasterData.subjectNames[0];
                }, 
                function( error ){
                    console.log( "Error getting QBM master data." ) ;
                    console.log( error ) ;
                    $scope.addErrorAlert( "Could not load master data." ) ;
                }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
    
    function fetchSearchResults() {
    	
        console.log( "Loading revision search results from server." ) ;
        
        var subjectName = $scope.searchCriteria.subject.name ;
        var topicId = -1 ;
        var bookId = -1 ;
        
        if( $scope.searchCriteria.topic != null ) {
        	topicId = $scope.searchCriteria.topic.id ;
        }
        
        if( $scope.searchCriteria.book != null ) {
        	bookId = $scope.searchCriteria.book.id ;
        }
        
        $scope.interactingWithServer = true ;
        $http.get( '/RevisionQuestions', {
        	params:{
        		subjectName : subjectName,
        		topicId : topicId,
        		bookId : bookId,
        		ignoreReviewed : $scope.searchCriteria.ignoreReviewed
        	}
        } )
        .then( 
                function( response ){
                    console.log( "Revision questions received." ) ;
                    console.log( response ) ;
                    prepareRevisionQuestions( response.data ) ;
                    $location.path( "/problemList" ) ;
                    $route.reload() ;
                }, 
                function( error ){
                    console.log( "Error revision questions." ) ;
                    console.log( error ) ;
                    $scope.addErrorAlert( "Could not load revision questions." ) ;
                }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
    
    function prepareRevisionQuestions( questions ) {

    	var qIndexMap = {} ;
    	
    	$scope.revisionProblems = [] ;
    	
    	for( var i=0; i<questions.length; i++ ) {
    		var q = questions[i] ;
    		
    		if( !(q.questionId in qIndexMap) ) {
    			qIndexMap[ q.questionId ] = $scope.revisionProblems.length ;
    			$scope.revisionProblems.push( q ) ;
    		}
    		else {
    			var index = qIndexMap[ q.questionId ] ;
    			var earlierQ = $scope.revisionProblems[ index ] ;
    			
    			earlierQ.duration += q.duration ;
    			if( q.outcome == "Pigeon" ) {
    				earlierQ.outcome = "Pigeon" ;
    			}
    			else if( q.outcome == "Redo" ) {
    				earlierQ.outcome = "Redo" ;
    			}
    			
    			if( q.starred ) {
    				earlierQ.starred = true ;
    			}
    		}
    	}
    	
    	if( $scope.revisionProblems.length > 0 ) {
    		$scope.selectedProblem = $scope.revisionProblems[0] ;
    	}
    }

	// --- [END] Local functions
} ) ;