sConsoleApp.controller( 'SearchQuestionController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Search Questions." ;
	$scope.topicsMasterList = [] ;
	$scope.booksMasterList = [] ;
	
	$scope.searchResults = [] ;
	$scope.actionCmd = "" ;
	$scope.actionCmdList = [ "", 
		"Select All",
		"Unselect All", 
		"Sync Selected" 
	] ;
	
	// --- [START] Controller initialization
	// First load the master data from the server. The drop down
	// master data will consist of subjects, topics, books etc.
	
	$scope.$parent.loadQBMMasterData() ;
	
	if( $scope.$parent.lastUsedSearchCriteria != null ) {
		fetchSearchResults( $scope.$parent.lastUsedSearchCriteria ) ;
	}
	
	// --- [END] Controller initialization
	
	// --- [START] Scope functions
	
	$scope.editQuestion = function( id ) {
		$location.path( "/editQuestion/" + id ) ;
	}
	
	$scope.deleteQuestion = function( index ) {
		
		var question = $scope.searchResults[index] ;
    	console.log( "Deleting question " + question.id ) ;

    	$scope.$parent.interactingWithServer = true ;
        $http.delete( '/TestQuestion/' + question.id )
        .then( 
            function( response ){
                console.log( "Successfully deleted." ) ;
                $scope.searchResults.splice( index, 1 ) ;
            }, 
            function( error ){
                console.log( "Error deleting question on server." + error ) ;
                var errMsg = "Server error." ;
                $scope.$parent.addErrorAlert( "Could not delete question. " + errMsg ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}
	
	$scope.subjectSelectionChanged = function() {
		console.log( $scope.$parent.searchCriteria.selectedSubjects ) ;
		
		$scope.$parent.selectedTopics = [] ;
		$scope.$parent.selectedBooks = [] ;
		
		$scope.topicsMasterList = [] ;
		$scope.booksMasterList = [] ;
		
		for( i=0; i<$scope.$parent.searchCriteria.selectedSubjects.length; i++ ) {
			var subject = $scope.$parent.searchCriteria.selectedSubjects[i] ;
			var topics = $scope.$parent.qbmMasterData.topics[ subject ] ;
			var books = $scope.$parent.qbmMasterData.books[ subject ] ;
			
			Array.prototype.push.apply( $scope.topicsMasterList, topics ) ;
			Array.prototype.push.apply( $scope.booksMasterList, books ) ;
		}
	}
	
	$scope.executeSearch = function() {
		console.log( "Executing search" ) ;
		
		var selTopics = [] ;
		var selBooks = [] ;
		
		for( i=0; i<$scope.$parent.searchCriteria.selectedTopics.length; i++ ) {
			selTopics.push( $scope.$parent.searchCriteria.selectedTopics[i].id ) ;
		}
		
		for( i=0; i<$scope.$parent.searchCriteria.selectedBooks.length; i++ ) {
			selBooks.push( $scope.$parent.searchCriteria.selectedBooks[i].id ) ;
		}
		
		// NOTE: Empty value of any of the parameters implies that we consider
		//       all the possibilities for that parameter.
		var criteria = {
			subjects              : $scope.$parent.searchCriteria.selectedSubjects,
			selectedQuestionTypes : $scope.$parent.searchCriteria.selectedQuestionTypes,
			showOnlyUnsynched     : $scope.$parent.searchCriteria.showOnlyUnsynched,
			excludeAttempted      : $scope.$parent.searchCriteria.excludeAttempted,
			searchText            : $scope.$parent.searchCriteria.searchText,
			testConfigId          : $scope.$parent.searchCriteria.testConfigId,
			selectedTopics        : selTopics,
			selectedBooks         : selBooks
		} ;
		
		fetchSearchResults( criteria ) ;
	}
	
	$scope.syncTop10Questions = function() {
        var numSelected = 0 ;
        for( i=0; i<$scope.searchResults.length; i++ ) {
            if( numSelected > 10 ) break ;
            var question = $scope.searchResults[i] ;
            if( !question.selected ) {
                question.selected = true ;
                numSelected++ ;
            }
        }
        $scope.actionCmd = "" ;
        syncSelectedQuestions() ;
	}
	
	$scope.actionCmdChanged = function() {
		
		if( $scope.actionCmd == "Select All" ) {
			for( i=0; i<$scope.searchResults.length; i++ ) {
				var question = $scope.searchResults[i] ;
				question.selected = true ;
			}
			$scope.actionCmd = "" ;
		}
		else if( $scope.actionCmd == "Unselect All" ) {
			for( i=0; i<$scope.searchResults.length; i++ ) {
				var question = $scope.searchResults[i] ;
				question.selected = false ;
			}
			$scope.actionCmd = "" ;
		}
		else if( $scope.actionCmd == "Sync Selected" ) {
			syncSelectedQuestions() ;
		}
	}
	
	$scope.syncQuestion = function( question ) {
        syncQuestionsToServer( [ question.id ], function(){
            question.synched = true ;
        }) ;
	}
	
	$scope.isQuestionVisible = function( question ) {
		if( $scope.$parent.searchCriteria.showOnlyUnsynched ) {
			if( question.synched == false ) {
				return true ;
			}
			return false ;
		} 
		return true ;
	}
	
	$scope.automateSyncAllQuestions = function() {

	    var allQuestions = [] ; 
        for( i=0; i<$scope.searchResults.length; i++ ) {
            var question = $scope.searchResults[i] ;
            if( !question.synched ) {
                allQuestions.push( question ) ;
            }
        }
        
        if( allQuestions.length > 0 ) {
            syncAllQuestions( allQuestions ) ;
        }
	}
	
	// --- [END] Scope functions
	
	function syncAllQuestions( questions ) {
	    if( questions.length > 0 ) {
	        var question = questions.shift() ;
	        syncQuestionsToServer( [ question.id ], function(){
	            question.synched = true ;
	            syncAllQuestions( questions ) ;
	        }) ;
	    }
	}
	
	function syncSelectedQuestions() {
		var selectedQuestionIds = [] ;
		var selectedQuestions = [] ;
		
		for( i=0; i<$scope.searchResults.length; i++ ) {
			var question = $scope.searchResults[i] ;
			if( question.selected && !question.synched ) {
				selectedQuestionIds.push( question.id ) ;
				selectedQuestions.push( question ) ;
			}
		}
		if( selectedQuestionIds.length == 0 ) {
			$scope.$parent.addErrorAlert( "No questions selected for sync" ) ;
    		$scope.actionCmd = "" ;
		}
		else {
			syncQuestionsToServer( selectedQuestionIds, function(){
			    for( i=0; i<selectedQuestions.length; i++ ) {
			        selectedQuestions[i].synched = true ;
			    }
                $scope.actionCmd = "" ;
			}) ;
		}
	}
	
	function syncQuestionsToServer( questionIds, callbackFn ) {
	    
        console.log( "Synching questions = " + questionIds ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/SyncTestQuestionsToPimon', questionIds )
        .then( 
            function( response ){
                console.log( "Successfully synched question." ) ;
                if( callbackFn != null ) {
                    callbackFn() ;
                }
            }, 
            function( error ){
                console.log( "Error synching questions." + error ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}
	
	function fetchSearchResults( criteria ) {
		
		$scope.searchResults = [] ;
        $scope.$parent.interactingWithServer = true ;
        
        $http( {
            url:'/TestQuestion',
            method:'GET',
            params: criteria
        })
        .then( 
            function( response ){
                console.log( "Search results received." ) ;
                console.log( response ) ;
                $scope.searchResults = response.data ;
                $scope.$parent.lastUsedSearchCriteria = criteria ;
                
    			for( i=0; i<$scope.searchResults.length; i++ ) {
    				var question = $scope.searchResults[i] ;
    				question.selected = false ;
    			}
            }, 
            function( error ){
                console.log( "Error getting search results." ) ;
                console.log( error ) ;
                $scope.$parent.addErrorAlert( "Could not fetch search results." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}

} ) ;