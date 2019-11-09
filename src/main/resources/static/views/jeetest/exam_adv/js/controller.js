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
    
    this.id = null ;
    this.sectionNumber = 0 ;
    this.displayName = null ;
    this.questionType = null ;
    this.questions = [] ;
    this.stats = new SectionStats() ;
    this.nextSection = null ;
    this.maxMarks = 0 ;

    this.initializeStats = function() {
        this.stats.numQuestions = this.questions.length ;
        this.stats.numNotVisited = this.questions.length ;
    }

    this.computeSectionMaxMarks = function() {
        if( this.questionType == 'SCA' ) {
            this.maxMarks = this.questions.length * 3 ;
        }
        else if( this.questionType == 'MCA' ) {
            this.maxMarks = this.questions.length * 4 ;
        }
        else if( this.questionType == 'NT' ) {
            this.maxMarks = this.questions.length * 3 ;
        }
        else if( this.questionType == 'LCT' ) {
            this.maxMarks = this.questions.length * 3 ;
        }
        else if( this.questionType == 'MMT' ) {
            this.maxMarks = this.questions.length * 3 ;
        }
    }
}

sConsoleApp.controller( 'JEEAdvExamBaseController', function( $scope, $http, $rootScope, $location, $window ) {
    
    // ---------------- Local variables --------------------------------------
    
    // ---------------- Scope variables --------------------------------------

    // Initialization context - these are populated during initialization
    // and are considered read only going forward. Note that only the structure
    // remains read only, attribute values inside these data structures can
    // still get modified
    $scope.testConfigIndex = null ;
    $scope.overallSection = new Section() ;
    $scope.sections = [] ;
    
    // UI related variables. 
    $scope.alerts = [] ;
    $scope.navBarTitle = "JEE Advanced Practice Test" ;
    
    // Exam scope related variables.
    $scope.interactingWithServer = false ;
    $scope.answersSubmitted = false ;
    $scope.examStartTime = 0 ;
    
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
    
    // -----------------------------------------------------------------------
    // --- [START] Controller initialization ---------------------------------
    
    console.log( "Loading JEEAdvExamBaseController" ) ;
    
    // --- [END] Controller initialization -----------------------------------
    
    // -----------------------------------------------------------------------
    // --- [START] Scope functions -------------------------------------------
    
    // ------------------- Server comm functions -----------------------------
    $scope.saveClickStreamEvent = function( eventId, payload ) {
        
        var timeMarker = (new Date()).getTime() - $scope.examStartTime ;
        console.log( "ClickStreamEvent[ " + 
                        "eventId = " + eventId + "," + 
                        "timeMarker = " + timeMarker + ", " + 
                        "payload = " + payload + "," + 
                        "testAttemptId = " + $scope.testAttempt.id + "]" ) ;
        
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
    
    $scope.saveTestAttempt = function( callbackFn ) {
        
        console.log( "Saving test attempt." ) ;
        
        $scope.interactingWithServer = true ;
        $http.post( '/TestAttempt', $scope.testAttempt )
        .then ( 
            function( response ){
                $scope.testAttempt = response.data ;
                callbackFn() ;
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
    
    // ----------- UI related scope functions --------------------------------

    $scope.addErrorAlert = function( msgString ) {
        $scope.alerts.push( { type: 'danger', msg: msgString } ) ;
    } ;
    
    $scope.dismissAlert = function( index ) {
        $scope.alerts.splice( index, 1 ) ;
    }
    
    // --- [END] Scope functions

    // -----------------------------------------------------------------------
    // --- [START] Local functions -------------------------------------------
    
    // --- [END] Local functions
} ) ;