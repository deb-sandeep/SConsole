sConsoleApp.controller( 'SearchQuestionController', function( $scope, $http ) {
    
	$scope.$parent.navBarTitle = "Search Questions." ;
	$scope.topicsMasterList = [] ;
	$scope.booksMasterList = [] ;
	
	$scope.searchCriteria = {
		selectedSubjects : [],
		selectedTopics : [],
		selectedBooks : [],
		selectedQuestionTypes : [ "SCA", "MCA", "IT", "RNT", "MMT" ],
		showOnlyUnsynched : false,
		excludeAttempted : true
	}
	
	// --- [START] Controller initialization
	// First load the master data from the server. The drop down
	// master data will consist of subjects, topics, books etc.
	$scope.$parent.loadQBMMasterData() ;
	// --- [END] Controller initialization
	
	// --- [START] Scope functions
	
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
		
		var allTopics = { topicName : "ALL TOPICS" } ;
		var allBooks  =  { bookName : "ALL BOOKS" } ;
		
		$scope.topicsMasterList.unshift( allTopics ) ;
		$scope.booksMasterList.unshift( allBooks ) ;
		$scope.searchCriteria.selectedTopics.push( allTopics ) ;
		$scope.searchCriteria.selectedBooks.push( allBooks ) ;
	}
	
	$scope.executeSearch = function() {
		console.log( "Executing search" ) ;
	}
	
	// --- [END] Scope functions
	
} ) ;