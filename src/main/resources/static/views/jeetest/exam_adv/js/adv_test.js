sConsoleApp.controller( 'JEEAdvTestController', function( $scope, $http, $rootScope, $location, $window ) {
    
    // ---------------- local variables --------------------------------------
    // These are the strategy based attempt laps
    // L1  - Level 1
    // L2  - Level 2
    // AMR - Answers marked for review
    // L3P - Level 3 prioritization
    // L2  - Level 3
    var currentLapIndex = -1 ;
    var startTime = 0 ;
    
    // ---------------- Scope variables --------------------------------------

    $scope.attemptLapNames = [ "L1", "L2", "AMR", "L3P", "Purple", "L3" ] ;
    $scope.secondsRemaining = 0 ;
    $scope.timeSpent = 0 ;
    $scope.timerActive = false ;
    
    $scope.currentQuestion = null ;
    $scope.currentLapName = null ;
    $scope.nextLapName = null ;
    
    // -----------------------------------------------------------------------
    // --- [START] Controller initialization ---------------------------------
    initializeController() ;
    
    // --- [END] Controller initialization -----------------------------------
    
    // -----------------------------------------------------------------------
    // --- [START] Event listeners -------------------------------------------
    // --- [END] Event listeners ---------------------------------------------
    
    // --- [START] Scope functions -------------------------------------------
    
    // ----------- Question response related scope functions -----------------
    
    // ----------- Exam related scope functions -----------------------------
    
    // ----------- UI related scope functions --------------------------------
    
    // --- [END] Scope functions
    
    // -----------------------------------------------------------------------
    // --- [START] Local functions -------------------------------------------
    // ------------------- Initialization helpers ----------------------------
    function initializeController() {
        
        console.log( "Initializing JEEAdvExamBaseController" ) ;
        
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
                $scope.$parent.attemptLapDetails[ lapNames[i] ] = {
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
                    
                    /*
                    startTime = (new Date()).getTime() ;
                    
                    $scope.secondsRemaining = $scope.testConfigIndex.duration * 60 ;
                    startTimer() ;
                    
                    $scope.$parent.testAttempt.testConfig = $scope.testConfigIndex ;
                    saveTestAttempt() ;
                    */
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
        $scope.$parent.overallSection.initializeStats() ;
    }
    
    function initializeSections( secSubjectName, secQTypes, 
                                 secQIndices, secQuestions ) {
        
        console.log( "Initializing sections for subject - " + secSubjectName ) ;
        
        var section = null ;
        
        for( var i=0; i<secQTypes.length; i++ ) {
            var secQType = secQTypes[i] ;
            
            section = new Section() ;
            section.displayName = secSubjectName + " SEC " + (i+1) ;
            section.questionType = secQType ;

            console.log( "  Section Q type = " + secQType ) ;
            console.log( "  Display name = " + section.displayName ) ;
            
            for( var j=0; j<secQIndices[i].length; j++ ) {
                var qIndex = secQIndices[i][j] ;
                var question = secQuestions[qIndex] ;
                console.log( "    Q index = " + qIndex + ", id = " + question.id ) ;

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
        else {
            qEx.interactionHandler = new NTInteractionHandler( qEx, $rootScope ) ;
        }
    }
    
    // --- [END] Local functions
} ) ;