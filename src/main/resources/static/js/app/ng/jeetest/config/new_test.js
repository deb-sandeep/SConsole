sConsoleApp.controller( 'NewTestController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Create New Test" ;
	
	$scope.phyTopics = [] ;
	$scope.chemTopics = [] ;
	$scope.mathTopics = [] ;
	
	$scope.selectedTopic = null ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	loadQBInsights() ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.toggleTopicSelection = function( topic ) {
		console.log( "Topic selected = " + topic.topicName ) ;
		topic.selected = !topic.selected ;
	}
	
	$scope.getTopicRowStyle = function( topic ) {
		if( topic.selected ) {
			return "topic-sel-dlg-row" ;
		}
		return "topic-dlg-row" ;
	}
	
	$scope.filterOnlySelectedTopics = function( topic ) {
		return topic.selected ;
	}
	
	$scope.topicSelectionChanged = function() {
		// TODO: Get the active questions associated with this topic 
		//       and categorize them as per their question types.
		//       Populate second column with question type scroll divs
		//       with meta data of each question. The div should have
		//       a move to button which moves the question to the final list.
	}
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
	function loadQBInsights() {
		
        console.log( "Loading question bank insights from server." ) ;
        
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/QBTopicInsights' )
        .then( 
                function( response ){
                    console.log( "QBM insights received." ) ;
                    console.log( response ) ;
                    processRawInsightData( response.data ) ;
                }, 
                function( error ){
                    console.log( "Error getting Q insights data." ) ;
                    console.log( error ) ;
                    $scope.addErrorAlert( "Could not load QB insights." ) ;
                }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}
	
	function processRawInsightData( rawData ) {
		
		for( i=0; i<rawData.length; i++ ) {
			var insight = rawData[i] ;
			
			insight.selected = false ;
			
			if( insight.subjectName == 'IIT - Physics' ) {
				if( insight.totalQuestions > 0 ) {
					$scope.phyTopics.push( insight ) ;
				}
			}
			else if( insight.subjectName == 'IIT - Chemistry' ) {
				if( insight.totalQuestions > 0 ) {
					$scope.chemTopics.push( insight ) ;
				}
			}
			else if( insight.subjectName == 'IIT - Maths' ) {
				if( insight.totalQuestions > 0 ) {
					$scope.mathTopics.push( insight ) ;
				}
			}
			else {
				console.log( "Invalid subject found. " + insight.subjectName ) ; 
			}
		}
	}
	
	// --- [END] Local functions
} ) ;