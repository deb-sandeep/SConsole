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
    
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------

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
    
    // --- [END] Local functions
} ) ;