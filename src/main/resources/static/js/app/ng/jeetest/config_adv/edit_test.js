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
	} ;
	
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
    $scope.customDuration = null ;
    $scope.shortName = null ;
		
	var topicQSortDir = {} ;
	var assembledQSortDir = {} ;
	
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
			assembledQSortDir[ sels[i] ] = {
				projTimeSortDir : 'asc',
				latLevelSortDir : 'asc'
			} ;
		}

		$scope.selectedSectionTypes.section1Type = sels[0] ;
		$scope.selectedSectionTypes.section2Type = sels[1] ;
		$scope.selectedSectionTypes.section3Type = sels[2] ;
		
		$( '#sectionSelectionDialog' ).modal( 'hide' ) ;
		
		setTimeout( function(){
			for( var i=0; i<sels.length; i++ ) {
				adjustCanvasWidth( sels[i] ) ;
			}
		}, 100 ) ;
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
	
	$scope.shuffleAssembledQuestions = function( secType ) {
		
		var questionsForAllSections = $scope.assembledQuestionsForSelectedSubject ;
		var questions = questionsForAllSections[ secType ] ;
		
		if( questions.length > 0 ) {
			shuffle( questions ) ;
			refreshRampGraph( secType ) ;
		}
	}
	
	$scope.sortTopicQuestionsByLatLevel = function( qType ) {
		
		var curSortDir = topicQSortDir[ qType ].latLevelSortDir ;
		var questions = $scope.questionsForSelectedTopic[ qType ] ;
		sortQuestionsByAttribute( questions, "lat", curSortDir ) ;
		topicQSortDir[ qType ].latLevelSortDir = toggleSortDirection( curSortDir ) ;
    }
	
	$scope.sortAssembledQuestionsByLatLevel = function( qType ) {
		
		var curSortDir = assembledQSortDir[ qType ].latLevelSortDir ;
		var questionsForAllSections = $scope.assembledQuestionsForSelectedSubject ;
		var questions = questionsForAllSections[ qType ] ;
		
		sortQuestionsByAttribute( questions, "lat", curSortDir ) ;
		assembledQSortDir[ qType ].latLevelSortDir = toggleSortDirection( curSortDir ) ;
		
		refreshRampGraph( qType ) ;
	}
    
	$scope.sortTopicQuestionsByProjTime = function( qType ) {
		var curSortDir = topicQSortDir[ qType ].projTimeSortDir ;
		var questions = $scope.questionsForSelectedTopic[ qType ] ;
		sortQuestionsByAttribute( questions, "proj", curSortDir ) ;
		topicQSortDir[ qType ].projTimeSortDir = toggleSortDirection( curSortDir ) ;
    }

	$scope.sortAssembledQuestionsByProjTime = function( qType ) {
		
		var curSortDir = assembledQSortDir[ qType ].projTimeSortDir ;
		var questionsForAllSections = $scope.assembledQuestionsForSelectedSubject ;
		var questions = questionsForAllSections[ qType ] ;
		
		sortQuestionsByAttribute( questions, "proj", curSortDir ) ;
		assembledQSortDir[ qType ].projTimeSortDir = toggleSortDirection( curSortDir ) ;
		
		refreshRampGraph( qType ) ;
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
	
	$scope.getTotalMarks = function( subject, qType ) {
		
		if( $scope.selectedSectionTypes.section1Type == 'XXX' ) return 0 ;
		
		if( subject == null && qType == null ) {
			
			return $scope.getTotalMarks( 'IIT - Physics', null ) + 
		           $scope.getTotalMarks( 'IIT - Chemistry', null ) + 
		           $scope.getTotalMarks( 'IIT - Maths', null ) ;
		}
		else if( subject != null && qType == null ) {
			
			return $scope.getTotalMarks( subject, $scope.selectedSectionTypes.section1Type ) + 
			       $scope.getTotalMarks( subject, $scope.selectedSectionTypes.section2Type ) +
			       $scope.getTotalMarks( subject, $scope.selectedSectionTypes.section3Type ) ;
		}
		else if( subject == null && qType != null ) {
			
			return $scope.getTotalMarks( 'IIT - Physics', qType ) + 
			       $scope.getTotalMarks( 'IIT - Chemistry', qType ) + 
			       $scope.getTotalMarks( 'IIT - Maths', qType ) ;
		}
		else {
			var numQuestions = $scope.assembledQuestions[subject][qType].length ;
			var marksPerQuestion = 0 ;
			
			if( numQuestions == 0 ) return 0 ;
			
		    if( qType == 'SCA' ) {
		        marksPerQuestion = 3 ;
		    }
		    else if( qType == 'MCA' ) {
		        marksPerQuestion = 4 ;
		    }
		    else if( qType == 'NT' ) {
		        marksPerQuestion = 3 ;
		    }
		    else if( qType == 'LCT' ) {
		        marksPerQuestion = 3 ;
		    }
		    else if( qType == 'MMT' ) {
		        marksPerQuestion = 3 ;
		    }
		    
		    return numQuestions * marksPerQuestion ;
		}
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
		
		var srcArray = $scope.assembledQuestions[ sType ][ qType ] ;
		
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
				refreshRampGraph( qType ) ;
				break ;
			}
		}
	}
	
	$scope.saveTest = function() {
		saveTestOnServer() ;
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
	
	function loadTestConfiguration( testId ) {
		
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/TestConfiguration/' + testId )
        .then( 
            function( response ){
                console.log( "Successfully loaded test configuration." ) ;
                console.log( response ) ;
                deserializeTestConfiguration( response.data ) ;
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

	function deserializeTestConfiguration( config ) {
		
		$scope.testId = config.id ;
		$scope.examType = config.examType ;
        $scope.shortName = config.shortName ;

		setTitle() ;

		// Identify and select the topics for which there are assembled questions
        deserializeSelectedTopics( config.phyQuestions,  $scope.phyTopics  ) ;
		deserializeSelectedTopics( config.chemQuestions, $scope.chemTopics ) ;
		deserializeSelectedTopics( config.mathQuestions, $scope.mathTopics ) ;
		
		reverseEngineerSectionTypes( config.allQuestions ) ;
		$scope.applySectionSelections() ;
		
	    arrangeAssembledQuestions( config.allQuestions ) ;
	}
	
	function deserializeSelectedTopics( questions, topics ) {
		
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
	
	function reverseEngineerSectionTypes( allQuestions ) {
		
		var sectionTypeSet = new Set() ;
		for( var i=0; i<allQuestions.length; i++ ) {
			sectionTypeSet.add( allQuestions[i].questionType ) ;
		}
		
		var index = 0 ;
		while( sectionTypeSet.size < 3 && index < $scope.questionTypes.length ) {
			sectionTypeSet.add( $scope.questionTypes[index] ) ;
			index++;
		}
		
		var sectionTypeArray = Array.from( sectionTypeSet ) ;
		var sectionSequenceLookup = {
			'SCA' : 1,
			'MCA' : 2,
			'NT'  : 3,
			'LCT' : 4,
			'MMT' : 5
		} ;
		
		sectionTypeArray.sort( function( typeA, typeB ) {
			return sectionSequenceLookup[typeA] - sectionSequenceLookup[typeB] ;
		} ) ;
		
		$scope.sectionSelectionOptions.section1Type = sectionTypeArray[0] ;
		$scope.sectionSelectionOptions.section2Type = sectionTypeArray[1] ;
		$scope.sectionSelectionOptions.section3Type = sectionTypeArray[2] ;
	}
	
	function arrangeAssembledQuestions( allQuestions ) {
		
		for( var i=0; i<allQuestions.length; i++ ) {
			var question = allQuestions[i] ;
			var sType = question.subject.name ;
			var qType = question.questionType ;
			
			var tgtArray = $scope.assembledQuestions[ sType ][ qType ] ;
			tgtArray.push( question ) ;
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
	
	function adjustCanvasWidth( secType ) {
		var canvasId = secType + "_ramp_graph" ;
		var canvas = document.getElementById( canvasId ) ;
		var $td = $('canvas').parent() ;
		canvas.width = $td.width() ;
		canvas.height = $td.height() ;		
	}
	
	function changeSelectedQuestionSequence( up ) {
		
		if( $scope.selectedQuestion == null ) return ;
		
		var question = $scope.selectedQuestion ;
		var sType = question.subject.name ;
		var qType = question.questionType ;
		var tgtQArray = $scope.assembledQuestions[ sType ][ qType ] ;
		
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
				refreshRampGraph( qType ) ;
				return ;
			}
		}
	}
	
    function saveTestOnServer() {
    	
    	console.log( "Saving test on server." ) ;
    	
    	var phyQs  = getConcatenatedAssembledQuestions( 'IIT - Physics' ) ;
    	var chemQs = getConcatenatedAssembledQuestions( 'IIT - Chemistry' ) ;
    	var mathQs = getConcatenatedAssembledQuestions( 'IIT - Maths' ) ;
    	
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/TestConfiguration', {
        	id             : $scope.testId,
        	examType       : $scope.examType,
        	shortName      : $scope.shortName,
            customDuration : $scope.customDuration, 
        	phyQuestions   : phyQs,
	    	chemQuestions  : chemQs,
	    	mathQuestions  : mathQs
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
    
    function getConcatenatedAssembledQuestions( subjectName ) {
    	
    	var qMap = $scope.assembledQuestions[ subjectName ] ;
    	var secTypes = $scope.selectedSectionTypes ;
    	
    	var sec1Qs = qMap[ secTypes.section1Type ] ;
    	var sec2Qs = qMap[ secTypes.section2Type ] ;
    	var sec3Qs = qMap[ secTypes.section3Type ] ;
    	
    	var questions = sec1Qs.concat( sec2Qs ).concat( sec3Qs ) ;
    	return questions ;
    }
    
	function setTitle() {
		if( $scope.testId == -1 ) {
			$scope.$parent.navBarTitle = "Editing <NEW TEST>" ;
		}
		else {
			$scope.$parent.navBarTitle = "Editing Test - " + $scope.testId ;			
		}
	}
	
	// --- [END] Local functions
} ) ;