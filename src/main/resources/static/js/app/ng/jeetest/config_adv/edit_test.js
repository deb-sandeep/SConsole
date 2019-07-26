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
	
	$scope.selectedTopic = null ;
	$scope.questionsForSelectedTopic = {
		SCA : [],
		MCA : [],
		NT  : [],
		LCT : [],
		MMT : []
	} ;
	
	$scope.selectedQuestion = null ;
		
	$scope.assembledQuestions = {
		'IIT - Physics'   : {},
		'IIT - Chemistry' : {},
		'IIT - Maths'     : {}
	}
	
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
		loadQuestionsForTopic( $scope.selectedTopic.topicId, $scope.examType ) ;
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
	
	// --- [END] Local functions
} ) ;