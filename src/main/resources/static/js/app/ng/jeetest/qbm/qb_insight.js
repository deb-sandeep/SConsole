sConsoleApp.controller( 'QBInsightController', function( $scope, $http, $location ) {
    
	var subjectNames = [ 'IIT - Physics','IIT - Chemistry','IIT - Maths' ] ;
	var subjectInsights = [] ;
	
	var phyTotalQ = 0 ;
	var phyAttemptedQ = 0 ;
	var phyAssignedQ = 0 ;
	
	var chemTotalQ = 0 ;
	var chemAttemptedQ = 0 ;
	var chemAssignedQ = 0 ;
	
	var mathTotalQ = 0 ;
	var mathAttemptedQ = 0 ;
	var mathAssignedQ = 0 ;
	
	var phyTopics = [] ;
	var chemTopics = [] ;
	var mathTopics = [] ;
	
	var phyTopicsInsights = [] ;
	var chemTopicsInsights = [] ;
	var mathTopicsInsights = [] ;
	
	$scope.$parent.navBarTitle = "Question Bank Insight." ;
	$scope.topicGraphTitle = "Physics" ;
	
	// --- [START] Controller initialization
	loadQBInsights( null ) ;
	// --- [END] Controller initialization
	
	// --- [START] Scope functions
	
	$scope.renderPhyInsights = function() {
		$scope.topicGraphTitle = "Physics" ;
		loadQBInsights( function(){
			renderTopicInsightGraph( 'topic_insight_graph', phyTopicsInsights,  phyTopics, 10 ) ;
		}) ;
	}

	$scope.renderChemInsights = function() {
		$scope.topicGraphTitle = "Chemistry" ;
		loadQBInsights( function(){
			renderTopicInsightGraph( 'topic_insight_graph', chemTopicsInsights, chemTopics, 10 ) ;
		}) ;
	}

	$scope.renderMathsInsights = function() {
		$scope.topicGraphTitle = "Maths" ;
		loadQBInsights( function(){
			renderTopicInsightGraph( 'topic_insight_graph', mathTopicsInsights, mathTopics, 10 ) ;
		} ) ;
	}

	// --- [END] Scope functions
	
	// --- [START] internal functions
	
	function clearState() {
		
		phyTotalQ = 0 ;
		phyAttemptedQ = 0 ;
		phyAssignedQ = 0 ;
		
		chemTotalQ = 0 ;
		chemAttemptedQ = 0 ;
		chemAssignedQ = 0 ;
		
		mathTotalQ = 0 ;
		mathAttemptedQ = 0 ;
		mathAssignedQ = 0 ;
		
		subjectInsights.length = 0 ;

		phyTopics.length = 0 ;
        chemTopics.length = 0 ;
        mathTopics.length = 0 ;
        
        phyTopicsInsights.length = 0 ;
        chemTopicsInsights.length = 0 ;
        mathTopicsInsights.length = 0 ;
	}
	
	function loadQBInsights( callbackFn ) {
		
        console.log( "Loading question bank insights from server." ) ;
        
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/QBTopicInsights' )
        .then( 
                function( response ){
                    console.log( "QBM insights received." ) ;
                    console.log( response ) ;
                    clearState() ;
                    processRawInsightData( response.data, callbackFn ) ;
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
	
	function processRawInsightData( rawData, callbackFn ) {
		
		for( i=0; i<rawData.length; i++ ) {
			var insight = rawData[i] ;
			
			if( insight.subjectName == 'IIT - Physics' ) {
				phyTotalQ     += insight.totalQuestions ;
				phyAttemptedQ += insight.attemptedQuestions ;
				phyAssignedQ  += insight.assignedQuestions ;
				
				phyTopics.push( insight.topicName ) ;
				phyTopicsInsights.push( [ 
					insight.availableQuestionsByType['SCA'], 
					insight.availableQuestionsByType['MCA'], 
					insight.availableQuestionsByType['NT'], 
					insight.availableQuestionsByType['LCT'], 
					insight.availableQuestionsByType['MMT'], 
					insight.assignedQuestions - insight.attemptedQuestions,
					insight.attemptedQuestions ] ) ;
			}
			else if( insight.subjectName == 'IIT - Chemistry' ) {
				chemTotalQ     += insight.totalQuestions ;
				chemAttemptedQ += insight.attemptedQuestions ;
				chemAssignedQ  += insight.assignedQuestions ;
				
				chemTopics.push( insight.topicName ) ;
				chemTopicsInsights.push( [ 
					insight.availableQuestionsByType['SCA'], 
					insight.availableQuestionsByType['MCA'], 
					insight.availableQuestionsByType['NT'], 
					insight.availableQuestionsByType['LCT'], 
					insight.availableQuestionsByType['MMT'], 
					insight.assignedQuestions - insight.attemptedQuestions,
					insight.attemptedQuestions ] ) ;
			}
			else if( insight.subjectName == 'IIT - Maths' ) {
				mathTotalQ     += insight.totalQuestions ;
				mathAttemptedQ += insight.attemptedQuestions ;
				mathAssignedQ  += insight.assignedQuestions ;
				
				mathTopics.push( insight.topicName ) ;
				mathTopicsInsights.push( [ 
					insight.availableQuestionsByType['SCA'], 
					insight.availableQuestionsByType['MCA'], 
					insight.availableQuestionsByType['NT'], 
					insight.availableQuestionsByType['LCT'], 
					insight.availableQuestionsByType['MMT'], 
					insight.assignedQuestions - insight.attemptedQuestions,
					insight.attemptedQuestions ] ) ;
			}
			else {
				console.log( "Invalid subject found. " + insight.subjectName ) ; 
			}
		}
		
		subjectInsights.push( [ phyTotalQ  - phyAssignedQ,  phyAssignedQ  - phyAttemptedQ,  phyAttemptedQ  ] ) ;
		subjectInsights.push( [ chemTotalQ - chemAssignedQ, chemAssignedQ - chemAttemptedQ, chemAttemptedQ ] ) ;
		subjectInsights.push( [ mathTotalQ - mathAssignedQ, mathAssignedQ - mathAttemptedQ, mathAttemptedQ ] ) ;
		
		renderSubjectInsightGraph( 'subject_insight_graph', subjectInsights,  subjectNames, 10 ) ;
		
		if( callbackFn != null ) {
			callbackFn() ;
		}
		else {
			$scope.renderPhyInsights() ;
		}
	}
	
	function renderSubjectInsightGraph( canvasId, dataArray, labels, textSize ) {
		
	    var canvas = document.getElementById( canvasId ) ;
	    RGraph.reset( canvas ) ;
	    
	    var chart = new RGraph.HBar({
	        id: canvasId,
	        data: dataArray,
	        options: {
	            textFont:'Quicksand',
	            titleFont:'Quicksand',
	            grouping: 'stacked',
	            hmargin: 0,
	            axesColor: '#999',
	            textSize: textSize,
	            marginTop: 40,
	            marginBottom: 10,
	            marginLeft: 10,
	            marginRight: 10,
	            labelsAbove: true,
	            backgroundGridHlines: false,
	            backgroundGridBorder: false,
	            yaxis:false,
	            
	            yaxisLabels: labels,
	            yaxisLabelsFont:'Quicksand',
	            yaxisTitleFont:'Quicksand',
	            
	            key: ['Available', 'Assigned', 'Attempted'],
	            keyHalign: 'middle',
	            keyPosition: 'margin',
	            keyColors: ['#2E773A','#9F9F9F','#cfd8dc'],
	            colors: ['#2E773A','#9F9F9F','#cfd8dc'],
	            keyLabelsSize: 10,
	            keyLabelsFont:'Verdana',
	        }
	    }).draw() ;
	}
	
	function renderTopicInsightGraph( canvasId, dataArray, labels, textSize ) {
		
	    var canvas = document.getElementById( canvasId ) ;
	    RGraph.reset( canvas ) ;
	    
	    var chart = new RGraph.HBar({
	        id: canvasId,
	        data: dataArray,
	        options: {
	            textFont:'Quicksand',
	            titleFont:'Quicksand',
	            grouping: 'stacked',
	            hmargin: 0,
	            axesColor: '#999',
	            textSize: textSize,
	            marginTop: 40,
	            marginBottom: 10,
	            marginLeft: 10,
	            marginRight: 10,
	            labelsAbove: true,
	            backgroundGridHlines: false,
	            backgroundGridBorder: false,
	            yaxis:false,
	            
	            yaxisLabels: labels,
	            yaxisLabelsFont:'Quicksand',
	            yaxisTitleFont:'Quicksand',
	            
	            key: ['SCA', 'MCA', 'NT', 'LCT', 'MMC', 'Assigned', 'Attempted'],
	            keyHalign: 'middle',
	            keyPosition: 'margin',
	            keyColors: ['#4150B7', '#E8DF37', '#19B2CC', '#FC7F47', '#F35844','#9F9F9F','#cfd8dc'],
	            colors: ['#4150B7', '#E8DF37', '#19B2CC', '#FC7F47', '#F35844','#9F9F9F','#cfd8dc'],
	            keyLabelsSize: 10,
	            keyLabelsFont:'Verdana',
	        }
	    }).draw() ;
	}	
	// --- [END] internal functions
} ) ;