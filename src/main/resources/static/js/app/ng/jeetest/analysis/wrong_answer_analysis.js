sConsoleApp.controller( 'WrongAnswersAnalysisController', function( $scope, $http, $location, $routeParams ) {
    
	$scope.$parent.navBarTitle = "Wrong Test Answer Analysis" ;
	
    // ------------------ Master reference data -----------------------------
	$scope.rcOptions = new RCOptions() ;
    $scope.searchMaster = {
        subjectNames  : [ "IIT - Physics", "IIT - Chemistry", "IIT - Maths" ],
        numQuestions  : [ "All", 
            "> 10", "> 20", "> 30", "> 40", "> 50", "> 60",
            "< 10", "< 20", "< 30", "< 40", "< 50", "< 60", 
        ],
        efficiencyMarkers : [ "Any",
            "> 95", "> 90", "> 85", "> 80", "> 75", "> 70", "> 60", "> 50", 
            "< 95", "< 90", "< 85", "< 80", "< 75", "< 70", "< 60", "< 50",  
        ],
        rcOptions     : $scope.rcOptions.choices,
        timeHorizon   : [ "1 weeks",
                          "2 weeks",
                          "3 weeks",
                          "4 weeks",
                          "5 weeks",
                          "6 weeks",
                          "9 weeks",
                          "12 weeks",
                          "15 weeks",
                          "18 weeks",
                          "21 weeks",
                          "24 weeks",
                          "208 weeks"
       ]
    } ;
    
    // ------------------ Scope variables ------------------------------------
    $scope.searchCriteria = {
        selectedSubjects : [],
        numQuestions     : [$scope.searchMaster.numQuestions[0]],
        efficiencyMarkers: [$scope.searchMaster.efficiencyMarkers[0]],
        errorRCAChoices  : [
            $scope.rcOptions.choices[0],
            $scope.rcOptions.choices[4],
            $scope.rcOptions.choices[5],
            $scope.rcOptions.choices[8],
            $scope.rcOptions.choices[9],
            $scope.rcOptions.choices[10]
        ],
        timeHorizon : $scope.searchMaster.timeHorizon[1]
    } ;
    
    $scope.topicDetails = [] ;
    $scope.selectedQuestions = [] ;
    $scope.selectedQuestionsRC = "" ;
    $scope.effGraphImgPath = "" ;
        
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
    
    fetchErrorDetailsRawData( 2 ) ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
    
    $scope.determineVisibility = function( topicDetail ) {
        
        if( isFilteredBySelectedSubjects( topicDetail ) ) {
            return false ;
        }
        if( isFilteredByNumQuestions( topicDetail ) ) {
            return false ;
        }
        if( isFilteredByEfficiencyRange( topicDetail ) ) {
            return false ;
        }
        return true ;
    }
    
    $scope.getNumErrorsForRC = function( topicDetail, rc ) {
        if( topicDetail.rcClusters.hasOwnProperty( rc ) ) {
            return topicDetail.rcClusters[ rc ].length ;
        }
        return "" ;
    }
    
    $scope.getNumErrorsForChosenRCs = function( topicDetail ) {
        
        var numErrors = 0 ;
        for( var i=0; i<$scope.searchCriteria.errorRCAChoices.length; i++ ) {
            var rc = $scope.searchCriteria.errorRCAChoices[i].id ;
            if( topicDetail.rcClusters.hasOwnProperty( rc ) ) {
                numErrors += topicDetail.rcClusters[ rc ].length ;
            }
        }
        
        if( topicDetail.rcClusters.hasOwnProperty( "-UNASSIGNED-" ) ) {
            numErrors += topicDetail.rcClusters[ "-UNASSIGNED-" ].length ;
        }
        return numErrors > 0 ? numErrors : "" ;
    }
    
    $scope.getTopicEffectiveness = function( topicDetail ) {
        
        var numErrors = $scope.getNumErrorsForChosenRCs( topicDetail ) ;
        var eff = (( topicDetail.numQuestions - numErrors )/topicDetail.numQuestions)*100 ;
        eff = Math.ceil( eff ) ;
        return eff ;
    }
    
    $scope.getEffectivenessClass = function( topicDetail ) {
        
        var eff = $scope.getTopicEffectiveness( topicDetail ) ;
        if( eff < 70 ) return 'eff-bad' ;
        else if( eff < 80 ) return 'eff-vpoor' ;
        else if( eff < 90 ) return 'eff-poor' ;
        else if( eff < 95 ) return 'eff-ok' ;
        return 'eff-good' ;
    }
    
    $scope.showQuestions = function( topicDetail, rc ) {
        
        if( topicDetail.rcClusters.hasOwnProperty( rc ) ) {
            var questions = topicDetail.rcClusters[ rc ] ;
            console.log( "Displaying questions - " + questions ) ;
            $scope.selectedQuestionsRC = rc ;
            displayQuestions( questions ) ;
        }
    }
    
    $scope.showEffGraph = function( topicDetail ) {
        $scope.effGraphImgPath = "/jeetest/effgraphs/" + 
                                 topicDetail.subjectName + "/" + 
                                 topicDetail.topicName + "/graph.png" ;
        
        $( '#effGraphDialog' ).modal( 'show' ) ;
    }
    
    $scope.refreshDataForTimeHorizon = function() {
        
        var timeHorizon = $scope.searchCriteria.timeHorizon ;
        var numWeeks = parseInt( timeHorizon.substring( 0, timeHorizon.length - 6 ) ) ;
        fetchErrorDetailsRawData( numWeeks ) ;
    }
	
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
    
    function displayQuestions( questions ) {
        
        console.log( "Fetching specific questions" ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.get( "/TestQuestion/Ids?ids=" + questions.join( ',' ) )
        .then( 
            function( response ){
                console.log( response ) ;
                $scope.selectedQuestions = response.data ;
                $( '#questionsDisplayDialog' ).modal( 'show' ) ;
            }, 
            function( error ){
                console.log( "Error getting specific questions." ) ;
                console.log( error ) ;
                $scope.$parent.addErrorAlert( "Error getting specific questions." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    function isFilteredBySelectedSubjects( topicDetail ) {
        
        var selectedSubjects = $scope.searchCriteria.selectedSubjects ;
        
        var isMatched = false ;
        if( selectedSubjects.length != 0 ) {
            for( var i=0; i<selectedSubjects.length; i++ ) {
                if( topicDetail.subjectName == selectedSubjects[i] ) {
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

    function isFilteredByNumQuestions( topicDetail ) {
        
        var isMatched = true ;
        var choices = $scope.searchCriteria.numQuestions ; 
        
        for( var i=0; i<choices.length; i++ ) {
            var choice = choices[i] ; 
            var isLessThan = choice.startsWith( "<" ) ;
            var numQuestions = parseInt( choice.substring( 2 ) ) ;
            
            if( choice != "All" ) {
                var match = false ;
                if( isLessThan ) {
                    if( topicDetail.numQuestions < numQuestions ) {
                        match = true ;
                    }
                }
                else {
                    if( topicDetail.numQuestions >= numQuestions ) {
                        match = true ;
                    }
                }
                isMatched = isMatched & match ;
                if( !isMatched ) {
                    break ;
                }
            }
            else {
                isMatched = true ;
                break ;
            }
        }
        return !isMatched ;
    }
    
    function isFilteredByEfficiencyRange( topicDetail ) {
        
        var isMatched = true ;
        var choices = $scope.searchCriteria.efficiencyMarkers ; 
        
        for( var i=0; i<choices.length; i++ ) {
            var choice = choices[i] ; 
            var isLessThan = choice.startsWith( "<" ) ;
            var efficiencyMarker = parseInt( choice.substring( 2 ) ) ;
            var topicEfficiency = $scope.getTopicEffectiveness( topicDetail ) ;
            
            if( choice != "Any" ) {
                var match = false ;
                if( isLessThan ) {
                    if( topicEfficiency < efficiencyMarker ) {
                        match = true ;
                    }
                }
                else {
                    if( topicEfficiency >= efficiencyMarker ) {
                        match = true ;
                    }
                }
                isMatched = isMatched & match ;
                if( !isMatched ) {
                    break ;
                }
            }
            else {
                isMatched = true ;
                break ;
            }
        }
        return !isMatched ;
    }
    
    function fetchErrorDetailsRawData( numWeeks ) {
        
        console.log( "Fetching error details raw data" ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.get( "/TestAttempt/TopicWiseTestQuestionErrorDetails?timeHorizon=" + numWeeks )
        .then( 
            function( response ){
                console.log( response ) ;
                processRawData( response.data ) ;
            }, 
            function( error ){
                console.log( "Error getting error details raw data." ) ;
                console.log( error ) ;
                $scope.$parent.addErrorAlert( "Error getting error details raw data." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    function processRawData( data ) {
        $scope.topicDetails = data ;
    }
    
    // --- [END] Local functions
} ) ;