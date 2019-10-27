function SectionStats() {
    
    this.numQuestions = 0 ;
    this.numCorrectAnswers = 0 ;
    this.numWrongAnswers = 0 ;
    this.numNotVisited = 0 ;
    this.numNotAnswered = 0 ;
    this.numAttempted = 0 ;
    this.numMarkedForReview = 0 ;
    this.numAnsAndMarkedForReview = 0 ;
}

function Section() {
    
    this.displayName = null ;
    this.subject = null ;
    this.questions = [] ;
    this.stats = new SectionStats() ;
    this.nextSection = null ;
}

sConsoleApp.controller( 'JEEAdvExamBaseController', function( $scope, $http, $rootScope, $location, $window ) {
    
    // ---------------- Local variables --------------------------------------
    var startTime = 0 ;
    var currentLapIndex = -1 ;
    
    // ---------------- Scope variables --------------------------------------

    // Initialization context - these are populated during initialization
    // and are considered read only going forward.
    $scope.testConfigIndex = null ;
    $scope.attemptLapNames = [] ;
    $scope.overallSection = new Section() ;
    $scope.sections = [] ;
    
    // UI related variables. 
    $scope.alerts = [] ;
    $scope.navBarTitle = "JEE Advanced Practice Test" ;
    $scope.paletteHidden = false ;
    
    // Exam scope related variables.
    $scope.interactingWithServer = false ;
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
    $scope.attemptLapDetails = {} ;
    
    // Current question scope related variables
    $scope.currentQuestion = null ;
    $scope.currentLapName = null ;
    $scope.nextLapName = null ;
    
    // -----------------------------------------------------------------------
    // --- [START] Controller initialization ---------------------------------
    
    console.log( "Loading JEEAdvExamBaseController" ) ;
    
    // --- [END] Controller initialization -----------------------------------
    
    // -----------------------------------------------------------------------
    // --- [START] Scope functions -------------------------------------------
    
    // This is called from the initialization logic of the JEEAdvTestController
    // as JEEAdvTestController is loaded as the default route
    $scope.initializeController = function( attemptLapNames ) {
        
        console.log( "Initializing JEEAdvExamBaseController" ) ;
        
        var parameters = new URLSearchParams( window.location.search ) ;
        var testId = parameters.get( 'id' ) ;
        
        if( testId == null || $scope.answersSubmitted == true ) {
            $window.location.href = "/jeetest" ;
        }
        else {
            // Remove any scroll bars from the viewport - this is a 
            // full screen single page application.
            var elements = document.getElementsByTagName( "body" ) ;
            elements[0].style.overflowY = "hidden" ;
            
            loadTestConfiguration( testId ) ;

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
    
    $scope.resetState = function() {
        
        $scope.testConfigIndex = null ;
        $scope.attemptLapNames = [] ;
        $scope.overallSection = new Section() ;
        $scope.sections = [] ;
        
        $scope.alerts.length = 0 ;
        $scope.paletteHidden = false ;
        
        $scope.interactingWithServer = false ;
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
        
        // This contains an array of objects, each tracking start time, 
        // end time and time spent for each corresponding lap
        $scope.attemptLapDetails = {} ;

        $scope.currentQuestion = null ;
        $scope.currentLapName = null ;
        $scope.nextLapName = null ;

        startTime = 0 ;
        currentLapIndex = -1 ;
    }
    
    $scope.startTimer = function() {
        $scope.timerActive = true ;
        setTimeout( handleTimerEvent, 1000 ) ;
    }

    $scope.saveClickStreamEvent = function( eventId, payload ) {
        
        var timeMarker = (new Date()).getTime() - startTime ;
        console.log( "ClickStreamEvent[ " + 
                        "eventId = " + eventId + "," + 
                        "timeMarker = " + timeMarker + ", " + 
                        "payload = " + payload + "]" ) ;
        
        $scope.interactingWithServer = true ;
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
                $scope.addErrorAlert( "Could not save click stream event." ) ;
            }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
    
    // ----------- Question response related scope functions -----------------
    $scope.saveAndNext = function() {
        console.log( "Saving answer and showing next question." ) ;
    }
    
    $scope.saveAndMarkForReview = function() {
        console.log( "Saving answer, marking for review and showing next question." ) ;
    }
    
    $scope.clearResponse = function() {
    }
    
    $scope.markForReviewAndNext = function() {
    }
    
    $scope.showQuestion = function( questionEx ) {
    }
    
    $scope.showNextQuestion = function() {
    }
    
    $scope.showPreviousQuestion = function() {
    }
    
    // ----------- Exam related scope functions -----------------------------
    
    $scope.submitAnswers = function() {
    }
    
    $scope.saveTestAttempt = function() {
        
        console.log( "Saving test attempt" ) ;
        
        $scope.interactingWithServer = true ;
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
                $scope.addErrorAlert( "Could not save test attempt." ) ;
            }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
    
    $scope.endTestAttempt = function() {
    }
    
    $scope.endCurrentLapAndStartNextLap = function() {
        endCurrentLap() ;
        startNextLap() ;
    }
    
    // ----------- UI related scope functions --------------------------------

    $scope.addErrorAlert = function( msgString ) {
        $scope.alerts.push( { type: 'danger', msg: msgString } ) ;
    } ;
    
    $scope.dismissAlert = function( index ) {
        $scope.alerts.splice( index, 1 ) ;
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
    
    $scope.getControlDashboardQuestionButtonStyle = function( questionEx ) {
        var style = questionEx.getStatusStyle() ;
        if( $scope.currentQuestion == questionEx ) {
            style += " q-control-border" ;
        }
        return style ;
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
        
        $scope.interactingWithServer = true ;
        $http.post( '/TestAttempt/LapSnapshot', snapshots )
        .then ( 
            function( response ){
                console.log( "Successfully saved test lap snapshot." ) ;
            }, 
            function( error ){
                console.log( "Error saving test lap snapshot on server." ) ;
                $scope.addErrorAlert( "Could not save test attempt lap snapshot." ) ;
            }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
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
    
    // ------------------- Initialization helpers ----------------------------
    function loadTestConfiguration( testId ) {
        
        console.log( "Loading test configuration - " + testId ) ;
        
        $scope.interactingWithServer = true ;
        $http.get( '/TestConfiguration/' + testId )
        .then( 
            function( response ){
                console.log( response.data ) ;
                
                $scope.testConfigIndex = response.data.testConfigIndex ;
                if( $scope.testConfigIndex.attempted == true ) {
                    console.log( "ERROR: This test has already been attempted." ) ;
                    $window.location.href = "/jeetest" ;
                }
                else {
                    preProcessQuestions( response.data ) ;
                    
                    $scope.$broadcast( 'computeSectionIndices' ) ;
                    startTime = (new Date()).getTime() ;
                    
                    $scope.secondsRemaining = $scope.testConfigIndex.duration * 60 ;
                    $scope.startTimer() ;
                    
                    $scope.testAttempt.testConfig = $scope.testConfigIndex ;
                    $scope.saveTestAttempt() ;
                }
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
        
        console.log( "Preprocessing questions." ) ;
        
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
        
        console.log( "Enhancing questions." ) ;
        
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
    
    // --- [END] Local functions
} ) ;