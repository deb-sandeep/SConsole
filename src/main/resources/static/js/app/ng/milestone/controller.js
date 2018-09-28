sConsoleApp.controller( 'MilestoneController', function( $scope, $http ) {
    
    var TOPIC_MAP = {} ;
    
    $scope.streamNumber = "1" ;
    $scope.subjectName = "IIT - Maths" ;
    $scope.message = null ;
    $scope.loading = false ;
    $scope.topics = [] ;
    
    {
        loadTopicsFromServer() ;
    }
    
    $scope.subjectSelected = function() {
        $scope.topics = TOPIC_MAP[$scope.subjectName ] ;
        computeTopicsForDisplay() ;
    }
    
    $scope.streamSelected = function() {
        computeTopicsForDisplay() ;
    }
    
    $scope.checkDirty = function( topic ) {
        
        if( !topic.startDay.isSame( moment( topic.burnStart ) ) ) {
            return "dirty" ;
        }
        
        if( !topic.endDay.isSame( moment( topic.burnCompletion ) ) ) {
            return "dirty" ;
        }
        
        return "" ;
    }
    
    $scope.updateOnServer = function() {
        
        var changedTopics = [] ;
        
        for( var i=0; i<$scope.topics.length; i++ ) {
            var topic = $scope.topics[i] ;
            if( $scope.checkDirty( topic ) == "dirty" ) {
                changedTopics.push( {
                    topicId  : topic.id,
                    startDay : topic.startDay.toDate(),
                    endDay   : topic.endDay.toDate()
                }) ;
            }
        }
        
        console.log( "Posting changed topics to server." ) ;
        $http.post( '/ChangedTopics', { 
            changedTopics : changedTopics
        })
        .then( 
                TODO: Finish the post logic and then do the server side stuff
                      like posting events etc .
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
                    
                    cleanseTopicMap( "IIT - Maths" ) ;
                    cleanseTopicMap( "IIT - Physics" ) ;
                    cleanseTopicMap( "IIT - Chemistry" ) ;
                    
                    computeTopicsForDisplay() ;
                }, 
                function( error ){
                    console.log( "Error getting topic details." + error ) ;
                }
        ) ;
    }
    
    function cleanseTopicMap( subjectName ) {
        
        var validTopics = [] ;
        var topics = TOPIC_MAP[ subjectName ] ;
        
        for( var i=0; i<topics.length; i++ ) {
            var topic = topics[i] ;
            
            if( topic.streamNumber != 0 && 
                ( topic.burnStart != null && topic.burnCompletion != null ) ) {
                
                topic.startDay = moment( topic.burnStart ) ;
                topic.endDay = moment( topic.burnCompletion ) ;
                topic.freezeDuration = true ;
                topic.startShift = 0 ;
                topic.endShift = 0 ;
                
                topic.originalDuration = moment.duration( 
                            moment( topic.burnCompletion ).diff( moment( topic.burnStart ) )
                        ).as( 'd' ) ;
                
                
                refreshDuration( topic ) ;
                validTopics.push( topic ) ;
            }
        }
        
        TOPIC_MAP[ subjectName ] = validTopics ;
    }
    
    function refreshDuration( topic ) {
        topic.duration = moment.duration( topic.endDay.diff( topic.startDay ) )
                               .as( 'd' ) ;
    }
    
    function topicSort( a, b ) {
        
        var retVal = 0 ;
        if( a.startDay.isBefore( b.startDay ) ) {
            retVal = -1 ;
        }
        else if( a.startDay.isAfter( b.startDay ) ) {
            retVal = 1 ;
        }
        return retVal ;
    }
    
    function computeTopicsForDisplay() {
        
        var topics = TOPIC_MAP[ $scope.subjectName ] ;
        var validTopics = [] ;
        
        for( var i=0; i<topics.length; i++ ) {
            if( topics[i].streamNumber == $scope.streamNumber ) {
                validTopics.push( topics[i] ) ;
            }
        }
        
        $scope.topics = validTopics ;
        $scope.topics.sort( topicSort ) ;
        
        chainTopicsInScope() ;
        
        $scope.$$postDigest(function(){
            for( var i=0; i<$scope.topics.length; i++ ) {
                var topic = $scope.topics[i] ;
                initDatePicker( topic ) ;
            }
        });        
    }
    
    function chainTopicsInScope() {
        
        var lastTopic = null ;
        for( var i=0; i<$scope.topics.length; i++ ) {
            
            var topic = $scope.topics[i] ;
            topic.nextTopic = null ;
            
            if( lastTopic == null ) {
                lastTopic = topic ;
            }
            else {
                lastTopic.nextTopic = topic ;
                lastTopic = topic ;
            }
        }
    }
    
    function initDatePicker( topic ) {
        
        var dpStart = $( '#datetimepicker_start' + topic.id ) ;
        var dpEnd   = $( '#datetimepicker_end' + topic.id ) ;
        
        dpStart.datetimepicker({
            format: "MMM / DD / YYYY"
        }) ;
        dpEnd.datetimepicker({
            format: "MMM / DD / YYYY"
        }) ;
        
        dpStart.off( "dp.change" ) ;
        dpEnd.off( "dp.change" ) ;
        
        updateStartDatePicker( topic ) ;
        updateEndDatePicker( topic ) ;
        
        dpStart.on( "dp.change", function( e ){
            topic.startDay = e.date ;
            topic.startShift = moment.duration( 
                    moment( topic.startDay ).diff( moment( topic.burnStart ) )
            ).as( 'd' ) ;
            
            processTopicStartDateChange( topic, e.date ) ;
        }) ;
        dpEnd.on( "dp.change", function( e ){
            topic.endDay = e.date ;
            topic.endShift = moment.duration( 
                    moment( topic.endDay ).diff( moment( topic.burnCompletion ) )
            ).as( 'd' ) ;
            
            processTopicEndDateChange( topic, e.date ) ;
        }) ;
    }
    
    function updateStartDatePicker( topic ) {
        
        var dpStart = $( '#datetimepicker_start' + topic.id ) ;
        
        if( typeof dpStart.data( "DateTimePicker" ) != 'undefined' ) {
            dpStart.data( "DateTimePicker" ).date( topic.startDay ) ;
        }
    }
    
    function updateEndDatePicker( topic ) {
        
        var dpEnd = $( '#datetimepicker_end' + topic.id ) ;
        
        if( typeof dpEnd.data( "DateTimePicker" ) != 'undefined' ) {
            dpEnd.data( "DateTimePicker" ).date( topic.endDay ) ;
        }
    }
    
    function processTopicStartDateChange( topic, startDate ) {
        
        if( topic.freezeDuration ) {
            topic.endDay = topic.startDay.clone().add( topic.duration, 'd' ) ;
            updateEndDatePicker( topic ) ;
        }
        else {
            refreshDuration( topic ) ;
        }
        $scope.$apply() ;
    }
    
    function processTopicEndDateChange( topic, endDate ) {
        
        refreshDuration( topic ) ;
        $scope.$apply() ;
        
        var nextTopic = topic.nextTopic ;
        if( nextTopic != null ) {
            nextTopic.startDay = topic.endDay.clone().add( 1, 'd' ) ;
            updateStartDatePicker( nextTopic ) ;
        }
    }
}) ;