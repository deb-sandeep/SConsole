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
    this.displayName = null ;
    this.questionType = null ;
    this.questions = [] ;
    this.stats = new SectionStats() ;
    this.nextSection = null ;

    this.initializeStats = function() {
        this.stats.numQuestions = this.questions.length ;
        this.stats.numNotVisited = this.questions.length ;
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
    $scope.attemptLapNames = [] ;
    $scope.overallSection = new Section() ;
    $scope.sections = [] ;
    
    // UI related variables. 
    $scope.alerts = [] ;
    $scope.navBarTitle = "JEE Advanced Practice Test" ;
    $scope.paletteHidden = false ;
    
    // Exam scope related variables.
    $scope.interactingWithServer = false ;
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
    
    // -----------------------------------------------------------------------
    // --- [START] Controller initialization ---------------------------------
    
    console.log( "Loading JEEAdvExamBaseController" ) ;
    
    // --- [END] Controller initialization -----------------------------------
    
    // -----------------------------------------------------------------------
    // --- [START] Scope functions -------------------------------------------
    
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