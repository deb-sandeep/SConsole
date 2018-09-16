sConsoleApp.controller( 'BurnController', function( $scope, $http ) {
    
    var TOPIC_MAP = {} ;
    
    $scope.displayTopics = [] ;
    $scope.displayBooks = [] ;
    $scope.displayProblems = [] ;
    $scope.message = null ;
    $scope.serverResponseStatus = null ;
    $scope.loading = false ;
    
    $scope.search = {
        book : {
            bookShortName : ""
        },
        chapterId : "",
        exerciseName : "",
        problemTag : "",
        active:true
    }
    
    $scope.userChoice = {
        subject : "IIT - Maths",
        topic : null,
        book : null
    } ;
    
    $scope.burnStats = {
        // Immutable attributes
        completionDate : null,
        numActive : 0,
        averageDailyTime: 0,
        averageProblemTime: 0,
        numProblemsPerDay: 0,
        numDaysTillBurnCompletion : 0,
        
        // Mutable/computed attributes
        burnCapacity: 0,
        burnOverflow: 0,
        burnExtraDays: 0,
        projectedBurnCompletionDate: null,
        numProjectedBurnCompletionDays: 0
    } ;
    
    {
        loadTopicsFromServer( $scope.userChoice.subject ) ;
    }
    
    $scope.subjectSelected = function() {
        $scope.displayTopics = TOPIC_MAP[$scope.userChoice.subject] ;
        $scope.userChoice.topic = $scope.displayTopics[0] ;
        loadProblemsFromServer( $scope.userChoice.topic ) ;
    }
    
    $scope.topicSelected = function() {
        console.log( "Topic selected = " ) ;
        console.log( $scope.userChoice.topic ) ;
        loadProblemsFromServer( $scope.userChoice.topic ) ;
    }
    
    $scope.bookSelected = function() {
        console.log( "Book selected = " + $scope.sessionDetails.book ) ;
    }
    
    $scope.checkDirty = function( problem ) {
        return problem.changedActivation != problem.active ? "dirty" : "" ;
    }
    
    $scope.activationChanged = function( problem ) {
        if( problem.changedActivation == true ) {
            $scope.burnStats.numActive++ ;
        }
        else {
            $scope.burnStats.numActive-- ;
        }
        computeProjectedBurnStats() ;
    }
    
    $scope.toggleSelection = function() {
        console.log( "Toggling selection." ) ;
        console.log( "Num filtered items = " + $scope.filtered.length ) ;
        
        var newActivation = false ;
        for( var i=0; i<$scope.filtered.length; i++ ) {
            var problem = $scope.filtered[i] ;
            if( i==0 ) {
                newActivation = problem.changedActivation? false : true ;
            }
            
            if( problem.changedActivation == true && 
                newActivation == false ) {
                $scope.burnStats.numActive-- ;
            }
            else if( problem.changedActivation == false && 
                      newActivation == true ) {
                $scope.burnStats.numActive++ ;
            }
            
            problem.changedActivation = newActivation ;
        }
        computeProjectedBurnStats() ;
    }
    
    $scope.updateServer = function() {
        console.log( "Updating server." ) ;
        
        var updatedProblems = [] ;
        
        for( var i=0; i<$scope.displayProblems.length; i++ ) {
            var problem = $scope.displayProblems[i] ;
            if( problem.changedActivation != problem.active ) {
                updatedProblems.push( {
                    id : problem.id,
                    active: problem.changedActivation
                }) ;
            }
        }

        if( updatedProblems.length > 0 ) {
            callUpdateServerAPI( updatedProblems ) ;
        }
    }
    
    function computeProjectedBurnStats() {
        
        $scope.burnStats.burnOverflow = $scope.burnStats.numActive -
                                        $scope.burnStats.burnCapacity ;
        
        $scope.burnStats.burnExtraDays = $scope.burnStats.burnOverflow / 
                                         $scope.burnStats.numProblemsPerDay ;
        
        $scope.burnStats.projectedBurnCompletionDate =
            moment( $scope.burnStats.completionDate ).add( $scope.burnStats.burnExtraDays, 'days' )
                                                     .toDate() ;
        
        var now = moment() ;
        var completionDt = moment( $scope.burnStats.projectedBurnCompletionDate ) ;
        var numDays = completionDt.diff( now, 'days' ) + 1 ;
        $scope.burnStats.numProjectedBurnCompletionDays = numDays ;
    }
    
    function loadTopicsFromServer( subject ) {
        
        console.log( "Loading topics from server." ) ;
        
        $scope.loading = true ;
        $http.get( '/Topic' )
        .then( 
                function( data ){
                    console.log( "Topics received." ) ;
                    console.log( data ) ;
                    TOPIC_MAP = data.data ;
                    $scope.displayTopics = TOPIC_MAP[subject] ;
                }, 
                function( error ){
                    console.log( "Error getting topic details." ) ;
                    console.log( error ) ;
                }
        )
        .finally(function() {
            $scope.loading = false ;
        }) ;
    }
    
    function loadBooksFromServer( topicId ) {
        
        console.log( "Loading books from server." ) ;
        
        $scope.loading = true ;
        $http( {
            url:'/Book',
            method:'GET',
            params: {
                topicId: topicId
            }
        })
        .then( 
                function( data ){
                    console.log( "Books received." ) ;
                    console.log( data ) ;
                    $scope.displayBooks = data.data ;
                }, 
                function( error ){
                    console.log( "Error getting book details." ) ;
                    console.log( error ) ;
                }
        )
        .finally(function() {
            $scope.loading = false ;
        }) ;
    }
    
    function loadProblemsFromServer( topic ) {

        console.log( "Loading probems from server." ) ;
        
        $scope.loading = true ;
        $scope.burnStats.numActive = 0 ;
        
        $http( {
            url:'/AllUnsolvedProblems',
            method:'GET',
            params: {
                topicId: topic.id
            }
        })
        .then( 
            function( data ){
                console.log( "Problems received." ) ;
                console.log( data ) ;
                $scope.displayProblems = data.data ;
                
                if( $scope.displayProblems.length > 0 ) {
                    $scope.burnStats.completionDate = 
                        $scope.displayProblems[0].topic.burnCompletion ;
                    
                    if( $scope.burnStats.completionDate == null ) {
                        $scope.burnStats.completionDate = moment( "2019-11-30" ).toDate() ;
                    }
                    
                    var now = moment() ;
                    var completionDt = moment( $scope.burnStats.completionDate ) ;
                    var numDays = completionDt.diff( now, 'days' ) + 1 ;

                    $scope.burnStats.numDaysTillBurnCompletion = numDays ;
                }
                
                for( var i=0; i<$scope.displayProblems.length; i++ ) {
                    var problem = $scope.displayProblems[i] ;
                    problem.changedActivation = problem.active ;
                    
                    if( problem.active ) {
                        $scope.burnStats.numActive++ ;
                    }
                }
                
                loadHistoricBurnStats( topic ) ;
            }, 
            function( error ){
                console.log( "Error getting book details." + error ) ;
            }
        )
        .finally(function() {
            $scope.loading = false ;
        }) ;
    }
    
    function loadHistoricBurnStats( topic ) {

        console.log( "Loading historic burn statistics from server." ) ;
        
        $scope.loading = true ;
        $scope.burnStats.averageDailyTime   =  0 ;
        $scope.burnStats.numProblemsPerDay  =  0 ;
        $scope.burnStats.averageProblemTime =  0 ;
        
        $http( {
            url:'/HistoricBurnStats',
            method:'GET',
            params: {
                topicId: topic.id
            }
        })
        .then( 
            function( data ){
                console.log( "Data received." ) ;
                console.log( data ) ;
                
                var totalDuration = 0 ;
                var totalNumProblems = 0 ;
                var stats = data.data ;
                var numDays = stats.length ;
                
                if( stats.length > 0 ) {
                    for( var i=0; i<numDays; i++ ) {
                        totalDuration += stats[i].duration ;
                        totalNumProblems += stats[i].numQuestionsSolved ;
                    }
                    
                    $scope.burnStats.averageDailyTime   = totalDuration/numDays ;
                    $scope.burnStats.numProblemsPerDay  = totalNumProblems/numDays ;
                    $scope.burnStats.averageProblemTime = totalDuration/totalNumProblems ;
                }
                else {
                    $scope.burnStats.averageDailyTime   = 45*60 ;
                    $scope.burnStats.numProblemsPerDay  = 15 ;
                    $scope.burnStats.averageProblemTime = 5*60 ;
                }
                
                $scope.burnStats.burnCapacity = $scope.burnStats.numDaysTillBurnCompletion *
                                                $scope.burnStats.numProblemsPerDay ;
                computeProjectedBurnStats() ;
            }, 
            function( error ){
                console.log( "Error getting book details." + error ) ;
            }
        )
        .finally(function() {
            $scope.loading = false ;
        }) ;
    }

    
    function callUpdateServerAPI( updatedProblems ) {

        console.log( "Updating server with user updates." ) ;
        
        $scope.loading = true ;
        $http.post( '/UpdateProblemActivations', {
            topicId : $scope.userChoice.topic.id,
            problemActivations : updatedProblems
        })
        .then( 
            function( data ){
                console.log( "Successfully posted button press." ) ;
                $scope.topicSelected() ;
            }, 
            function( error ){
                console.log( "Error posting button press." + error ) ;
                $scope.message = error ;
            }
        )
        .finally(function() {
            $scope.loading = false ;
        }) ;
    }
}) ;