sConsoleApp.controller( 'QBInsightController', function( $scope, $http, $location ) {
    
	var subjectNames = [ 'IIT - Physics','IIT - Chemistry','IIT - Maths' ] ;
	var subjectInsights = [] ;
	
	var phyTotalQ = 0 ;
	var phyAttemptedQ = 0 ;
	var chemTotalQ = 0 ;
	var chemAttemptedQ = 0 ;
	var mathTotalQ = 0 ;
	var mathAttemptedQ = 0 ;
	
	var phyTopics = [] ;
	var chemTopics = [] ;
	var mathTopics = [] ;
	
	var phyTopicsInsights = [] ;
	var chemTopicsInsights = [] ;
	var mathTopicsInsights = [] ;
	
	$scope.$parent.navBarTitle = "Question Bank Insight." ;
	$scope.topicGraphTitle = "Physics" ;
	
	// --- [START] Controller initialization
	loadQBInsights() ;
	// --- [END] Controller initialization
	
	// --- [START] Scope functions
	
	$scope.renderPhyInsights = function() {
		$scope.topicGraphTitle = "Physics" ;
		renderGraph( 'topic_insight_graph', phyTopicsInsights,  phyTopics, 10 ) ;
	}

	$scope.renderChemInsights = function() {
		$scope.topicGraphTitle = "Chemistry" ;
		renderGraph( 'topic_insight_graph', chemTopicsInsights, chemTopics, 10 ) ;
	}

	$scope.renderMathsInsights = function() {
		$scope.topicGraphTitle = "Maths" ;
		renderGraph( 'topic_insight_graph', mathTopicsInsights, mathTopics, 10 ) ;
	}

	// --- [END] Scope functions
	
	// --- [START] internal functions
	
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
			
			if( insight.subjectName == 'IIT - Physics' ) {
				phyTotalQ += insight.totalQuestions ;
				if( insight.attemptedQuestions != null ) {
					phyAttemptedQ += insight.attemptedQuestions ;
				}
				
				phyTopics.push( insight.topicName ) ;
				phyTopicsInsights.push( [ insight.totalQuestions - insight.attemptedQuestions, 
					                      insight.attemptedQuestions ] ) ;
			}
			else if( insight.subjectName == 'IIT - Chemistry' ) {
				chemTotalQ += insight.totalQuestions ;
				if( insight.attemptedQuestions != null ) {
					chemAttemptedQ += insight.attemptedQuestions ;
				}
				
				chemTopics.push( insight.topicName ) ;
				chemTopicsInsights.push( [ insight.totalQuestions - insight.attemptedQuestions, 
					                      insight.attemptedQuestions ] ) ;
			}
			else if( insight.subjectName == 'IIT - Maths' ) {
				mathTotalQ += insight.totalQuestions ;
				if( insight.attemptedQuestions != null ) {
					mathAttemptedQ += insight.attemptedQuestions ;
				}
				
				mathTopics.push( insight.topicName ) ;
				mathTopicsInsights.push( [ insight.totalQuestions - insight.attemptedQuestions, 
					                      insight.attemptedQuestions ] ) ;
			}
			else {
				console.log( "Invalid subject found. " + insight.subjectName ) ; 
			}
		}
		
		subjectInsights.push( [ phyTotalQ  - phyAttemptedQ,  phyAttemptedQ  ] ) ;
		subjectInsights.push( [ chemTotalQ - chemAttemptedQ, chemAttemptedQ ] ) ;
		subjectInsights.push( [ mathTotalQ - mathAttemptedQ, mathAttemptedQ ] ) ;
		
		renderGraph( 'subject_insight_graph', subjectInsights,    subjectNames, 10 ) ;
		$scope.renderPhyInsights() ;
	}
	
	function renderGraph( canvasId, dataArray, labels, textSize ) {
		
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
	            
	            key: ['Remaining','Attempted'],
	            keyHalign: 'middle',
	            keyPosition: 'margin',
	            keyColors: ['#00e676','#cfd8dc'],
	            colors: ['#00e676','#cfd8dc'],
	            keyLabelsSize: 10,
	            keyLabelsFont:'Verdana',
	        }
	    }).draw() ;
	}
	// --- [END] internal functions
} ) ;