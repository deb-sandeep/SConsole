sConsoleApp.controller( 'SearchQuestionController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Search Questions." ;
	$scope.topicsMasterList = [] ;
	$scope.booksMasterList = [] ;
	
	$scope.searchResults = [] ;
	
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
			selectedTopics        : selTopics,
			selectedBooks         : selBooks
		} ;
		
		fetchSearchResults( criteria ) ;
	}
	
	// --- [END] Scope functions
	
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