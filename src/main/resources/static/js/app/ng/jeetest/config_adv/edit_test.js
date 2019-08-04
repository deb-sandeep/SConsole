sConsoleApp.controller( 'EditAdvTestController', function( $scope, $http, $routeParams, $location ) {
	
	$scope.$parent.navBarTitle = "Create New Test" ;
	$scope.questionTypes = [ "SCA", "MCA", "NT", "LCT", "MMT" ] ;
	
	$scope.testId = $routeParams.id ;
	$scope.examType = "ADV" ;

	$scope.sectionSelectionOptions = {
		sectionTypes : [ 'SCA', 'MCA', 'NT', 'LCT', 'MMT' ],
		section1Type : 'MCA',
		section2Type : 'NT',
		section3Type : 'LCT'
	} ;
	
	$scope.selectedSectionTypes = {
		section1Type : "XXX",
		section2Type : "XXX",
		section3Type : "XXX"
	} ;
	
	$scope.phyTopics = [] ;
	$scope.chemTopics = [] ;
	$scope.mathTopics = [] ;
	
	$scope.assembledQuestions = {
		'IIT - Physics'   : {},
		'IIT - Chemistry' : {},
		'IIT - Maths'     : {}
	}
	
	$scope.selectedTopic = null ;
	$scope.selectedSubject = null ;
	$scope.questionsForSelectedTopic = {
		SCA : [],
		MCA : [],
		NT  : [],
		LCT : [],
		MMT : []
	} ;
	$scope.assembledQuestionsForSelectedSubject = null ;
	
	$scope.selectedQuestion = null ;
		
	var topicQSortDir = {} ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	loadQBInsights() ;
	if( $scope.testId == -1 ) {
		showSectionSelectionDialog() ;
	}
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	$scope.dismissSectionSelections = function() {
		$( '#sectionSelectionDialog' ).modal( 'hide' ) ;
		$location.path( "/" ) ;
	}
	
	$scope.applySectionSelections = function() {
		
		var sels = [ $scope.sectionSelectionOptions.section1Type,
					 $scope.sectionSelectionOptions.section2Type,
		             $scope.sectionSelectionOptions.section3Type ] ;
		
		var types = [ sels[0] ] ;
		for( var i=1; i<sels.length; i++ ) {
			if( types.indexOf( sels[i] ) != -1 ) {
				$scope.$parent.addErrorAlert( "Sections can't be of same type." ) ;
				return ;
			} 
			else {
				types.push( sels[i] ) ;
			}
		}
		
		for( var i=0; i<sels.length; i++ ) {
			$scope.assembledQuestions[ 'IIT - Physics'   ][ sels[i] ] = [] ;
			$scope.assembledQuestions[ 'IIT - Chemistry' ][ sels[i] ] = [] ;
			$scope.assembledQuestions[ 'IIT - Maths'     ][ sels[i] ] = [] ;
			
			topicQSortDir[ sels[i] ] = {
				projTimeSortDir : 'asc',
				latLevelSortDir : 'asc'
			} ;
		}

		$scope.selectedSectionTypes.section1Type = sels[0] ;
		$scope.selectedSectionTypes.section2Type = sels[1] ;
		$scope.selectedSectionTypes.section3Type = sels[2] ;
		
		$( '#sectionSelectionDialog' ).modal( 'hide' ) ;
	}
	
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
		$scope.selectedSubject = $scope.selectedTopic.subjectName ;
		$scope.assembledQuestionsForSelectedSubject = $scope.assembledQuestions[ $scope.selectedSubject ] ;
		loadQuestionsForTopic( $scope.selectedTopic.topicId, $scope.examType ) ;
		
		refreshRampGraph( $scope.selectedSectionTypes.section1Type ) ;
		refreshRampGraph( $scope.selectedSectionTypes.section2Type ) ;
		refreshRampGraph( $scope.selectedSectionTypes.section3Type ) ;
	}
	
	$scope.shuffleTopicQuestions = function( qType ) {
		var questions = $scope.questionsForSelectedTopic[ qType ] ;
		shuffle( questions ) ;
	} 
	
	$scope.sortTopicQuestionsByLatLevel = function( qType ) {
		var curSortDir = topicQSortDir[ qType ].latLevelSortDir ;
		var questions = $scope.questionsForSelectedTopic[ qType ] ;
		sortQuestionsByAttribute( questions, "lat", curSortDir ) ;
		topicQSortDir[ qType ].latLevelSortDir = toggleSortDirection( curSortDir ) ;
    }
    
	$scope.sortTopicQuestionsByProjTime = function( qType ) {
		var curSortDir = topicQSortDir[ qType ].projTimeSortDir ;
		var questions = $scope.questionsForSelectedTopic[ qType ] ;
		sortQuestionsByAttribute( questions, "proj", curSortDir ) ;
		topicQSortDir[ qType ].projTimeSortDir = toggleSortDirection( curSortDir ) ;
    }

	$scope.questionSelectedForTest = function() {
		
		if( $scope.selectedQuestion == null ) return ;
		
		var qType = $scope.selectedQuestion.questionType ;
		var sType = $scope.selectedQuestion.subject.name ;
		
		var srcArray = $scope.questionsForSelectedTopic[ qType ] ;
		var tgtArray = $scope.assembledQuestions[ sType ][ qType ] ;
		
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
		
		refreshRampGraph( qType ) ;
	}
	
	$scope.getTotalQuestions = function( subject, qType ) {
		
		if( $scope.selectedSectionTypes.section1Type == 'XXX' ) return 0 ;
		
		if( subject == null && qType == null ) {
			
			return $scope.getTotalQuestions( 'IIT - Physics', null ) + 
		           $scope.getTotalQuestions( 'IIT - Chemistry', null ) + 
		           $scope.getTotalQuestions( 'IIT - Maths', null ) ;
		}
		else if( subject != null && qType == null ) {
			
			var subAssembledQs = $scope.assembledQuestions[subject] ;
			var qTypes = $scope.selectedSectionTypes ;
			
			return subAssembledQs[qTypes.section1Type].length + 
				   subAssembledQs[qTypes.section2Type].length + 
			       subAssembledQs[qTypes.section3Type].length ;
		}
		else if( subject == null && qType != null ) {
			
			return $scope.getTotalQuestions( 'IIT - Physics', qType ) + 
			       $scope.getTotalQuestions( 'IIT - Chemistry', qType ) + 
			       $scope.getTotalQuestions( 'IIT - Maths', qType ) ;
		}
		else {
			return $scope.assembledQuestions[subject][qType].length ;
		}
	}
	
	$scope.getTotalTime = function( subject, qType ) {
		
		if( $scope.selectedSectionTypes.section1Type == 'XXX' ) return 0 ;
		
		if( subject == null && qType == null ) {
			
			return $scope.getTotalTime( 'IIT - Physics', null ) + 
		           $scope.getTotalTime( 'IIT - Chemistry', null ) + 
		           $scope.getTotalTime( 'IIT - Maths', null ) ;
		}
		else if( subject != null && qType == null ) {
			
			return $scope.getTotalTime( subject, $scope.selectedSectionTypes.section1Type ) + 
			       $scope.getTotalTime( subject, $scope.selectedSectionTypes.section2Type ) +
			       $scope.getTotalTime( subject, $scope.selectedSectionTypes.section3Type ) ;
		}
		else if( subject == null && qType != null ) {
			
			return $scope.getTotalTime( 'IIT - Physics', qType ) + 
			       $scope.getTotalTime( 'IIT - Chemistry', qType ) + 
			       $scope.getTotalTime( 'IIT - Maths', qType ) ;
		}
		else {
			
			var questions = $scope.assembledQuestions[subject][qType] ;
			var time = 0 ;
			
			for( var i=0; i<questions.length; i++ ) {
				time += questions[i].projectedSolveTime ;
			}
			return time ;
		}
	}
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
	function showSectionSelectionDialog() {
		$scope.sectionSelectionOptions.section1Type = 'MCA' ;
		$scope.sectionSelectionOptions.section2Type = 'NT' ;
		$scope.sectionSelectionOptions.section3Type = 'LCT' ;
		$( '#sectionSelectionDialog' ).modal( 'show' ) ;
	}
	
	function loadQBInsights() {
		
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/QBTopicInsights' )
        .then( 
                function( response ){
                    console.log( response ) ;
                    processRawInsightData( response.data ) ;
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
		
		var qTypes = $scope.selectedSectionTypes.section1Type + "," +
		             $scope.selectedSectionTypes.section2Type + "," +
		             $scope.selectedSectionTypes.section3Type ;
		
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/TestQuestion/Topic/' + topicId + "?questionTypes=" + qTypes )
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
	
	// Filter (prune/remove) the questions from typeQuestionsMap which are
	// present in the assembled questions map. This will ensure that we 
	// do not create a situation where the user can add duplicate questions.
	function filterFreshlyLoadedTopicQuestions( topicId, typeQuestionsMap ) {
		
		for( var i=0; i<$scope.questionTypes.length; i++ ) {
			
			var qType = $scope.questionTypes[i] ;
			var srcArray = typeQuestionsMap[ qType ] ;
			
			// If there are no questions for the question type, continue
			if( srcArray.length == 0 ) continue ;
			
			// If there are questions, find the subject name which will be
			// used to lookup assembled questions
			var sType = srcArray[0].subject.name ;
			
			var assembledQuestionsMap = $scope.assembledQuestions[ sType ] ;
			
			// Remember that JEE advanced is section based and during configuration
			// the user is allowed to choose sections. The following check
			// ensures that if the question belongs to a type which is not
			// selected, we don't really care - it will never be shown to the user.
			if( !assembledQuestionsMap.hasOwnProperty( qType ) ) {
				continue ;
			}
			
			var assembledQuestions = assembledQuestionsMap[ qType ] ;
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
			
			if( dir == "asc" ) {
				return val1 - val2 ;
			}
			return val2 - val1 ;
		}) ;
	}
	
	function toggleSortDirection( dir ) {
		return (dir == "asc") ? "desc" : "asc" ;
	}
	
	function refreshRampGraph( qType ) {
		
		if( $scope.selectedSubject == null ) return ;
		
		var canvasId = qType + "_ramp_graph" ;
		
		tgtArray = $scope.assembledQuestionsForSelectedSubject[ qType ] ;
		
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
	
	// --- [END] Local functions
} ) ;