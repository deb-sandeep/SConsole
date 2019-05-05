sConsoleApp.controller( 'EditTestController', function( $scope, $http, $routeParams, $location ) {
    
	$scope.$parent.navBarTitle = "Create New Test" ;
	$scope.examTypes = [ "MAIN", "ADV" ] ;
	$scope.questionTypes = [ "SCA", "MCA", "IT", "RNT", "MMT" ] ;
	
	$scope.testId = $routeParams.id ;
	
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
	
	console.log( "Editing test = " + $scope.testId ) ;
	
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
				break ;
			}
		}
		
		// Add the selected question to the target array 
		tgtArray.push( question ) ;
		
		refreshRampGraph( sType, tgtArray ) ;
	}
	
	$scope.moveQuestionUp = function() {
		changeSelectedQuestionSequence( true ) ;
	}
	
	$scope.moveQuestionDown = function() {
		changeSelectedQuestionSequence( false ) ;
	}
	
	$scope.removeAssembledQuestion = function() {
		
		var question = $scope.selectedQuestion ;
		var sType = question.subject.name ;
		var qType = question.questionType ;
		
		var srcArray = $scope.assembledQuestions[ sType ] ;
		
		for( var i=0; i<srcArray.length; i++ ) {
			if( srcArray[i] == question ) {
				srcArray.splice( i, 1 ) ;
				if( question.topic.id == $scope.selectedTopic.topicId ) {
					$scope.questionsForSelectedTopic[ qType ].push( question ) ;
				}
				
				if( i < srcArray.length ) {
					$scope.selectedQuestion = srcArray[i] ;
				}
				else {
					if( srcArray.length > 0 ) {
						$scope.selectedQuestion = srcArray[i-1] ;
					}
				}
				refreshRampGraph( sType, srcArray ) ;
				break ;
			}
		}
	}
	
	$scope.getProjectedMinutes = function( subject ) {
		
		var minutes = 0 ;
		var srcArray = $scope.assembledQuestions[ subject ] ;
		
		for( var i=0; i<srcArray.length; i++ ) {
			minutes += srcArray[i].projectedSolveTime ;
		}
		return minutes/60 ;
	}
	
	$scope.saveTest = function() {
		
		console.log( "Saving test." ) ;
	}
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
	function changeSelectedQuestionSequence( up ) {
		
		if( $scope.selectedQuestion == null ) return ;
		
		var question = $scope.selectedQuestion ;
		var sType = question.subject.name ;
		var qType = question.questionType ;
		
		var tgtQArray = $scope.assembledQuestions[ sType ] ;
		for( var i=0; i<tgtQArray.length; i++ ) {
			if( tgtQArray[i] == question ) {
				
				if( up ) {
					if( i > 0 ) {
						var temp = tgtQArray[i-1] ;
						tgtQArray[i-1] = question ;
						tgtQArray[i] = temp ;
					}
				} 
				else {
					if( i < tgtQArray.length-1 ) {
						var temp = tgtQArray[i+1] ;
						tgtQArray[i+1] = question ;
						tgtQArray[i] = temp ;
					}
				}
				refreshRampGraph( sType, tgtQArray ) ;
				return ;
			}
		}
	}
	
	function refreshRampGraph( sType, tgtArray ) {
		
		var canvasId = "" ;
		
		if     ( sType == 'IIT - Physics'   ) { canvasId = "phy_ramp_graph"  ; }
		else if( sType == 'IIT - Chemistry' ) { canvasId = "chem_ramp_graph" ; }
		else if( sType == 'IIT - Maths'     ) { canvasId = "math_ramp_graph" ; }
		
	    var canvas = document.getElementById( canvasId ) ;
	    RGraph.reset( canvas ) ;
	    
	    var timeArray = [] ;
	    var latArray = [] ;
	    var xTicks = [] ;
	    
	    for( var i=0; i<tgtArray.length; i++ ) {
	    	xTicks.push( "" + (i+1) ) ;
	    	latArray.push( tgtArray[i].lateralThinkingLevel ) ;
	    	timeArray.push( tgtArray[i].projectedSolveTime ) ;
	    }
	    
	    bar = new RGraph.Bar({
	        id: canvasId,
	        data: latArray,
	        options: {
	            backgroundGridVlines: false,
	            backgroundGridBorder: true,
	            backgroundGridColor: '#999',
	            xaxisLabels: xTicks,
	            hmargin: 2,
	            marginLeft: 30,
	            marginRight: 50,
	            marginBottom: 20,
	            colors: ['Gradient(#E0E0E0:#FEFEFE)'],
	            textColor: '#ccc',
	            axesColor: '#999',
	            xaxisTickmarksCount: 0,
	            yaxisTickmarksCount: 0,
	            shadowColor: 'black',
	            shadowOffsetx: 0,
	            shadowOffsety: 0,
	            shadowBlur: 5,
	            colorsStroke: 'rgba(0,0,0,0)',
	            combinedEffect: 'wave',
	            combinedEffectOptions: '{frames: 30}',
	            textSize: 12
	        }
	    });



	    var line = new RGraph.Line({
	        id: canvasId,
	        data: timeArray,
	        options: {
	            axes: false,
	            backgroundGrid: false,
	            linewidth: 2,
	            colors: ['#0b0'],
	            yaxisPosition: 'right',
	            axesColor: '#999',
	            textColor: '#ccc',
	            marginLeft: 5,
	            marginRight: 5,
	            tickmarksStyle: null,
	            spline: true,
	            combinedEffect: 'trace',
	            textSize: 12
	        }
	    });
	    
	    var combo = new RGraph.CombinedChart([bar, line]).draw() ;
	    
	    // Because the combo class turns the Line chart labels off,
	    // turn them back on
	    line.set({
	        yaxisLabels: true
	    });

	    RGraph.redraw();
	}
	
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
                    
                    // Need to filter the response based on what questions are
                    // already selected, so that the questions are note
                    // repeated across the question combo and assembled lists.
                    $scope.questionsForSelectedTopic = 
                    	filterFreshlyLoadedTopicQuestions( topicId, response.data ) ;
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
	
	function filterFreshlyLoadedTopicQuestions( topicId, typeQuestionsMap ) {
		for( var i=0; i<$scope.questionTypes.length; i++ ) {
			var srcArray = typeQuestionsMap[ $scope.questionTypes[i] ] ;
			
			if( srcArray.length == 0 ) continue ;
			
			var sType = srcArray[0].subject.name ;
			var assembledQuestions = $scope.assembledQuestions[ sType ] ;
			
			if( assembledQuestions.length == 0 ) continue ;
			for( var j=0; j<assembledQuestions.length; j++ ) {
				var assQ = assembledQuestions[j] ;
				
				if( assQ.topic.id == topicId ) {
					
					for( var k=0; k<srcArray.length; k++ ) {
						var srcQ = srcArray[k] ;
						
						if( srcQ.id == assQ.id ) {
							srcArray.splice( k, 1 ) ;
							k-- ;
							console.log( "Purged " + srcQ.questionRef ) ;
						}
					}
				}
			}
		}
		
		return typeQuestionsMap ;
	}
	
	// --- [END] Local functions
} ) ;