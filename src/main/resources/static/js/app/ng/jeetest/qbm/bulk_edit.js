function BulkQEntry( entry ) {
	this.qRef     = entry.qRef ;
	this.imgName  = entry.imgName ;
	this.qType    = entry.qType ;
	this.aText    = entry.aText ;
	this.latLevel = entry.latLevel ;
	this.projTime = entry.projTime ;
	this.saved    = entry.saved ;
	this.imgPath  = entry.imgPath ;
}

sConsoleApp.controller( 'BulkEditController', 
		                function( $scope, $http, $sce, $routeParams, $window, $timeout ) {
	
	var editHelper = new EditHelper() ;
	
	$scope.$parent.navBarTitle = "Create Questions in Bulk" ;
	$scope.validationErrors = [] ;
	$scope.baseInput = {
		subjectName : "",
		topic : null,
		book : null,
		baseQRef : ""
	} ;
	
	$scope.entries = [] ;
	
	// --- [START] Controller initialization
	
	// First load the drop down master data from the server. The drop down
	// master data will consist of subjects, topics, books etc.
	$scope.$parent.loadQBMMasterData() ;
	
	// --- [END] Controller initialization
	
	// --- [START] Scope functions
	$scope.applyBaseCriteria = function() {
		$scope.validationErrors.length = 0 ;
		validateBaseCriteria() ;
		
		if( $scope.validationErrors.length > 0 ) {
			$( '#validationErrorsDialog' ).modal( 'show' ) ;
		}
		else {
			getBulkQuestionMetaData() ;
		}
	}
	
	$scope.closeValidationErrorsDialog = function() {
		$scope.validationErrors.length = 0 ;
		$( '#validationErrorsDialog' ).modal( 'hide' ) ;
	}
	
	$scope.saveEntry = function( entry ) {
		console.log( "Saving entry " + entry.qRef ) ;
		entry.saved = true ;
	}
	
	// --- [START] Internal functions
	function validateBaseCriteria() {
		
		if( $scope.baseInput.subjectName.trim() == "" ) {
			$scope.validationErrors.push( "Subject name should be specified." ) ;
		}
		
		if( $scope.baseInput.topic == null ) {
			$scope.validationErrors.push( "Topic should be specified." ) ;
		}
		
		if( $scope.baseInput.book == null ) {
			$scope.validationErrors.push( "Book should be specified." ) ;
		}
	}

    function getBulkQuestionMetaData() {
    	
        $scope.interactingWithServer = true ;
        $http.get( '/BulkQuestionsEntryMeta', {
        	params:{
        		subjectName : $scope.baseInput.subjectName,
        		topicId : $scope.baseInput.topic.id,
        		bookId : $scope.baseInput.book.id,
        		baseQRef : $scope.baseInput.baseQRef
        	}
        } )
        .then( 
                function( response ){
                    console.log( "Bulk question entry meta data received." ) ;
                    console.log( response ) ;
                    $scope.entries.length = 0 ;
                    for( var i=0; i<response.data.length; i++ ) {
                        $scope.entries.push( new BulkQEntry( response.data[i] ) ) ;
                    }
                }, 
                function( error ){
                    console.log( "Server error" ) ;
                    console.log( error ) ;
                    $scope.addErrorAlert( "Could not load bulk entry meta data." ) ;
                }
        )
        .finally(function() {
            $scope.interactingWithServer = false ;
        }) ;
    }
} ) ;