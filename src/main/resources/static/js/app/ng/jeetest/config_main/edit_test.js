sConsoleApp.controller( 'EditTestController', function( $scope, $http, $routeParams, $location ) {
	
	var scaLatLevelSortDir = "asc" ;
	var scaProjTimeSortDir = "desc" ;
    
	$scope.assembledQSortDir = {
		'IIT - Physics' : {
			lat : "asc",
			proj : "asc"
		},
		'IIT - Chemistry' : {
			lat : "asc",
			proj : "asc"
		},
		'IIT - Maths' : {
			lat : "asc",
			proj : "asc"
		}
	}

	$scope.$parent.navBarTitle = "Create New Test" ;
	$scope.questionTypes = [ "SCA", "MCA", "NT", "LCT", "MMT" ] ;
	
	$scope.testId = $routeParams.id ;
	
	$scope.phyTopics = [] ;
	$scope.chemTopics = [] ;
	$scope.mathTopics = [] ;
	
	$scope.questionsForSelectedTopic = {
		SCA : [],
		MCA : [],
		NT  : [],
		LCT : [],
		MMT : []
	} ;
	
	$scope.selectedTopic = null ;
	$scope.selectedQuestion = null ;
	$scope.examType = "MAIN" ;
	
	$scope.assembledQuestions = {
		'IIT - Physics'   : [],
		'IIT - Chemistry' : [],
		'IIT - Maths'     : []
	}
	$scope.customDuration = null ;
	$scope.shortName = null ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	loadQBInsights() ;
	setTitle() ;
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.toggleTopicSelection = function( topic ) {
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
		
		if( $scope.selectedQuestion == null ) return ;
		
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
		addQuestionToTargetArray( question, tgtArray ) ;
		
		refreshRampGraph( sType ) ;
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
				if( $scope.selectedTopic != null ) {
					if( question.topic.id == $scope.selectedTopic.topicId ) {
						$scope.questionsForSelectedTopic[ qType ].push( question ) ;
					}
				}
				
				if( i < srcArray.length ) {
					$scope.selectedQuestion = srcArray[i] ;
				}
				else {
					if( srcArray.length > 0 ) {
						$scope.selectedQuestion = srcArray[i-1] ;
					}
				}
				refreshRampGraph( sType ) ;
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
		saveTestOnServer() ;
	}
	
	$scope.shuffleQuestions = function( subjectName, qType ) {
		var questions = null ;
		if( subjectName != null ) {
			questions = $scope.assembledQuestions[ subjectName ] ;
			shuffleAssembledQuestionsArray( questions ) ;
			refreshRampGraph( subjectName ) ;
		}
		else if( qType != null ) {
			questions = $scope.questionsForSelectedTopic[ qType ] ;
			shuffle( questions ) ;
		}
	} 
	
	$scope.sortQuestionsByLatLevel = function( qType ) {
		var questions = $scope.questionsForSelectedTopic[ qType ] ;
		sortQuestionsByAttribute( questions, "lat", scaLatLevelSortDir ) ;
		scaLatLevelSortDir = toggleSortDirection( scaLatLevelSortDir ) ;
    }
    
	$scope.sortQuestionsByProjTime = function( qType ) {
		var questions = $scope.questionsForSelectedTopic[ qType ] ;
		sortQuestionsByAttribute( questions, "proj", scaProjTimeSortDir ) ;
		scaProjTimeSortDir = toggleSortDirection( scaProjTimeSortDir ) ;
    }

	$scope.sortAssembledQuestionsByLatLevel = function( subjectName ) {
		var questions = $scope.assembledQuestions[ subjectName ] ;
		var sortDir   = $scope.assembledQSortDir[ subjectName ].lat ;
		sortQuestionsByAttribute( questions, "lat", sortDir ) ;
		$scope.assembledQSortDir[ subjectName ].lat = toggleSortDirection( sortDir ) ;
		refreshRampGraph( subjectName ) ;
    }
    
	$scope.sortAssembledQuestionsByProjTime = function( subjectName ) {
		var questions = $scope.assembledQuestions[ subjectName ] ;
		var sortDir   = $scope.assembledQSortDir[ subjectName ].proj ;
		sortQuestionsByAttribute( questions, "proj", sortDir ) ;
		$scope.assembledQSortDir[ subjectName ].proj = toggleSortDirection( sortDir ) ;
		refreshRampGraph( subjectName ) ;
    }
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
	function addQuestionToTargetArray( question, tgtArray )  {
		
		var qType = question.questionType ;
		
		if( qType == "NT" ) {
			tgtArray.push( question ) ;
		}
		else {
			var i=0 ;
			for( ; i<tgtArray.length; i++ ) {
				if( tgtArray[i].questionType == "NT" ) {
					break ;
				}
			}
			tgtArray.splice( i, 0, question ) ;
		}
	}
	
	function shuffleAssembledQuestionsArray( questions ) {
		var scaQuestions = [] ;
		var ntQuestions = [] ;
		
		for( var i=0; i<questions.length; i++ ) {
			var question = questions[i] ;
			if( question.questionType == "SCA" ) {
				scaQuestions.push(  question ) ;
			}
			else {
				ntQuestions.push( question ) ;
			}
		} 
		
		shuffle( scaQuestions ) ;
		shuffle( ntQuestions ) ;
		
		questions.length = 0 ;
		questions.push.apply( questions, scaQuestions ) ;
		questions.push.apply( questions, ntQuestions ) ;
	}
	
	function shuffle( array ) {
		  var currentIndex = array.length, temporaryValue, randomIndex;

		  // While there remain elements to shuffle...
		  while (0 !== currentIndex) {

		    // Pick a remaining element...
		    randomIndex = Math.floor(Math.random() * currentIndex);
		    currentIndex -= 1;

		    // And swap it with the current element.
		    temporaryValue = array[currentIndex];
		    array[currentIndex] = array[randomIndex];
		    array[randomIndex] = temporaryValue;
		  }
		  return array;
	}	
	
	function sortQuestionsByAttribute( questions, attribute, dir ) {
		questions.sort( function( q1, q2 ){
			var val1 = ( attribute == "lat" ) ? q1.lateralThinkingLevel : q1.projectedSolveTime ;
			var val2 = ( attribute == "lat" ) ? q2.lateralThinkingLevel : q2.projectedSolveTime ;
			
			if( q1.questionType == q2.questionType ) {
				if( dir == "asc" ) {
					return val1 - val2 ;
				}
				return val2 - val1 ;
			}
			else {
				if( q1.questionType == 'SCA' ) {
					return -1 ;
				}
				return 1 ;
			}
		}) ;
	}
	
	function toggleSortDirection( dir ) {
		return (dir == "asc") ? "desc" : "asc" ;
	}
	
	function setTitle() {
		if( $scope.testId == -1 ) {
			$scope.$parent.navBarTitle = "Editing <NEW TEST>" ;
		}
		else {
			$scope.$parent.navBarTitle = "Editing Test - " + $scope.testId ;			
		}
	}
	
	function loadTestConfiguration( testId ) {
		
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/TestConfiguration/' + testId )
        .then( 
            function( response ){
                console.log( "Successfully loaded test configuration." ) ;
                
                $scope.examType = response.data.examType ;
                $scope.shortName = response.data.shortName ;
                $scope.assembledQuestions[ 'IIT - Physics'   ] = response.data.phyQuestions ;
                $scope.assembledQuestions[ 'IIT - Chemistry' ] = response.data.chemQuestions ;
                $scope.assembledQuestions[ 'IIT - Maths'     ] = response.data.mathQuestions ;

                setTimeout( function(){
                	refreshRampGraph( 'IIT - Physics' ) ;
                	refreshRampGraph( 'IIT - Chemistry' ) ;
                	refreshRampGraph( 'IIT - Maths' ) ;
                }, 500 ) ;
                
                selectTopics( response.data.phyQuestions,  $scope.phyTopics  ) ;
                selectTopics( response.data.chemQuestions, $scope.chemTopics ) ;
                selectTopics( response.data.mathQuestions, $scope.mathTopics ) ;
            }, 
            function( error ){
                console.log( "Error getting test configuration from server." + error ) ;
                $scope.$parent.addErrorAlert( "Could not fetch test." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}
	
	function selectTopics( questions, topics ) {
		
		for( var i=0; i<questions.length; i++ ) {
			var question = questions[i] ;
			for( var j=0; j<topics.length; j++ ) {
				var topic = topics[j] ;
				if( question.topic.id == topic.topicId ) {
					topic.selected = true ;
				}
			}
		}
	}
	
    function saveTestOnServer() {
    	
    	console.log( "Saving test on server." ) ;
    	
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/TestConfiguration', {
        	id             : $scope.testId,
        	examType       : $scope.examType,
        	shortName      : $scope.shortName,
        	customDuration : $scope.customDuration, 
        	phyQuestions   : $scope.assembledQuestions[ 'IIT - Physics' ],
	    	chemQuestions  : $scope.assembledQuestions[ 'IIT - Chemistry' ],
	    	mathQuestions  : $scope.assembledQuestions[ 'IIT - Maths' ]
        })
        .then( 
            function( response ){
                console.log( "Successfully saved test configuration." ) ;
                $scope.testId = response.data.id ;
                setTitle() ;
            }, 
            function( error ){
                console.log( "Error saving test on server." + error ) ;
                $scope.$parent.addErrorAlert( "Could not save test." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
	function changeSelectedQuestionSequence( up ) {
		
		if( $scope.selectedQuestion == null ) return ;
		
		var question = $scope.selectedQuestion ;
		var sType = question.subject.name ;
		var qType = question.questionType ;
		
		var tgtQArray = $scope.assembledQuestions[ sType ] ;

		var scaQuestions = [] ;
		var ntQuestions = [] ;
		for( var i=0; i<tgtQArray.length; i++ ) {
			var q = tgtQArray[i] ;
			if( q.questionType == "SCA" ) {
				scaQuestions.push( q ) ;
			}
			else {
				ntQuestions.push( q ) ;
			}
		}
		
		var lowerBound = -1 ;
		var upperBound = -1 ;
		
		if( qType == "SCA" ) {
			lowerBound = 0 ;
			upperBound = scaQuestions.length - 1 ;
		}
		else {
			lowerBound = scaQuestions.length ;
			upperBound = tgtQArray.length - 1 ;
		}
		
		for( var i=0; i<tgtQArray.length; i++ ) {
			if( tgtQArray[i] == question ) {
				if( up ) {
					if( i > lowerBound ) {
						var temp = tgtQArray[i-1] ;
						tgtQArray[i-1] = question ;
						tgtQArray[i] = temp ;
					}
				} 
				else {
					if( i < upperBound ) {
						var temp = tgtQArray[i+1] ;
						tgtQArray[i+1] = question ;
						tgtQArray[i] = temp ;
					}
				}
				refreshRampGraph( sType ) ;
				return ;
			}
		}
	}
	
	function refreshRampGraph( sType ) {
		
		var canvasId = "" ;
		
		if     ( sType == 'IIT - Physics'   ) { canvasId = "phy_ramp_graph"  ; }
		else if( sType == 'IIT - Chemistry' ) { canvasId = "chem_ramp_graph" ; }
		else if( sType == 'IIT - Maths'     ) { canvasId = "math_ramp_graph" ; }
		
		tgtArray = $scope.assembledQuestions[ sType ] ;
		
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
		
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/QBTopicInsights' )
        .then( 
                function( response ){
                    console.log( response ) ;
                    processRawInsightData( response.data ) ;
            		
            		if( $scope.testId > 0 ) {
            			loadTestConfiguration( $scope.testId ) ;
            		}
                }, 
                function( error ){
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
			
			insight.selected = true ;
			
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
		
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/TestQuestion/Topic/' + topicId + "?questionTypes=SCA,NT" )
        .then( 
                function( response ){
                    console.log( response ) ;
                    
                    // Need to filter the response based on what questions are
                    // already selected, so that the questions are note
                    // repeated across the question combo and assembled lists.
                    $scope.questionsForSelectedTopic = 
                    	filterFreshlyLoadedTopicQuestions( topicId, response.data ) ;
                }, 
                function( error ){
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
						}
					}
				}
			}
		}
		return typeQuestionsMap ;
	}
	
	// --- [END] Local functions
} ) ;