function LapAutomata() {}

LapAutomata.prototype.mainStrategyA = {
    "L1" : {
        transitions : {
            "q-not-visited"               : "L2",
            "q-attempted"                 : "ANS",
            "q-ans-and-marked-for-review" : "AMR",
            "q-marked-for-review"         : "L2",
            "q-not-answered"              : "L3"
        }
    },
    "L2" : {
        transitions : {
            "q-not-visited"               : "L3",
            "q-attempted"                 : "ANS",
            "q-ans-and-marked-for-review" : "AMR",
            "q-marked-for-review"         : "Purple",
            "q-not-answered"              : "L3"
        }
    },
    "AMR" : {
        transitions : {
            "q-not-visited"               : "XXX",
            "q-attempted"                 : "ANS",
            "q-ans-and-marked-for-review" : "XXX",
            "q-marked-for-review"         : "Purple",
            "q-not-answered"              : "L3"
        }
    },
    "L3P" : {
        transitions : {
            "q-not-visited"               : "XXX",
            "q-attempted"                 : "ANS",
            "q-ans-and-marked-for-review" : "ANS",
            "q-marked-for-review"         : "Purple",
            "q-not-answered"              : "L3"
        }
    },
    "Purple" : {
        transitions : {
            "q-not-visited"               : "XXX",
            "q-attempted"                 : "ANS",
            "q-ans-and-marked-for-review" : "XXX",
            "q-marked-for-review"         : "L3",
            "q-not-answered"              : "L3"
        }
    },
    "L3" : {
        transitions : {
            "q-not-visited"               : "XXX",
            "q-attempted"                 : "ANS",
            "q-ans-and-marked-for-review" : "ANS",
            "q-marked-for-review"         : "ABA",
            "q-not-answered"              : "ABA"
        }
    }
} ;

function QuestionLapAttemptDetails( qaDetails, lapName ) {
    
    this.qaDetails = qaDetails ;
    this.lapName = lapName ;
    
    this.postLapState = null ;
    this.attemptStatus = null ;
    this.timeSpent = null ;
}

function QuestionAttemptDetails( question, attempt, lapNames, examType ) {
    
    this.question = question ;
    this.attempt  = attempt ;
    this.lapAttemptDetails = [] ;
    this.lapAttemptDetailMap = {} ;
    this.totalTimeSpent = 0 ;
    this.answeredInLap = "" ;
    
    this.initialize = function() {
        for( var i=0; i<lapNames.length; i++ ) {
            var lapName = lapNames[i] ;
            var qlad = new QuestionLapAttemptDetails( this, lapName ) ;
            this.lapAttemptDetails.push( qlad ) ;
            
            this.lapAttemptDetailMap[ lapName ] = qlad ;
        }
    }
    
    this.ingestLapSnapshot = function( snapshot ) {
        
        var lapAttemptDetail = this.lapAttemptDetailMap[ snapshot.lapName ] ;
        lapAttemptDetail.attemptStatus = snapshot.attemptStatus ;
        lapAttemptDetail.timeSpent = snapshot.timeSpent ;
        
        this.totalTimeSpent += snapshot.timeSpent ;
        
        var strategyAutomata = null ; 
        if( examType == "MAIN" ) {
            strategyAutomata = LapAutomata.prototype.mainStrategyA ;
        }
        
        if( strategyAutomata != null ) {
            var lapTransitions = strategyAutomata[ snapshot.lapName ].transitions ;
            lapAttemptDetail.postLapState = lapTransitions[ snapshot.attemptStatus ] ;
        }
        
        if( snapshot.attemptStatus == "q-attempted" ) {
            if( this.answeredInLap == "" ) {
                this.answeredInLap = snapshot.lapName ;
            }
        }
        else {
            if( this.answeredInLap != "" ) {
                this.answeredInLap = "" ;
            }
        }
    }
    
    this.initialize() ;
}

sConsoleApp.controller( 'TestAttemptLapDetailsController', function( $scope, $http, $location, $routeParams ) {
    
    // --------------- Local variables ---------------------------------------
    
    // ------------------ Master reference data -----------------------------
    $scope.searchMaster = {
        subjectNames : [ "IIT - Physics", "IIT - Chemistry", "IIT - Maths" ],
        resultTypes : [ "All", "Only Correct", "Only Wrong" ],
        attemptLaps : [ "L1", "L2", "AMR", "L3P", "Purple", "L3", "Abandoned" ],
        timeSpentChoices : [ "Any",
                      "< 1 min", "< 2 min", "< 3 min", "< 5 min", 
                      "> 1 min", "> 2 min", "> 3 min", "> 5 min" ],
        rcOptions : new RCOptions().choices 
    } ;
    
    // --------------- Scope variables ---------------------------------------
	$scope.$parent.navBarTitle = "Test Attempt Lap Details (Test ID = " + 
	                             $scope.$parent.selectedTestConfigId + ") " + 
	                             $routeParams.examType ;
	$scope.testAttemptId = $routeParams.id ;
	$scope.examType = $routeParams.examType ;
	
	$scope.lapNames = [] ;
	$scope.lapEvents        = null ;
    $scope.questions        = null ;
    $scope.questionAttempts = null ;
    $scope.lapSnapshots     = null ;
    
    $scope.qaDetails = [] ;
    
    $scope.searchCriteria = {
        selectedSubjects : [],
        resultType : $scope.searchMaster.resultTypes[0],
        attemptLaps : [],
        timeSpentChoice : $scope.searchMaster.timeSpentChoices[0],
        errorRCAChoices : []
    } ;
    
    $scope.selectedQuestion = null ;
    $scope.lapTimes    = {} ;
    $scope.lapAttempts = {} ;
    $scope.lapCorrects = {} ;
    $scope.lapAvgQTime = {} ;
    $scope.numAbandoned = 0 ;

    // -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
    fetchLapDetailsRawData( $scope.testAttemptId ) ;
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
    $scope.attemptDetailTimeHighlight = function( time ) {
        if( time <= 60 ) {
            return "lap-detail-cell-bg-a" ;
        }
        else if( time <= 120 ) {
            return "lap-detail-cell-bg-b" ;
        }
        else if( time <= 180 ) {
            return "lap-detail-cell-bg-c" ;
        }
        else if( time <= 300 ) {
            return "lap-detail-cell-bg-d" ;
        }
        else if( time > 300 ) {
            return "lap-detail-cell-bg-e" ;
        }
        return "" ;
    }
    
    $scope.getLapSubHeaderClass = function( index ) {
        return "lap-detail-cell-bg-" + $scope.lapNames[ Math.floor( index/2 ) ] ;
    }
    
    $scope.getLapSubHeader = function( index ) {
        return ( index % 2 == 0 ) ? "State" : "Time" ;
    }
    
    $scope.getLapDetail = function( qaDetail, index, blankForRepeat ) {
        
        var lapIndex = Math.floor( index/2 ) ;
        var lapName  = $scope.lapNames[ lapIndex ] ;
        var isState = ( index % 2 == 0 ) ;
        
        var lapAttemptDetail = qaDetail.lapAttemptDetailMap[ lapName ] ;
        
        if( isState ) {
            if( !blankForRepeat || index==0 ) {
                return lapAttemptDetail.postLapState ;
            }
            else {
                var prevIndex = index-2 ;
                var prevState = $scope.getLapDetail( qaDetail, prevIndex, false ) ;
                if( prevState == lapAttemptDetail.postLapState ) {
                    return "-" ;
                }
                else {
                    return lapAttemptDetail.postLapState ;
                }
            }
        }
        else {
            var secondsCount = lapAttemptDetail.timeSpent ;
            if( secondsCount < 5 ) {
                return "" ;
            }
            
            var minutes = Math.floor( secondsCount / 60 ) ;
            var seconds = secondsCount - ( minutes * 60 ) ;

            if( minutes < 10 ){ minutes = "0" + minutes ; }
            if( seconds < 10 ){ seconds = "0" + seconds ; }

            return minutes + ':' + seconds ;            
        }
        
        return index ;
    }
    
    $scope.getLapDetailCellClass = function( qaDetail, $index ) {
        
        var lapDetail = $scope.getLapDetail( qaDetail, $index, false ) ;
        var isState = ( $index % 2 == 0 ) ;
        
        if( isState ) {
            return "lap-detail-cell-bg-" + lapDetail ;
        }
        else {
            var state = $scope.getLapDetail( qaDetail, $index-1, false ) ;
            return "lap-detail-cell-fixed-font " +
                   "lap-detail-cell-bg-" + state ;
        }
        return "" ;
    }    
    
    $scope.determineVisibility = function( qaDetail ) {
        
        if( isFilteredBySelectedSubjects( qaDetail ) ) {
            return false ;
        }
        
        if( isFilteredByResultType( qaDetail ) ) {
            return false ;
        }
        
        if( isFilteredByAttemptLaps( qaDetail ) ) {
            return false ;
        }
        
        if( isFilteredByTimeSpent( qaDetail ) ) {
            return false ;
        }
        
        if( isFilteredByErrorRCChoices( qaDetail ) ) {
            return false ;
        }
        
        return true ;
    }
    
    $scope.showQuestion = function( question ) {
        $scope.selectedQuestion = question ;
        $( '#questionDisplayDialog' ).modal( 'show' ) ;
    }
    
    $scope.getTimeSpentForVisibleQuestions = function( lapName ) {
        var lapTime = 0 ;
        for( var j=0; j<$scope.qaDetails.length; j++ ) {
            var qaDetail = $scope.qaDetails[ j ] ;
            if( $scope.determineVisibility( qaDetail ) ) {
                var lapAttemptDetail = qaDetail.lapAttemptDetailMap[ lapName ] ;
                lapTime += lapAttemptDetail.timeSpent ;
            }
        }
        return lapTime ;
    }
    
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    function isFilteredBySelectedSubjects( qaDetail ) {
        
        var selectedSubjects = $scope.searchCriteria.selectedSubjects ;
        
        var isMatched = false ;
        
        if( selectedSubjects.length != 0 ) {
            for( var i=0; i<selectedSubjects.length; i++ ) {
                if( qaDetail.question.subject.name == selectedSubjects[i] ) {
                    isMatched = true ;
                    break ;
                }
            }
        }
        else {
            isMatched = true ;
        }
        return !isMatched ;
    }

    function isFilteredByResultType( qaDetail ) {
        
        var isFiltered = false ;
        var selectedType = $scope.searchCriteria.resultType ;
        
        if( selectedType != "All" ) {
            if( qaDetail.attempt.isCorrect ) {
                if( selectedType == "Only Wrong" ) {
                    isFiltered = true ;
                }
            } 
            else {
                if( selectedType == "Only Correct" ) {
                    isFiltered = true ;
                }
            }
        }
        return isFiltered ;
    }
        
    function isFilteredByAttemptLaps( qaDetail ) {
        
        var isMatched = false ;
        var selectedLaps = $scope.searchCriteria.attemptLaps ;
        
        if( selectedLaps.length != 0 ) {
            for( var i=0; i<selectedLaps.length; i++ ) {
                var selectedLap = selectedLaps[i] ;
                if( selectedLap == "Abandoned" ) {
                    if( qaDetail.answeredInLap == "" ) {
                        isMatched = true ;
                        break ;
                    }
                }
                else if( qaDetail.answeredInLap == selectedLaps[i] ) {
                    isMatched = true ;
                    break ;
                }
            }
        }
        else {
            isMatched = true ;
        }
        
        return !isMatched ;
    }
        
    function isFilteredByTimeSpent( qaDetail ) {
        
        var isMatched = true ;
        var choice = $scope.searchCriteria.timeSpentChoice ;
        
        var isLessThan = choice.startsWith( "<" ) ;
        var timeLimit = parseInt( choice.substring( 2, 3 ) )*60 ;
        var timeSpent = qaDetail.totalTimeSpent ;
        
        if( choice != "Any" ) {
            isMatched = false ;
            if( isLessThan ) {
                if( timeSpent < timeLimit ) {
                    isMatched = true ;
                }
            }
            else {
                if( timeSpent >= timeLimit ) {
                    isMatched = true ;
                }
            }
        }
        
        return !isMatched ;
    }
    
    function isFilteredByErrorRCChoices( qaDetail ) {
        
        var isMatched = true ;
        var rcaChoices = $scope.searchCriteria.errorRCAChoices ;
        
        if( rcaChoices.length > 0 ) {
            isMatched = false ;
            if( !qaDetail.attempt.isCorrect ) {
                for( var i=0; i<rcaChoices.length; i++ ) {
                    if( qaDetail.attempt.rootCause == rcaChoices[i].id ) {
                        isMatched = true ;
                        break ;
                    }
                }
            }
        }
        
        return !isMatched ;
    }
        
    function fetchLapDetailsRawData( testAttemptId ) {
        
        console.log( "Fetching lap details raw data for test attempt : " + testAttemptId ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.get( "/TestAttempt/LapDetails/" + testAttemptId )
        .then( 
            function( response ){
                console.log( response ) ;
                
                $scope.lapEvents        = response.data[0] ;
                $scope.questions        = response.data[1] ;
                $scope.questionAttempts = response.data[2] ;
                $scope.lapSnapshots     = response.data[3] ;
                
                processRawData() ;
            }, 
            function( error ){
                console.log( "Error getting lap details raw data." ) ;
                console.log( error ) ;
                $scope.$parent.addErrorAlert( "Error getting lap details raw data." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    function processRawData() {
        
        if( $scope.lapEvents.length == 0 ) return ;
        
        extractLapNames() ;
        constructQuestionAttemptDetails() ;
        processLapSnapshots() ;
        collectLapStatistics() ;
        
        console.log( "Raw data processed." ) ;
    }
    
    function extractLapNames() {
        
        for( var i=0; i<$scope.lapEvents.length; i++ ) {
            var event = $scope.lapEvents[i] ;
            if( event.eventId == "LAP_START" ) {
                $scope.lapNames.push( event.payload ) ;
            }
        }
    }
    
    function constructQuestionAttemptDetails() {
        
        for( var i=0; i<$scope.questions.length; i++ ) {
            var question = $scope.questions[i] ;
            var attempt  = $scope.questionAttempts[i] ;
            
            var attemptDetail = new QuestionAttemptDetails( 
                                                   question, attempt, 
                                                   $scope.lapNames, 
                                                   $scope.examType ) ;
            $scope.qaDetails.push( attemptDetail ) ;
        }
    }
    
    function processLapSnapshots() {
        
        for( var i=0; i<$scope.lapNames.length; i++ ) {
            var lapName = $scope.lapNames[i] ;
            for( var j=0; j<$scope.questions.length; j++ ) {
                var qaDetail = $scope.qaDetails[ j ] ;
                var lapSnapshotIndex = (i*$scope.questions.length) + j ;
                var lapSnapshot = $scope.lapSnapshots[ lapSnapshotIndex ] ;
                
                qaDetail.ingestLapSnapshot( lapSnapshot ) ;
            }
        }
    }
    
    function collectLapStatistics() {
        
        for( var i=0; i<$scope.lapNames.length; i++ ) {
            var lapName = $scope.lapNames[i] ;
            var lapTime = 0 ;
            var numAnswered = 0 ;
            var numCorrect = 0 ;
            
            for( var j=0; j<$scope.qaDetails.length; j++ ) {
                var qaDetail = $scope.qaDetails[ j ] ;
                var lapAttemptDetail = qaDetail.lapAttemptDetailMap[ lapName ] ;
                
                lapTime += lapAttemptDetail.timeSpent ;
                if( qaDetail.answeredInLap == lapName ) {
                    numAnswered++ ;
                    if( qaDetail.attempt.isCorrect ) {
                        numCorrect++ ;
                    }
                }
            }
            $scope.lapTimes[ lapName ] = lapTime ;
            $scope.lapAttempts[ lapName ] = numAnswered ;
            $scope.lapCorrects[ lapName ] = numCorrect ;
            
            var avgQTime = 0 ;
            if( numAnswered > 0 ) {
                avgQTime = Math.ceil( lapTime / numAnswered ) ;
            }
            $scope.lapAvgQTime[ lapName ] = avgQTime ;
        }
        
        for( var j=0; j<$scope.qaDetails.length; j++ ) {
            var qaDetail = $scope.qaDetails[ j ] ;
            if( qaDetail.answeredInLap == "" && 
                !qaDetail.attempt.isCorrect ) {
                $scope.numAbandoned++ ;
            }
        }
    }
    
    // --- [END] Local functions
} ) ;