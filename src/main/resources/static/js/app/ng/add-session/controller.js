sConsoleApp.controller( 'AddSessionController', function( $scope, $http ) {
    
    const SCR_CHOOSE_DATE              = "ChooseDate" ;
    const SCR_CHOOSE_SESSION_TYPE      = "ChooseSessionType" ;
    const SCR_CHOOSE_BOOK_AND_PROBLEMS = "ChooseBookAndProblems" ;
    const SCR_ADD_PROBLEM_OUTCOME      = "AddProblemOutcome" ;
    const SCR_ENTER_SESSION_TIME       = "EnterSessionTime" ;
    const SCR_REIVEW_AND_SUBMIT        = "ReviewAndSubmit" ;
    const SCR_SERVER_RESPONSE          = "SessionCreationServerResponse" ;
    
    var SCR_TITLE_MAP = new Map([ 
        [ SCR_CHOOSE_DATE             , "Step-1) Enter date"               ],
        [ SCR_CHOOSE_SESSION_TYPE     , "Step-2) Choose session type"      ],
        [ SCR_CHOOSE_BOOK_AND_PROBLEMS, "Step-3) Choose book and problems" ],
        [ SCR_ADD_PROBLEM_OUTCOME     , "Step-4) Add problem outcomes"     ],
        [ SCR_ENTER_SESSION_TIME      , "Step-3) Enter time spent"         ],
        [ SCR_REIVEW_AND_SUBMIT       , "Last Step) Review and Submit"        ]
    ]) ;
    
    var NAV_MAP = new Map( [
      [ SCR_CHOOSE_DATE              , [ SCR_CHOOSE_SESSION_TYPE,      null                         ]],
      [ SCR_CHOOSE_SESSION_TYPE      , [ SCR_CHOOSE_BOOK_AND_PROBLEMS, SCR_CHOOSE_DATE              ]],
      [ SCR_CHOOSE_BOOK_AND_PROBLEMS , [ SCR_ADD_PROBLEM_OUTCOME,      SCR_CHOOSE_SESSION_TYPE      ]],
      [ SCR_ADD_PROBLEM_OUTCOME      , [ SCR_REIVEW_AND_SUBMIT,        SCR_CHOOSE_BOOK_AND_PROBLEMS ]],
      [ SCR_ENTER_SESSION_TIME       , [ SCR_REIVEW_AND_SUBMIT,        SCR_CHOOSE_SESSION_TYPE      ]],
      [ SCR_REIVEW_AND_SUBMIT        , [ null,                         null       ]]
    ] ) ;

    var TOPIC_MAP = {} ;
    
    $scope.currentScreen = SCR_CHOOSE_DATE ;
    $scope.title = SCR_TITLE_MAP.get(SCR_CHOOSE_DATE) ;
    $scope.displayTopics = [] ;
    $scope.displayBooks = [] ;
    $scope.displayProblems = [] ;
    $scope.message = null ;
    $scope.serverResponseStatus = null ;
    $scope.loading = false ;
    
    $scope.sessionDetails = {
        sessionType : "Exercise",
        subject : "IIT - Maths",
        topic : null,
        book : null,
        selectedProblems : [],
        duration : 0,
        startTime : null
    } ;
    
    {
        loadTopicsFromServer() ;
    }
    
    $scope.moveToNextScreen = function( currentScreen ) {
        
        $scope.message = null ;
        
        if( currentScreen == SCR_CHOOSE_DATE ) {
            var moment = $('#datetimepicker').data("DateTimePicker").viewDate() ;
            $scope.sessionDetails.startTime = moment.toDate() ;
        }
        else if( currentScreen == SCR_CHOOSE_SESSION_TYPE ) {
            if( $scope.sessionDetails.topic == null ) {
                $scope.message = "Plese select topic." ;
            }            
            else if( $scope.sessionDetails.sessionType != "Exercise" ) {
                setCurrentScreen( SCR_ENTER_SESSION_TIME ) ;
                return ;
            }
        }
        else if( currentScreen == SCR_CHOOSE_BOOK_AND_PROBLEMS ) {
            if( $scope.sessionDetails.book == null ) {
                $scope.message = "Plese select book." ;
            }            
            else if( $scope.sessionDetails.selectedProblems.length == 0 ) {
                $scope.message = "Plese select problems." ;
            }
        }
        else if( currentScreen == SCR_ADD_PROBLEM_OUTCOME ) {
            $scope.sessionDetails.duration = 0 ;
            for( var i=0; i<$scope.sessionDetails.selectedProblems.length; i++ ) {
                var problem = $scope.sessionDetails.selectedProblems[i] ;
                $scope.sessionDetails.duration += problem.duration ;
            }
        }
        else if( currentScreen == SCR_REIVEW_AND_SUBMIT ) {
            createNewSessionOnServer() ;
            setCurrentScreen( null ) ;
        }
        
        if( $scope.message == null ) {
            setCurrentScreen( NAV_MAP.get( currentScreen )[0] ) ;
        }
    }
    
    $scope.moveToPrevScreen = function( currentScreen ) {
        if( currentScreen == SCR_CHOOSE_DATE ) {
            setCurrentScreen( SCR_SERVER_RESPONSE ) ;
        }
        else if( currentScreen == SCR_REIVEW_AND_SUBMIT ) {
            if( $scope.sessionDetails.sessionType != "Exercise" ) {
                setCurrentScreen( SCR_ENTER_SESSION_TIME ) ;
            }
            else {
                setCurrentScreen( SCR_ADD_PROBLEM_OUTCOME ) ;
            }
        }
        else {
            setCurrentScreen( NAV_MAP.get( currentScreen )[1] ) ;
        }
    }
    
    $scope.subjectSelected = function() {
        $scope.displayTopics = TOPIC_MAP[$scope.sessionDetails.subject] ;
    }
    
    $scope.topicSelected = function() {
        console.log( "Topic selected = " + $scope.sessionDetails.topic ) ;
    }
    
    $scope.bookSelected = function() {
        console.log( "Book selected = " + $scope.sessionDetails.book ) ;
        if( $scope.sessionDetails.book != null ) {
            loadProblemsFromServer() ;
        }
    }
    
    $scope.getProblemOutcomeClass = function( problem ) {
        
        if( problem.outcome == 'Skip' )        return "outcome_skip" ;
        else if( problem.outcome == 'Solved' ) return "outcome_solved" ;
        else if( problem.outcome == 'Redo' )   return "outcome_redo" ;
        else if( problem.outcome == 'Pigeon' ) return "outcome_pigeon" ;
        
        return "" ;
    }
    
    $scope.createNewSession = function() {
        setCurrentScreen( SCR_CHOOSE_DATE ) ;
    }
    
    function setCurrentScreen( currentScreen ) {
        $scope.currentScreen = currentScreen ;
        
        if( currentScreen != null ) {
            $scope.title = SCR_TITLE_MAP.get( currentScreen ) ;
        }
        else {
            return ;
        }
        
        if( currentScreen == SCR_CHOOSE_SESSION_TYPE ) {
            $scope.displayTopics = TOPIC_MAP[$scope.sessionDetails.subject] ;
        }
        else if( currentScreen == SCR_CHOOSE_BOOK_AND_PROBLEMS ) {
            loadBooksFromServer() ;
        }
        else if( currentScreen == SCR_ADD_PROBLEM_OUTCOME ) {
            for( var i=0; i<$scope.displayProblems.length; i++ ) {
                var problem = $scope.displayProblems[i] ;
                problem.outcome = null ;
                problem.duration = -1 ;
            }
            
            for( var i=0; i<$scope.sessionDetails.selectedProblems.length; i++ ) {
                var problem = $scope.sessionDetails.selectedProblems[i] ;
                problem.outcome = "Solved" ;
                problem.duration = 2 ;
            }
        }
    }
    
    function createNewSessionOnServer() {
        
        var problemOutcome = [] ;
        var bookId = -1 ;
        
        if( $scope.sessionDetails.sessionType == 'Exercise' ) {
            for( var i=0; i<$scope.sessionDetails.selectedProblems.length; i++ ) {
                var problem = $scope.sessionDetails.selectedProblems[i] ;
                problemOutcome.push( {
                    problemId : problem.id,
                    outcome : problem.outcome,
                    starred : problem.starred,
                    duration : problem.duration
                }) ;
            }
            bookId = $scope.sessionDetails.book.id ;
        }
        else {
            bookId = -1 ;
        }
        
        console.log( "Posting new session to server." ) ;
        $http.post( '/Session', { 
            sessionType : $scope.sessionDetails.sessionType,
            subject     : $scope.sessionDetails.subject,
            topicId     : $scope.sessionDetails.topic.id,
            bookId      : bookId,
            duration    : $scope.sessionDetails.duration,
            startTime   : $scope.sessionDetails.startTime,
            outcome     : problemOutcome
        })
        .then( 
            function( data ){
                console.log( "Response from Create Session received.." ) ;
                console.log( data ) ;
                $scope.currentScreen = SCR_SERVER_RESPONSE ;
                if( data.status == 200 ) {
                    $scope.serverResponseStatus = "success" ;
                }
                else {
                    $scope.serverResponseStatus = "failure" ;
                }
            }, 
            function( error ){
                console.log( "Error creating session." + error ) ;
                $scope.currentScreen = SCR_SERVER_RESPONSE ;
                $scope.serverResponseStatus = "failure" ;
            }
        ) ;
    }
    
    function loadTopicsFromServer() {
        
        console.log( "Loading topics from server." ) ;
        
        $http.get( '/Topic' )
        .then( 
                function( data ){
                    console.log( "Topics received." ) ;
                    console.log( data ) ;
                    TOPIC_MAP = data.data ;
                    $scope.displayTopics = TOPIC_MAP[$scope.sessionDetails.subject] ;
                    $scope.sessionDetails.topic = $scope.displayTopics[0] ;
                }, 
                function( error ){
                    console.log( "Error getting topic details." + error ) ;
                }
        ) ;
    }
    
    function loadBooksFromServer() {
        
        console.log( "Loading books from server." ) ;
        $http( {
            url:'/Book',
            method:'GET',
            params: {
                topicId: $scope.sessionDetails.topic.id
            }
        })
        .then( 
                function( data ){
                    console.log( "Books received." ) ;
                    console.log( data ) ;
                    $scope.displayBooks = data.data ;
                    if( $scope.displayBooks.length > 0 ) {
                        $scope.sessionDetails.book = data.data[0] ;
                        loadProblemsFromServer() ;
                    }
                }, 
                function( error ){
                    console.log( "Error getting book details." + error ) ;
                }
        ) ;
    }
    
    function loadProblemsFromServer() {

        console.log( "Loading probems from server." ) ;
        $scope.loading = true ;
        $http( {
            url:'/Problems',
            method:'GET',
            params: {
                topicId: $scope.sessionDetails.topic.id,
                bookId: $scope.sessionDetails.book.id
            }
        })
        .then( 
            function( data ){
                console.log( "Problems received." ) ;
                console.log( data ) ;
                $scope.displayProblems = data.data ;
                $scope.loading = false ;
            }, 
            function( error ){
                console.log( "Error getting book details." + error ) ;
                $scope.loading = false ;
            }
        ) ;
    }
    
}) ;