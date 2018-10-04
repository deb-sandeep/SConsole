sConsoleApp.controller( 'MilestoneController', function( $scope, $http ) {
    
    var TOPIC_MAP = {} ;
    var numNewTopicsAdded = 0 ;
    
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
        
        if( topic.active != topic.newActive ) {
            return "dirty" ;
        }
        
        if( topic.newName != topic.topicName ) {
        		return "dirty" ;
        }
        
        if( topic.newStreamNumber != topic.streamNumber ) {
        		return "dirty" ;
        }
        
        return "" ;
    }
    
    $scope.updateOnServer = function() {
        
        var changedTopics = [] ;
        var topics = TOPIC_MAP[ $scope.subjectName ] ;
        
        for( var i=0; i<topics.length; i++ ) {
            var topic = topics[i] ;
            if( $scope.checkDirty( topic ) == "dirty" ) {
                changedTopics.push( {
                    topicId      : topic.id,
                    startDay     : topic.startDay.toDate(),
                    endDay       : topic.endDay.toDate(),
                    active       : topic.newActive,
                    name         : topic.newName,
                    streamNumber : topic.newStreamNumber 
                }) ;
            }
        }
        
        console.log( "Posting changed topics to server." ) ;
        $scope.loading = true ;
        $http.post( '/ChangedTopics', { 
            changedTopics : changedTopics
        })
        .then( 
            function( data ){
                loadTopicsFromServer() ;
            }, 
            function( error ){
                console.log( "Error updating topic milestones." + error ) ;
                $scope.message = "Server failure. " + error ;
            }
        ) 
        .finally(function() {
            $scope.loading = false ;
        }) ;
    }
    
    $scope.addNewTopic = function( index ) {
    	
    		numNewTopicsAdded++ ;
    		
        var original = $scope.topics[index] ;
        var topic = {} ;
    		
        topic.id = -numNewTopicsAdded ;
        topic.active = false ;
        topic.burnStart = null ;
        topic.burnCompletion = null ;
        topic.streamNumber = original.streamNumber ;
        topic.startDay = moment( original.endDay ) ;
        topic.endDay = moment( original.endDay ) ;
        topic.subject = original.subject ;
        topic.duration = 0 ;
        topic.originalDuration = 0 ;
        topic.freezeDuration = true ;
        topic.startShift = 0 ;
        topic.endShift = 0 ;
        topic.newActive = false ;
        topic.overlap = false ;
        topic.topicName = null ;
        topic.newName = "<<Edit Topic Name>>" ;
        topic.editTopic = true ;
        topic.nextTopic = null ;
        topic.newStreamNumber = original.streamNumber ;

        var topics = TOPIC_MAP[ $scope.subjectName ] ;
        topics.push( topic ) ;
        
        computeTopicsForDisplay() ;
    }
    
    $scope.deleteNewTopic = function( index ) {
    	
    		var topic = $scope.topics[index] ;
    		var topics = TOPIC_MAP[ $scope.subjectName ] ;
    		var filteredTopics = topics.filter( function( value, index, arr ){
    			return value.id != topic.id ;
    		}) ;
    		
    		TOPIC_MAP[ $scope.subjectName ] = filteredTopics ;
    		
    		computeTopicsForDisplay() ;
    }
    
    $scope.changeTopicStream = function( topic ) {
    	
    		topic.newStreamNumber = topic.newStreamNumber == '1' ? '2' : '1' ;
    		computeTopicsForDisplay() ;
    }
    
    $scope.moveTopicUp = function( index ) {

    		var srcTopic = $scope.topics[ index ] ;
		var destTopic = $scope.topics[ index-1 ] ;
		
		srcTopic.startDay = destTopic.startDay ;
		srcTopic.endDay = srcTopic.startDay.clone().add( srcTopic.duration, 'd' ) ;
		
		destTopic.startDay = srcTopic.endDay.clone().add( 1, 'd' ) ;
		destTopic.endDay = destTopic.startDay.clone().add( destTopic.duration, 'd' ) ;
		
		computeTopicsForDisplay() ;
    }
    
    $scope.moveTopicDown = function( index ) {
    	
		var srcTopic = $scope.topics[ index ] ;
		var destTopic = $scope.topics[ index+1 ] ;
		
		destTopic.startDay = srcTopic.startDay ;
		destTopic.endDay = destTopic.startDay.clone().add( destTopic.duration, 'd' ) ;
		
		srcTopic.startDay = destTopic.endDay.clone().add( 1, 'd' ) ;
		srcTopic.endDay = srcTopic.startDay.clone().add( srcTopic.duration, 'd' ) ;
		
		computeTopicsForDisplay() ;
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
                topic.newActive = topic.active ;
                topic.overlap = false ;
                topic.newName = topic.topicName ;
                topic.editTopic = false ;
                topic.newStreamNumber = topic.streamNumber ;
                
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
        else if( a.id < b.id ) {
        		retVal = -1 ;
        }
        else if( a.id > b.id ) {
        		retVal = 1 ;
        }
        return retVal ;
    }
    
    function computeTopicsForDisplay() {
        
        var topics = TOPIC_MAP[ $scope.subjectName ] ;
        var validTopics = [] ;
        
        for( var i=0; i<topics.length; i++ ) {
            if( topics[i].newStreamNumber == $scope.streamNumber ) {
                validTopics.push( topics[i] ) ;
            }
        }
        
        $scope.topics = validTopics ;
        $scope.topics.sort( topicSort ) ;
        
        computeAndFlagTopicOverlap() ;
        chainTopicsInScope() ;
        
        $scope.$$postDigest(function(){
            for( var i=0; i<$scope.topics.length; i++ ) {
                var topic = $scope.topics[i] ;
                console.log( "Init date picker for topic = " + topic.id ) ;
                initDatePicker( topic ) ;
            }
        });        
    }
    
    // Identify the topics which are overlapping with others. An overlap
    // occurs when the start date or end end day of a session lies 
    // in between the start and end days of any other session.
    function computeAndFlagTopicOverlap() {
    		
    		for( var i=0; i<$scope.topics.length; i++ ) {
    			$scope.topics[i].overlap = false ;
    		}
    		
    		for( var i=0; i<$scope.topics.length; i++ ) {
    			
    			var topicToCheck = $scope.topics[i] ;
    			if( topicToCheck.overlap == true ) continue ;
    			
    			for( var j=0; j<$scope.topics.length; j++ ) {
    				
    				var topicToCheckAgainst = $scope.topics[j] ;
    				if( i == j ) continue ;
    				if( topicToCheckAgainst.overlap == true ) continue ;
    				
    				if( topicToCheck.startDay.isSame( topicToCheckAgainst.startDay ) || 
     				topicToCheck.startDay.isSame( topicToCheckAgainst.endDay ) ) {
    					
    					topicToCheck.overlap = true ;
    					topicToCheckAgainst.overlap = true ;
    				}
    				else if( topicToCheck.startDay.isAfter( topicToCheckAgainst.startDay ) && 
    				         topicToCheck.startDay.isBefore( topicToCheckAgainst.endDay ) ) {
    					
    					topicToCheck.overlap = true ;
    					topicToCheckAgainst.overlap = true ;
    				}
    				else if( topicToCheck.endDay.isAfter( topicToCheckAgainst.startDay ) && 
   				         topicToCheck.endDay.isBefore( topicToCheckAgainst.endDay ) ) {
   					
   					topicToCheck.overlap = true ;
   					topicToCheckAgainst.overlap = true ;
   				}
    			}
    		}
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
        
        computeAndFlagTopicOverlap() ;
        $scope.$apply() ;
    }
    
    function processTopicEndDateChange( topic, endDate ) {
        
        refreshDuration( topic ) ;
        
        var nextTopic = topic.nextTopic ;
        if( nextTopic != null ) {
            nextTopic.startDay = topic.endDay.clone().add( 1, 'd' ) ;
            updateStartDatePicker( nextTopic ) ;
        }
        
        computeAndFlagTopicOverlap() ;
        $scope.$apply() ;
    }
}) ;