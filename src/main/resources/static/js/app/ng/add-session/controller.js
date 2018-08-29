sConsoleApp.controller( 'AddSessionController', function( $scope, $http ) {
    
    const SCR_CHOOSE_DATE              = "ChooseDate" ;
    const SCR_CHOOSE_SESSION_TYPE      = "ChooseSessionType" ;
    const SCR_CHOOSE_BOOK_AND_PROBLEMS = "ChooseBookAndProblems" ;
    const SCR_ADD_PROBLEM_OUTCOME      = "AddProblemOutcome" ;
    const SCR_ENTER_SESSION_TIME       = "EnterSessionTime" ;
    const SCR_REIVEW_AND_SUBMIT        = "ReviewAndSubmit" ;    
    
    var SCR_TITLE_MAP = new Map([ 
        [ SCR_CHOOSE_DATE             , "1) Enter date"               ],
        [ SCR_CHOOSE_SESSION_TYPE     , "2) Choose session type"      ],
        [ SCR_CHOOSE_BOOK_AND_PROBLEMS, "3) Choose book and problems" ],
        [ SCR_ADD_PROBLEM_OUTCOME     , "4) Add problem outcomes"     ],
        [ SCR_ENTER_SESSION_TIME      , "3) Enter time spent"         ],
        [ SCR_REIVEW_AND_SUBMIT       , "Last) Review and Submit"        ]
    ]) ;
    
    var NAV_MAP = new Map( [
      [ SCR_CHOOSE_DATE              , [ SCR_CHOOSE_SESSION_TYPE,      null                         ]],
      [ SCR_CHOOSE_SESSION_TYPE      , [ SCR_CHOOSE_BOOK_AND_PROBLEMS, SCR_CHOOSE_DATE              ]],
      [ SCR_CHOOSE_BOOK_AND_PROBLEMS , [ SCR_ADD_PROBLEM_OUTCOME,      SCR_CHOOSE_SESSION_TYPE      ]],
      [ SCR_ADD_PROBLEM_OUTCOME      , [ SCR_ENTER_SESSION_TIME,       SCR_CHOOSE_BOOK_AND_PROBLEMS ]],
      [ SCR_ENTER_SESSION_TIME       , [ SCR_REIVEW_AND_SUBMIT,        SCR_ADD_PROBLEM_OUTCOME      ]],
      [ SCR_REIVEW_AND_SUBMIT        , [ null,                         SCR_ENTER_SESSION_TIME       ]]
    ] ) ;

    $scope.currentScreen = SCR_CHOOSE_DATE ;
    $scope.title = SCR_TITLE_MAP.get(SCR_CHOOSE_DATE) ;
    
    $scope.moveToNextScreen = function( currentScreen ) {
        setCurrentScreen( NAV_MAP.get( currentScreen )[0] ) ;
        
        if( currentScreen == SCR_CHOOSE_DATE ) {
            dateChosen() ;
        }
    }
    
    $scope.moveToPrevScreen = function( currentScreen ) {
        setCurrentScreen( NAV_MAP.get( currentScreen )[1] ) ;
    }
    
    function setCurrentScreen( currentScreen ) {
        $scope.currentScreen = currentScreen ;
        if( currentScreen != null ) {
            $scope.title = SCR_TITLE_MAP.get( currentScreen ) ;
        }
    }
    
    function dateChosen() {
        var moment = $('#datetimepicker').data("DateTimePicker").viewDate() ;
        console.log( moment ) ;
    }
} ) ;