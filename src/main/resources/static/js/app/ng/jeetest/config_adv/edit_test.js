sConsoleApp.controller( 'EditAdvTestController', function( $scope, $http, $routeParams, $location ) {
	
	$scope.$parent.navBarTitle = "Edit JEE Adv Test Configuration" ;
	$scope.testId = $routeParams.id ;

	$scope.phyTopics = [] ;
	$scope.chemTopics = [] ;
	$scope.mathTopics = [] ;
	
	$scope.sectionSelectionOptions = {
		sectionTypes : [ 'SCA', 'MCA', 'NT', 'LCT', 'MMT' ],
		section1Type : 'MCA',
		section2Type : 'NT',
		section3Type : 'LCT'
	} ;
	
	$scope.selectedSectionTypes = {
		section1Type : null,
		section2Type : null,
		section3Type : null
	} ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	loadQBInsights() ;
	if( $scope.testId == -1 ) {
		showSectionSelectionDialog() ;
	}
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	$scope.dismissSectionSelections = function() {
		$( '#sectionSelectionDialog' ).modal( 'hide' ) ;
		$location.path( "/" ) ;
	}
	
	$scope.applySectionSelections = function() {
		
		var sels = [ $scope.sectionSelectionOptions.section1Type,
					 $scope.sectionSelectionOptions.section2Type,
		             $scope.sectionSelectionOptions.section3Type ] ;
		
		var types = [ sels[0] ] ;
		for( var i=1; i<sels.length; i++ ) {
			if( types.indexOf( sels[i] ) != -1 ) {
				$scope.$parent.addErrorAlert( "Sections can't be of same type." ) ;
				return ;
			} 
			else {
				types.push( sels[i] ) ;
			}
		}

		$scope.selectedSectionTypes.section1Type = sels[0] ;
		$scope.selectedSectionTypes.section2Type = sels[1] ;
		$scope.selectedSectionTypes.section3Type = sels[2] ;
		
		$( '#sectionSelectionDialog' ).modal( 'hide' ) ;
	}
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
	function showSectionSelectionDialog() {
		$scope.sectionSelectionOptions.section1Type = 'MCA' ;
		$scope.sectionSelectionOptions.section2Type = 'NT' ;
		$scope.sectionSelectionOptions.section3Type = 'LCT' ;
		$( '#sectionSelectionDialog' ).modal( 'show' ) ;
	}
	
	function loadQBInsights() {
		
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/QBTopicInsights' )
        .then( 
                function( response ){
                    console.log( response ) ;
                    processRawInsightData( response.data ) ;
                }, 
                function( error ){
                    console.log( error ) ;
                    $scope.addErrorAlert( "Could not load QB insights." ) ;
                }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}
	
	function processRawInsightData( rawData ) {
		
		for( i=0; i<rawData.length; i++ ) {
			var insight = rawData[i] ;
			
			insight.selected = false ;
			
			if( insight.subjectName == 'IIT - Physics' ) {
				if( insight.totalQuestions > 0 ) {
					$scope.phyTopics.push( insight ) ;
				}
			}
			else if( insight.subjectName == 'IIT - Chemistry' ) {
				if( insight.totalQuestions > 0 ) {
					$scope.chemTopics.push( insight ) ;
				}
			}
			else if( insight.subjectName == 'IIT - Maths' ) {
				if( insight.totalQuestions > 0 ) {
					$scope.mathTopics.push( insight ) ;
				}
			}
			else {
				console.log( "Invalid subject found. " + insight.subjectName ) ; 
			}
		}
	}
	
	// --- [END] Local functions
} ) ;