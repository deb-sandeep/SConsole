sConsoleApp.controller( 'SearchQuestionController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Search Questions." ;
	$scope.topicsMasterList = [] ;
	$scope.booksMasterList = [] ;
	
	$scope.searchCriteria = {
		selectedSubjects : [],
		selectedTopics : [],
		selectedBooks : [],
		selectedQuestionTypes : [],
		showOnlyUnsynched : false,
		excludeAttempted : true,
		searchText : ""
	}
	
	$scope.searchResults = [] ;
	
	// --- [START] Controller initialization
	// First load the master data from the server. The drop down
	// master data will consist of subjects, topics, books etc.
	
	$scope.$parent.loadQBMMasterData() ;
	
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
		console.log( $scope.searchCriteria.selectedSubjects) ;
		$scope.topicsMasterList = [] ;
		$scope.booksMasterList = [] ;
		
		for( i=0; i<$scope.searchCriteria.selectedSubjects.length; i++ ) {
			var subject = $scope.searchCriteria.selectedSubjects[i] ;
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
		
		for( i=0; i<$scope.searchCriteria.selectedTopics.length; i++ ) {
			selTopics.push( $scope.searchCriteria.selectedTopics[i].id ) ;
		}
		
		for( i=0; i<$scope.searchCriteria.selectedBooks.length; i++ ) {
			selBooks.push( $scope.searchCriteria.selectedBooks[i].id ) ;
		}
		
		// NOTE: Empty value of any of the parameters implies that we consider
		//       all the possibilities for that parameter.
		var criteria = {
			subjects              : $scope.searchCriteria.selectedSubjects,
			selectedQuestionTypes : $scope.searchCriteria.selectedQuestionTypes,
			showOnlyUnsynched     : $scope.searchCriteria.showOnlyUnsynched,
			excludeAttempted      : $scope.searchCriteria.excludeAttempted,
			searchText            : $scope.searchCriteria.searchText,
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