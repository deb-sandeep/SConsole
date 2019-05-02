sConsoleApp.controller( 'NewTestController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Create New Test" ;
	$scope.examTypes = [ "MAIN", "ADV" ] ;
	
	$scope.phyTopics = [] ;
	$scope.chemTopics = [] ;
	$scope.mathTopics = [] ;
	$scope.questionsForSelectedTopic = {
		SCA : [],
		MCA : [],
		IT  : [],
		RNT : [],
		MMT : []
	} ;
	
	$scope.selectedTopic = null ;
	$scope.selectedQuestion = null ;
	$scope.examType = $scope.examTypes[0] ;
	
	$scope.assembledQuestions = {
		'IIT - Physics'   : [],
		'IIT - Chemistry' : [],
		'IIT - Maths'     : []
	}
	
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
		loadQuestionsForTopic( $scope.selectedTopic.topicId, $scope.examType ) ;
	}
	
	$scope.questionSelectedForTest = function() {
		var qType = $scope.selectedQuestion.questionType ;
		var sType = $scope.selectedQuestion.subject.name ;
		
		var srcArray = $scope.questionsForSelectedTopic[ qType ] ;
		var tgtArray = $scope.assembledQuestions[ sType ] ;
		
		var question = $scope.selectedQuestion ;
		
		// Remove the selected question from the source array
		for( var i=0; i<srcArray.length; i++ ) {
			if( srcArray[i] == question ) {
				srcArray.splice( i, 1 ) ;
				i-- ;
			}
		}
		
		// Add the selected question to the target array 
		tgtArray.push( question ) ;
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
	
	function loadQuestionsForTopic( topicId, examType ) {
		
        console.log( "Loading questions for topic " + topicId ) ;
        
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/TestQuestion/Topic/' + topicId + "?examType=" + examType )
        .then( 
                function( response ){
                    console.log( "Questions for topic received." ) ;
                    console.log( response ) ;
                    $scope.questionsForSelectedTopic = response.data ;
                }, 
                function( error ){
                    console.log( "Error getting Q for topic " + topicId ) ;
                    console.log( error ) ;
                    $scope.addErrorAlert( "Could not load questions for topic." ) ;
                }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}
	
	// --- [END] Local functions
} ) ;