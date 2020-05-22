sConsoleApp.controller( 'JEEAdvTestController', function( $scope, $http, $rootScope, $location, $window ) {
    
    // ---------------- local variables --------------------------------------
    // These are the strategy based attempt laps
    // L1  - Level 1
    // L2  - Level 2
    // AMR - Answers marked for review
    // L3P - Level 3 prioritization
    // L2  - Level 3
    var SEC_INFO_DIV_ID = "#sec-info-div" ;
    var currentLapIndex = -1 ;
    var secSpanIdVsSectionMap = {} ;
    
    // ---------------- Scope variables --------------------------------------

    $scope.attemptLapNames = [ "L1", "L2P", "L2", "AMR", "L3P", "Purple", "L3" ] ;
    $scope.secondsRemaining = 0 ;
    $scope.timeSpent = 0 ;
    $scope.timeSpentInCurrentLap = 0 ;
    $scope.timerActive = false ;
    $scope.paletteHidden = false ;
    $scope.thresholdAlertClass = "" ;
    
    $scope.currentHoverSection = null ;
    $scope.currentSection = null ;
    $scope.currentQuestion = null ;
    $scope.currentLapName = null ;
    $scope.nextLapName = null ;
    $scope.attemptLapDetails = {} ;
    
    // -----------------------------------------------------------------------
    // --- [START] Controller initialization ---------------------------------
    initializeController() ;
    
    // --- [END] Controller initialization -----------------------------------
    
    // -----------------------------------------------------------------------
    // --- [START] Event listeners -------------------------------------------
    $scope.$on( 'refreshAttemptSummary', function( event, payload ){
        refreshStats( payload ) ;
    }) ;
    
    // --- [END] Event listeners ---------------------------------------------
    
    // --- [START] Scope functions -------------------------------------------
    
    // ----------- Question response related scope functions -----------------
    $scope.saveAndNext = function() {
        console.log( "Saving answer and showing next question." ) ;
        if( $scope.currentQuestion.interactionHandler.isAnswered() ) {
            $scope.currentQuestion.attemptState = AttemptState.prototype.ATTEMPTED ;
            
            $scope.$parent.saveClickStreamEvent( 
                    ClickStreamEvent.prototype.ANSWER_SAVE, 
                    "" + $scope.currentQuestion.question.id ) ;
            
            refreshStats( $scope.currentQuestion ) ;
            $scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
        }
        else {
            alert( "Please provide a valid answer before saving." ) ;
        }
    }
    
    $scope.clearResponse = function() {
        console.log( "Clearing response of current question." ) ;
        $scope.currentQuestion.interactionHandler.clearResponse() ;
        $scope.$parent.saveClickStreamEvent( 
                ClickStreamEvent.prototype.ANSWER_CLEAR_RESPONSE, 
                "" + $scope.currentQuestion.question.id ) ;
    }
    
    $scope.markForReviewAndNext = function() {
        if( $scope.currentQuestion.interactionHandler.isAnswered() ) {
            
            $scope.currentQuestion.attemptState = AttemptState.prototype
                                                              .ANS_AND_MARKED_FOR_REVIEW ;
            $scope.$parent.saveClickStreamEvent( 
                    ClickStreamEvent.prototype.ANSWER_SAVE_AND_MARK_REVIEW, 
                    "" + $scope.currentQuestion.question.id ) ;
        }
        else {
            
            $scope.currentQuestion.attemptState = AttemptState.prototype
                                                              .MARKED_FOR_REVIEW ;
            $scope.$parent.saveClickStreamEvent( 
                    ClickStreamEvent.prototype.ANSWER_MARK_FOR_REVIEW, 
                    "" + $scope.currentQuestion.question.id ) ;
        }
        refreshStats( $scope.currentQuestion ) ;
        $scope.showQuestion( $scope.currentQuestion.nextQuestion ) ;
    }
    
    // ----------- Exam related scope functions -----------------------------
    $scope.selectSection = function( section ) {
        $scope.currentSection = section ;
        $scope.showQuestion( section.questions[0] ) ;
    }
    
    $scope.showQuestion = function( questionEx ) {
        
        if( questionEx != null ) {
            if( $scope.currentSection != null && 
                questionEx.section != $scope.currentSection ) {
                $scope.selectSection( questionEx.section ) ;
            }
            else {
                $scope.currentQuestion = questionEx ;
                if( $scope.currentQuestion.attemptState == AttemptState.prototype.NOT_VISITED ) {
                    $scope.currentQuestion.attemptState = AttemptState.prototype.NOT_ANSWERED ;
                }
                $scope.$parent.saveClickStreamEvent( 
                        ClickStreamEvent.prototype.QUESTION_VISITED, 
                        "" + questionEx.question.id ) ;
                refreshStats( questionEx ) ;
            }
        }
    }
    
    $scope.endCurrentLapAndStartNextLap = function() {
        endCurrentLap() ;
        startNextLap() ;
    }
    
    $scope.submitAnswers = function() {
        
        endCurrentLap() ;
        
        $scope.timerActive = false ;
        $scope.$parent.answersSubmitted = true ;
        
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
    
    // ----------- UI related scope functions --------------------------------
    $scope.getControlDashboardQuestionButtonStyle = function( questionEx ) {
        var style = questionEx.getStatusStyle() ;
        if( $scope.currentQuestion == questionEx ) {
            style += " q-control-border" ;
        }
        return style ;
    }
    
    $scope.getSectionTabClass = function( section ) {
        if( $scope.currentSection == section ) {
            return "selected-section" ;
        }
        return "" ;
    }
    
    $scope.initializeSectionHovers = function() {
        setTimeout( function(){
            var allSections = [ $scope.$parent.overallSection ] ;
            allSections = allSections.concat( $scope.$parent.sections ) ;
            
            $( SEC_INFO_DIV_ID ).hide() ;
            
            for( var i=0; i<allSections.length; i++ ) {
                initializeHoverForSection( allSections[i] ) ;
            }
        }, 100 ) ;
    }
    
    $scope.showQuestionPaper = function() {
        $scope.$parent.saveClickStreamEvent( 
                ClickStreamEvent.prototype.QUESTION_PAPER_VIEW_START, 
                null ) ;
    }
    
    $scope.hideQuestionPaper = function() {
        $scope.$parent.saveClickStreamEvent( 
                ClickStreamEvent.prototype.QUESTION_PAPER_VIEW_END, 
                null ) ;
    }
    
    $scope.toggleQuestionPalette = function() {
        
        var palette = document.getElementById( "adv-question-palette-panel" ) ;
        var display = document.getElementById( "adv-question-display-panel" ) ;
        
        if( $scope.paletteHidden ) {
            palette.style.display = "block" ;
            palette.style.width = "20%" ;
            display.style.width = "80%" ;
            $scope.paletteHidden = false ;
            $scope.$parent.saveClickStreamEvent( 
                    ClickStreamEvent.prototype.CONTROL_PANEL_EXPANDED, 
                    null ) ;
        }
        else {
            palette.style.display = "none" ;
            palette.style.width = "0%" ;
            display.style.width = "100%" ;
            $scope.paletteHidden = true ;
            $scope.$parent.saveClickStreamEvent( 
                    ClickStreamEvent.prototype.CONTROL_PANEL_COLLAPSED, 
                    null ) ;
        }
    }
    
    // --- [END] Scope functions
    
    // -----------------------------------------------------------------------
    // --- [START] Local functions -------------------------------------------
    
    // ------------------- Local utility functions ---------------------------
    function startTimer() {
        $scope.timerActive = true ;
        setTimeout( handleTimerEvent, 1000 ) ;
    }
    
    function handleTimerEvent() {
        if( $scope.currentLapName == null ) {
            setTimeout( handleTimerEvent, 1000 ) ;
        }
        
        $scope.secondsRemaining-- ;
        $scope.timeSpent++ ;

        $scope.attemptLapDetails[ $scope.currentLapName ].timeSpent++ ;
        if( $scope.currentQuestion != null ) {
            $scope.currentQuestion.timeSpent++ ;
            $scope.currentQuestion.lapDetails[ $scope.currentLapName ].timeSpent++ ;
            
            $scope.thresholdAlertClass = ( $scope.currentQuestion.timeSpent > 300 ) ?
                                         "threshold-alert" : "" ;
        }
        
        $scope.timeSpentInCurrentLap = $scope.attemptLapDetails[ $scope.currentLapName ].timeSpent ;
        
        if( $scope.secondsRemaining > 0 && $scope.timerActive ) {
            setTimeout( handleTimerEvent, 1000 ) ;
        }
        else {
            $scope.timerActive = false ;
            $scope.secondsRemaining = 0 ;
            if( $scope != null && 
                $scope.$parent != null && 
                !$scope.$parent.answersSubmitted ) {
                $scope.submitAnswers() ;
            }        
        }
        $scope.$digest() ;
    }
    
    function refreshStats( questionEx ) {
        refreshSectionStats( questionEx.section ) ;
        refreshSectionStats( $scope.$parent.overallSection ) ;
    }
    
    function refreshSectionStats( section )  {
        
        section.stats.numNotVisited = 0 ;
        section.stats.numNotAnswered = 0 ;
        section.stats.numAttempted = 0 ;
        section.stats.numMarkedForReview = 0 ;
        section.stats.numAnsAndMarkedForReview = 0 ;

        for( var i=0; i<section.questions.length; i++ ) {
            
            var q = section.questions[i] ;
            if( q.attemptState == AttemptState.prototype.NOT_VISITED ) {
                section.stats.numNotVisited++ ;
            }
            else if( q.attemptState == AttemptState.prototype.NOT_ANSWERED ) {
                section.stats.numNotAnswered++ ;
            }
            else if( q.attemptState == AttemptState.prototype.ATTEMPTED ) {
                section.stats.numAttempted++ ;
            }
            else if( q.attemptState == AttemptState.prototype.MARKED_FOR_REVIEW ) {
                section.stats.numMarkedForReview++ ;
            }
            else if( q.attemptState == AttemptState.prototype.ANS_AND_MARKED_FOR_REVIEW ) {
                section.stats.numAnsAndMarkedForReview++ ;
            }
        }
    }
    
    // ------------------- Lap marker functios ------------------------------
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
            $scope.$parent.saveClickStreamEvent( 
                                  ClickStreamEvent.prototype.LAP_START, 
                                  $scope.currentLapName ) ;
        }
    }
    
    function endCurrentLap() {
        
        var attemptDetail = $scope.attemptLapDetails[ $scope.currentLapName ] ;
        attemptDetail.endTime = new Date() ;
        
        $scope.$parent.saveClickStreamEvent( 
                             ClickStreamEvent.prototype.LAP_END, 
                             $scope.currentLapName ) ;

        var snapshots = [] ;
        var allQuestions = $scope.$parent.overallSection.questions ;
        for( var i=0; i<allQuestions.length; i++ ) {
            var q = allQuestions[i] ;
            var lapDetails = q.lapDetails[ $scope.currentLapName ] ;
            
            lapDetails.attemptState = q.attemptState ;
            
            snapshots.push({
                testAttemptId  : $scope.$parent.testAttempt.id ,
                questionId     : q.question.id ,
                lapName        : $scope.currentLapName,
                timeSpent      : lapDetails.timeSpent, 
                attemptStatus  : q.attemptState
            }) ;
        }
        saveTestAttemptLapSnapshots( snapshots, 1 ) ;
    }
    
    // ------------------- Server comm functions -----------------------------
    function saveTestAttemptLapSnapshots( snapshots, attemptNumber ) {
        
        console.log( "Saving test attempt lap snapshots." ) ;
        
        $scope.interactingWithServer = true ;
        $http.post( '/TestAttempt/LapSnapshot', snapshots )
        .then ( 
            function( response ){
                console.log( "Successfully saved test lap snapshot." ) ;
            }, 
            function( error ){
                if( attemptNumber < 4 ) {
                    // Attempt again to save the snapshots after some time
                    // with exponential backoff
                    console.log( "Lapshot save attempt " + attemptNumber + 
                                " failed. Trying again in " + 
                                ( 5 * attemptNumber ) + " seconds." ) ;
                    setTimeout( saveTestAttemptLapSnapshots, 5000*attemptNumber, 
                                snapshots, attemptNumber+1 ) ;
                }
                else {
                    // If we have not been able to save the snapshots after
                    // three attempts, log it onto the console for manual
                    // intervention
                    console.log( "Error saving test lap snapshot on server." ) ;
                    
                    var strMsg = "Could not save test attempt lap snapshot." + 
                                 "Please save the data shown on this alert for manual import.<copy>" +
                                 getLapSnapshotLog( snapshots ) + "</copy>";
                    
                    $scope.addErrorAlert( strMsg ) ;
                }
            }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
    
    function getLapSnapshotLog( snapshots ) {
        
        var csvStr = "" ;
        for( var i=0; i<snapshots.length; i++ ) {
            var snapshot = snapshots[i] ;
            csvStr += snapshot.testAttemptId + "," + 
                      snapshot.questionId + "," + 
                      snapshot.lapName + "," + 
                      snapshot.timeSpent + "," + 
                      snapshot.attemptStatus + ";" ;
        }
        console.log( csvStr ) ;
        return csvStr ;
    }
    
    // ------------------- Initialization helpers ----------------------------
    function initializeController() {
        
        console.log( "Initializing JEEAdvExamController" ) ;
        
        var parameters = new URLSearchParams( window.location.search ) ;
        var testId = parameters.get( 'id' ) ;
        
        if( testId == null || $scope.$parent.answersSubmitted == true ) {
            $window.location.href = "/jeetest" ;
        }
        else {
            // Remove any scroll bars from the viewport - this is a 
            // full screen single page application.
            var elements = document.getElementsByTagName( "body" ) ;
            elements[0].style.overflowY = "hidden" ;
            
            loadTestConfiguration( testId ) ;

            var lapNames = $scope.attemptLapNames ;
            for( var i=0; i<lapNames.length; i++ ) {
                $scope.attemptLapDetails[ lapNames[i] ] = {
                    startTime : null,
                    endTime : null,
                    timeSpent : 0
                }
            }
        }
    }
    
    function loadTestConfiguration( testId ) {
        
        console.log( "Loading test configuration - " + testId ) ;
        
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/TestConfiguration/' + testId )
        .then( 
            function( response ){
                console.log( response.data ) ;
                
                $scope.$parent.testConfigIndex = response.data.testConfigIndex ;
                
                if( $scope.$parent.testConfigIndex.attempted == true ) {
                    console.log( "ERROR: This test has already been attempted." ) ;
                    $window.location.href = "/jeetest" ;
                }
                else {
                    initializeExamDataStructures( response.data ) ;
                    
                    $scope.$parent.examStartTime = (new Date()).getTime() ;
                    
                    $scope.secondsRemaining = $scope.testConfigIndex.duration * 60 ;
                    startTimer() ;
                    
                    $scope.$parent.testAttempt.id = testId ;
                    $scope.$parent.testAttempt.testConfig = $scope.$parent.testConfigIndex ;
                    
                    $scope.$parent.saveTestAttempt( function() {
                        $scope.saveClickStreamEvent( 
                                ClickStreamEvent.prototype.TEST_STARTED, null ) ;
                        
                        
                        $scope.selectSection( $scope.$parent.sections[0] ) ;
                        startNextLap() ;
                    }) ;
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
    
    function initializeExamDataStructures( data ) {
        
        console.log( "Initializing exam data strutures." ) ;
        
        initializeSections( "PHY",
                            data.phySectionNames,
                            data.phySecQuestionIndices,
                            data.phyQuestions ) ;
        
        initializeSections( "CHEM",
                            data.chemSectionNames,
                            data.chemSecQuestionIndices,
                            data.chemQuestions ) ;
        
        initializeSections( "MATH",
                            data.mathSectionNames,
                            data.mathSecQuestionIndices,
                            data.mathQuestions ) ;

        $scope.$parent.overallSection.displayName = "Sections" ;
        $scope.$parent.overallSection.id = "overall-sec" ;
        $scope.$parent.overallSection.initializeStats() ;
    }
    
    function initializeSections( secSubjectName, secQTypes, 
                                 secQIndices, secQuestions ) {
        
        var section = null ;
        
        for( var i=0; i<secQTypes.length; i++ ) {
            var secQType = secQTypes[i] ;
            
            section = new Section() ;
            section.id = secSubjectName.toLowerCase() + "-sec-" + (i+1) ; 
            section.sectionNumber = (i+1) ;
            section.displayName = secSubjectName + " SEC " + (i+1) ;
            section.questionType = secQType ;

            for( var j=0; j<secQIndices[i].length; j++ ) {
                var qIndex = secQIndices[i][j] ;
                var question = secQuestions[qIndex] ;
                question.targetExam = "ADV" ;
                
                var questionEx = createEnhancedQuestion( question ) ;
                questionEx.index = (j+1) ;
                questionEx.section = section ;

                $scope.$parent.overallSection.questions.push( questionEx ) ;
                section.questions.push( questionEx ) ;
            }

            var lastSection = getLastSection() ;
            if( lastSection != null ) {
                lastSection.nextSection = section ;
            }

            section.computeSectionMaxMarks() ;
            section.initializeStats() ;
            $scope.$parent.sections.push( section ) ;
        }
    }
    
    function getLastSection() {

        var allSections = $scope.$parent.sections ;
        if( allSections.length > 0 ) {
            return allSections[ allSections.length-1 ] ;
        }
        return null ;
    }

    function getLastQuestionEx() {

        var allQuestions = $scope.$parent.overallSection.questions ;
        if( allQuestions.length > 0 ) {
            return allQuestions[ allQuestions.length-1 ] ;
        }
        return null ;
    }

    function createEnhancedQuestion( question ) {

        var questionEx = new QuestionEx( question, $scope.attemptLapNames ) ;
        var lastQuestionEx = getLastQuestionEx() ;
        
        if( lastQuestionEx != null ) {
            lastQuestionEx.nextQuestion = questionEx ;
            questionEx.prevQuestion = lastQuestionEx ;
        }
        
        questionEx.timeSpent = 0 ;
        associateInteractionHandler( questionEx ) ;

        return questionEx ;
    }
    
    function associateInteractionHandler( qEx ) {
        
        if( qEx.question.questionType == "SCA" ) {
            qEx.interactionHandler = new SCAInteractionHandler( qEx, $rootScope ) ;
        }
        else if( qEx.question.questionType == "MCA" ) {
            qEx.interactionHandler = new MCAInteractionHandler( qEx, $rootScope ) ;
        }
        else if( qEx.question.questionType == "NT" ) {
            qEx.interactionHandler = new NTInteractionHandler( qEx, $rootScope ) ;
        }
        else {
            qEx.interactionHandler = new SCAInteractionHandler( qEx, $rootScope ) ;
        }
    }
    
    function initializeHoverForSection( section ) {
        
        var secBtnId = "#" + section.id + "-btn" ;
        var secInfoSpanId = "#" + section.id + "-info-span" ;
        
        secSpanIdVsSectionMap[ secInfoSpanId ] = section ;
        
        $( secInfoSpanId ).hover( function( event ) {
            
            var sec = secSpanIdVsSectionMap[ "#" + event.currentTarget.id ] ;
            $scope.currentHoverSection = sec ;
            $scope.$digest() ;
            
            var leftPos  = $( secBtnId )[0].getBoundingClientRect().left   + $(window)['scrollLeft']();
            var bottomPos= $( secBtnId )[0].getBoundingClientRect().bottom + $(window)['scrollTop']();
            
            $( SEC_INFO_DIV_ID ).css( {
                top: bottomPos + 2, 
                left: leftPos 
            }).show() ;
        }, 
        function() {
            $scope.currentHoverSection = null ;
            $( SEC_INFO_DIV_ID ).hide() ;
        });
    }
    
    // --- [END] Local functions
} ) ;