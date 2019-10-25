sConsoleApp.controller( 'JEEMainExamBaseController', function( $scope, $http, $rootScope, $location ) {
    
    // ---------------- Local variables --------------------------------------
    var startTime = 0 ;
    var currentLapIndex = -1 ;
    
    // ---------------- Scope variables --------------------------------------
	
	$scope.alerts = [] ;
	$scope.navBarTitle = "JEEMain Practice Test" ;
	$scope.interactingWithServer = false ;
	
	$scope.paletteHidden = false ;
	$scope.activeTest = null ;
	
	// This is populated with instances of QuestionEx
	$scope.questions = [] ;
	$scope.testConfigIndex = null ;
	$scope.currentQuestion = null ;
	$scope.secondsRemaining = 0 ;
	$scope.timeSpent = 0 ;
	$scope.timerActive = false ;
	$scope.answersSubmitted = false ;
	$scope.testAttempt = {
	    id : 0,
	    testConfig : null,
	    score : 0,
	    timeTaken : 0, 
	    numCorrectAnswers : 0,
	    numWrongAnswers : 0,
	    numNotVisited : 0,
	    numNotAnswered : 0,
	    numAttempted : 0,
	    numMarkedForReview : 0,
	    numAnsAndMarkedForReview : 0
	} ;
	
	$scope.attemptLapNames = [] ;
	$scope.currentLapName = null ;
	$scope.nextLapName = null ;
	$scope.attemptLapDetails = {} ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.resetState = function() {
		$scope.activeTest = null ;
		$scope.alerts.length = 0 ;
		$scope.paletteHidden = false ;
		$scope.questions = [] ;
		$scope.testConfigIndex = null ;
		$scope.currentQuestion = null ;
		$scope.secondsRemaining = 0 ;
		$scope.timerActive = false ;
		$scope.answersSubmitted = false ;
		$scope.testAttempt = {
		    id : 0,
		    testConfig : null,
		    score : 0,
		    timeTaken : 0, 
		    numCorrectAnswers : 0,
		    numWrongAnswers : 0,
		    numNotVisited : 0,
		    numNotAnswered : 0,
		    numAttempted : 0,
		    numMarkedForReview : 0,
		    numAnsAndMarkedForReview : 0
		} ;

	    $scope.attemptLapNames = [] ;
	    $scope.currentLapName = null ;
	    $scope.nextLapName = null ;
	    $scope.attemptLapDetails = {} ;

		startTime = 0 ;
	}
	
	$scope.addErrorAlert = function( msgString ) {
	    $scope.alerts.push( { type: 'danger', msg: msgString } ) ;
	} ;
	
	$scope.dismissAlert = function( index ) {
		$scope.alerts.splice( index, 1 ) ;
	}

    $scope.initializeController = function( attemptLapNames ) {
    	if( $scope.activeTest == null ) {
    		$location.path( "/" ) ;
    	}
    	else {
    		// Remove any scrollbars from the viewport - this is a full screen SPA
    		var elements = document.getElementsByTagName( "body" ) ;
    		elements[0].style.overflowY = "hidden" ;
    		
    		// Note that this route can get transitioned in from either the
    		// instructions page or from the submit confirmation page.
    		// In case we are coming in from the instruction page, the 
    		// questions would need to be freshly loaded and the test started
    		// afresh. However, if we are coming back from the submit confirmation
    		// page, we don't have to recreate the full state - only 
    		// the attempt summary and the section indexes.
    		
    		if( $scope.questions.length == 0 ) {
    			loadTestConfiguration() ;
    		}
    		else {
    			$scope.$broadcast( 'computeSectionIndices' ) ;
    			$scope.showQuestion( $scope.questions[0] ) ;
    		}
    		
    		$scope.attemptLapNames = attemptLapNames ;
    		for( var i=0; i<attemptLapNames.length; i++ ) {
    		    $scope.attemptLapDetails[ attemptLapNames[i] ] = {
    		        startTime : null,
    		        endTime : null,
    		        timeSpent : 0
    		    }
    		}
    	}
    }
    
	$scope.toggleQuestionPalette = function() {
		
	    var palette = document.getElementById( "question-palette-panel" ) ;
	    var display = document.getElementById( "question-display-panel" ) ;
		
		if( $scope.paletteHidden ) {
			palette.style.display = "block" ;
			palette.style.width = "25%" ;
			display.style.width = "75%" ;
			$scope.paletteHidden = false ;
			$scope.saveClickStreamEvent( 
					ClickStreamEvent.prototype.CONTROL_PANEL_EXPANDED, 
					null ) ;
		}
		else {
			palette.style.display = "none" ;
			palette.style.width = "0%" ;
			display.style.width = "100%" ;
			$scope.paletteHidden = true ;
			$scope.saveClickStreamEvent( 
					ClickStreamEvent.prototype.CONTROL_PANEL_COLLAPSED, 
					null ) ;
		}
	}
	
	$scope.startTimer = function() {
		$scope.timerActive = true ;
		setTimeout( handleTimerEvent, 1000 ) ;
	}

    $scope.saveAndNext = function() {
    	console.log( "Saving answer and showing next question." ) ;
    	if( $scope.currentQuestion.interactionHandler.isAnswered() ) {
    		$scope.currentQuestion.attemptState = AttemptState.prototype.ATTEMPTED ;
    		$scope.saveClickStreamEvent( 
    				ClickStreamEvent.prototype.ANSWER_SAVE, 
    				"" + $scope.currentQuestion.question.id ) ;
    		
    		$scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    	}
    	else {
            alert( "Please provide a valid answer before saving." ) ;
    	}
    }
    
    $scope.saveAndMarkForReview = function() {
    	if( $scope.currentQuestion.interactionHandler.isAnswered() ) {
        	$scope.currentQuestion.attemptState = AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ;
    		$scope.saveClickStreamEvent( 
    				ClickStreamEvent.prototype.ANSWER_SAVE_AND_MARK_REVIEW, 
    				"" + $scope.currentQuestion.question.id ) ;
    		
        	$scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    	}
    	else {
    		alert( "Please provide a valid answer before saving." ) ;
    	}
    }
    
    $scope.clearResponse = function() {
    	$scope.currentQuestion.interactionHandler.clearResponse() ;
		$scope.saveClickStreamEvent( 
				ClickStreamEvent.prototype.ANSWER_CLEAR_RESPONSE, 
				"" + $scope.currentQuestion.question.id ) ;
    }
    
    $scope.markForReviewAndNext = function() {
    	$scope.currentQuestion.attemptState = AttemptState.prototype.MARKED_FOR_REVIEW ;
		$scope.saveClickStreamEvent( 
				ClickStreamEvent.prototype.ANSWER_MARK_FOR_REVIEW, 
				"" + $scope.currentQuestion.question.id ) ;
		
    	$scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    }
    
    $scope.showQuestion = function( questionEx ) {
    	if( questionEx != null ) {
    		$scope.currentQuestion = questionEx ;
    		if( $scope.currentQuestion.attemptState == AttemptState.prototype.NOT_VISITED ) {
    			$scope.currentQuestion.attemptState = AttemptState.prototype.NOT_ANSWERED ;
    		}
    		$scope.saveClickStreamEvent( 
    				ClickStreamEvent.prototype.QUESTION_VISITED, 
    				"" + questionEx.question.id ) ;
    	}
    	$scope.$broadcast( 'refreshAttemptSummary', questionEx ) ;
    }
    
    $scope.showNextQuestion = function() {
		$scope.saveClickStreamEvent( 
				ClickStreamEvent.prototype.NEXT_QUESTION, 
				null ) ;
    	$scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    }
    
    $scope.showPreviousQuestion = function() {
		$scope.saveClickStreamEvent( 
				ClickStreamEvent.prototype.BACK_QUESTION, 
				null ) ;
		$scope.showQuestion( $scope.currentQuestion.prevQuestion ) ;
    }
    
    $scope.scrollBottom = function() {
		$scope.saveClickStreamEvent( 
				ClickStreamEvent.prototype.SCROLL_BOTTOM, 
				$scope.currentQuestion.question.id ) ;
    	scrollToElement( "q_bottom" ) ;
    }
    
    $scope.scrollTop = function() {
		$scope.saveClickStreamEvent( 
				ClickStreamEvent.prototype.SCROLL_TOP, 
				$scope.currentQuestion.question.id ) ;
    	scrollToElement( "q_top" ) ;
    }
    
    $scope.showQuestionPaper = function() {
		$scope.saveClickStreamEvent( 
				ClickStreamEvent.prototype.QUESTION_PAPER_VIEW_START, 
				null ) ;
    }
    
    $scope.hideQuestionPaper = function() {
		$scope.saveClickStreamEvent( 
				ClickStreamEvent.prototype.QUESTION_PAPER_VIEW_END, 
				null ) ;
    }
    
    $scope.submitAnswers = function() {
        
        endCurrentLap() ;
    	
    	$scope.timerActive = false ;
    	$scope.answersSubmitted = true ;
    	
    	if( !$scope.$$phase ) {
    		$scope.$apply(function(){
    			$location.path( "/testResult" ) ;
    		});    	
    	}
    	else {
    		$location.path( "/testResult" ) ;
    	}
    	
		var elements = document.getElementsByTagName( "body" ) ;
		elements[0].style.overflowY = "auto" ;
    }
    
    $scope.getControlDashboardQuestionButtonStyle = function( questionEx ) {
    	
    	var style = questionEx.getStatusStyle() ;
    	if( $scope.currentQuestion == questionEx ) {
    		style += " q-control-border" ;
    	}
    	return style ;
    }
    
    $scope.saveTestAttempt = function() {
    	
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/TestAttempt', $scope.testAttempt )
        .then ( 
            function( response ){
                $scope.testAttempt = response.data ;
                $scope.saveClickStreamEvent( 
                		ClickStreamEvent.prototype.TEST_STARTED, null ) ;

                startNextLap() ;
                $scope.showQuestion( $scope.questions[0] ) ;
            }, 
            function( error ){
                console.log( "Error saving test attempt on server." ) ;
                $scope.$parent.addErrorAlert( "Could not save test attempt." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    $scope.endTestAttempt = function() {
        
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/TestAttempt', $scope.testAttempt )
        .then ( 
            function( response ){
                $scope.testAttempt = response.data ;
                $scope.saveClickStreamEvent( 
                        ClickStreamEvent.prototype.TEST_ENDED, null ) ;
            }, 
            function( error ){
                console.log( "Error saving test attempt on server." ) ;
                $scope.$parent.addErrorAlert( "Could not save test attempt." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    $scope.saveClickStreamEvent = function( eventId, payload ) {
    	
    	var timeMarker = (new Date()).getTime() - startTime ;
    	console.log( "ClickStreamEvent[ " + 
    			        "eventId = " + eventId + "," + 
    			        "timeMarker = " + timeMarker + ", " + 
    			        "payload = " + payload + "]" ) ;
    	
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/ClickStreamEvent', {
        	'eventId'       : eventId,
        	'timeMarker'    : timeMarker,
        	'payload'       : payload,
        	'testAttemptId' : $scope.testAttempt.id
        } )
        .then ( 
            function( response ){
            }, 
            function( error ){
                console.log( "Error saving click stream event." ) ;
                $scope.$parent.addErrorAlert( "Could not save click stream event." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    $scope.endCurrentLapAndStartNextLap = function() {
        endCurrentLap() ;
        startNextLap() ;
    }
    
	// --- [END] Scope functions

	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    function startNextLap() {
        if( currentLapIndex < $scope.attemptLapNames.length-1 ) {
            currentLapIndex++ ;
            
            // Set the lap names, so that the UI displays properly
            $scope.currentLapName = $scope.attemptLapNames[ currentLapIndex ] ;
            if( currentLapIndex < $scope.attemptLapNames.length-1 ) {
                $scope.nextLapName = $scope.attemptLapNames[ currentLapIndex + 1 ] ;
            }
            else {
                $scope.nextLapName = "" ;
            }
            
            var attemptDetail = $scope.attemptLapDetails[ $scope.currentLapName ] ;
            attemptDetail.startTime = new Date() ;
            $scope.saveClickStreamEvent( ClickStreamEvent.prototype.LAP_START, 
                                         $scope.currentLapName ) ;
        }
    }
    
    function endCurrentLap() {
        
        var attemptDetail = $scope.attemptLapDetails[ $scope.currentLapName ] ;
        attemptDetail.endTime = new Date() ;
        
        $scope.saveClickStreamEvent( ClickStreamEvent.prototype.LAP_END, 
                                     $scope.currentLapName ) ;

        var snapshots = [] ;
        
        for( var i=0; i<$scope.questions.length; i++ ) {
            var q = $scope.questions[i] ;
            var lapDetails = q.lapDetails[ $scope.currentLapName ] ;
            
            lapDetails.attemptState = q.attemptState ;
            
            snapshots.push({
                testAttemptId  : $scope.testAttempt.id ,
                questionId     : q.question.id ,
                lapName        : $scope.currentLapName,
                timeSpent      : lapDetails.timeSpent, 
                attemptStatus  : q.attemptState
            }) ;
        }
        
        saveTestAttemptLapSnapshots( snapshots ) ;
    }
    
    function saveTestAttemptLapSnapshots( snapshots ) {
        
        console.log( "Saving test attempt lap snapshots." ) ;
        
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/TestAttempt/LapSnapshot', snapshots )
        .then ( 
            function( response ){
                console.log( "Successfully saved test lap snapshot." ) ;
            }, 
            function( error ){
                console.log( "Error saving test lap snapshot on server." ) ;
                $scope.$parent.addErrorAlert( "Could not save test attempt lap snapshot." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }

    function loadTestConfiguration() {
    	
        $scope.interactingWithServer = true ;
        $http.get( '/TestConfiguration/' + $scope.activeTest.id )
        .then( 
            function( response ){
                console.log( response.data ) ;
                
                preProcessQuestions( response.data ) ;
                $scope.testConfigIndex = response.data.testConfigIndex ;
                
                $scope.$broadcast( 'computeSectionIndices' ) ;
                
                $scope.secondsRemaining = $scope.activeTest.duration * 60 ;
                $scope.startTimer() ;
                
        		startTime = (new Date()).getTime() ;
        		$scope.testAttempt.testConfig = $scope.testConfigIndex ;
        		$scope.saveTestAttempt() ;
            }, 
            function( error ){
                console.log( "Error getting test configuration from server." + error ) ;
                $scope.addErrorAlert( "Could not fetch test." ) ;
            }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
    
    function preProcessQuestions( testConfig ) {
    	
        var phyQuestions  = testConfig.phyQuestions ;
        var chemQuestions = testConfig.chemQuestions ;
        var mathQuestions = testConfig.mathQuestions ;
        
        if( phyQuestions.length > 0 ) {
        	enhanceQuestions( phyQuestions ) ;
        }
        
        if( chemQuestions.length > 0 ) {
        	enhanceQuestions( chemQuestions ) ;
        }
        
        if( mathQuestions.length > 0 ) {
        	enhanceQuestions( mathQuestions ) ;
        }
    }
    
    function enhanceQuestions( questions ) {
    	
    	for( var i=0; i<questions.length; i++ ) {
    		
    		var question = questions[i] ;
    		var questionEx = new QuestionEx( question, $scope.attemptLapNames ) ;
    		var lastQuestionEx = null ;
    		
    		if( $scope.questions.length > 0 ) {
    			lastQuestionEx = $scope.questions[ $scope.questions.length-1 ] ;
    			
    			lastQuestionEx.nextQuestion = questionEx ;
    			questionEx.prevQuestion = lastQuestionEx ;
    		}
    		
    		questionEx.index = $scope.questions.length ;
    		questionEx.timeSpent = 0 ;
    		associateInteractionHandler( questionEx ) ;
    		
    		$scope.questions.push( questionEx ) ;
    	}
    }
    
    function associateInteractionHandler( questionEx ) {
    	
    	if( questionEx.question.questionType == "SCA" ) {
    		questionEx.interactionHandler = new SCAInteractionHandler( questionEx, $rootScope ) ;
    	}
    	else {
    		questionEx.interactionHandler = new NTInteractionHandler( questionEx, $rootScope ) ;
    	}
    }
    
    function scrollToElement( id ) {
    	var myElement = document.getElementById( id ) ;
    	var topPos = myElement.offsetTop ;
    	document.getElementById( 'question-display-scroll-pane' ).scrollTop = topPos ;
    }
    
    function handleTimerEvent() {
    	$scope.secondsRemaining-- ;
    	$scope.timeSpent++ ;
    	
    	$scope.attemptLapDetails[ $scope.currentLapName ].timeSpent++ ;
    	
    	if( $scope.currentQuestion != null ) {
    		$scope.currentQuestion.timeSpent++ ;
    		$scope.currentQuestion.lapDetails[ $scope.currentLapName ].timeSpent++ ;
    	}
    	
    	if( $scope.secondsRemaining > 0 && $scope.timerActive ) {
    		setTimeout( handleTimerEvent, 1000 ) ;
    	}
    	else {
    		$scope.timerActive = false ;
    		$scope.secondsRemaining = 0 ;
    		if( !$scope.answersSubmitted ) {
    			$scope.submitAnswers() ;
    		}
    	}
    	$scope.$digest() ;
    }
    
	// --- [END] Local functions
} ) ;