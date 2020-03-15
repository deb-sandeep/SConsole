sConsoleApp.controller( 'AdvTestConfigSummaryDashboardController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Advanced Test Config Summary Dashboard" ;
	$scope.testSummaries = [] ;
	
	// -----------------------------------------------------------------------
	// --- [START] Controller initialization ---------------------------------
	
	fetchTestConfigurations() ;
	
	// --- [END] Controller initialization -----------------------------------
	
	// -----------------------------------------------------------------------
	// --- [START] Scope functions -------------------------------------------
	
	$scope.deleteTest = function( test ) {
		deleteTestConfiguration( test.id ) ;
	}
	
	$scope.editTest = function( test ) {
		$location.path( "/editTest/" + test.id ) ;
	}
	
	$scope.newTest = function() {
		$location.path( "/editTest/-1" ) ;
	}
	
    $scope.syncTestToPiMon = function( test ) {
        console.log( "Synching test = " + test.id + " to pimon." ) ;
        syncTestToPiMon( test ) ;
    }
    
    $scope.cloneTest = function( testId ) {
        cloneTest( testId ) ;
    }
    
	// --- [END] Scope functions
	
	// -----------------------------------------------------------------------
	// --- [START] Local functions -------------------------------------------
	
	function deleteTestConfiguration( id ) {
		
    	console.log( "Deleting test configuration. ID = " + id ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.delete( "/TestConfiguration/" + id )
        .then( 
            function( response ){
                console.log( "Test Configuration deleted." ) ;
                fetchTestConfigurations() ;
            }, 
            function( error ){
                console.log( "Error deleting test configuration." ) ;
                console.log( error ) ;
                $scope.$parent.addErrorAlert( "Could not delete test config." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
	}
	
    function fetchTestConfigurations() {
    	
    	console.log( "Fetching test configuration summaries from server." ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.get( "/TestConfigurationIndex" )
        .then( 
            function( response ){
                console.log( "Test Configuration summaries received." ) ;
                console.log( response ) ;
                $scope.testSummaries = [] ;
                for( var i=0; i<response.data.length; i++ ) {
                	var testSummary = response.data[i] ;
                	if( testSummary.examType == "ADV" ) {
                		$scope.testSummaries.push( testSummary ) ;
                	}
                }
            }, 
            function( error ){
                console.log( "Error getting Test Configuration summaries." ) ;
                console.log( error ) ;
                $scope.$parent.addErrorAlert( "Could not test summaries." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    
    function syncTestToPiMon( test ) {
        
        console.log( "Synching test to PiMon." ) ;
        
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/SyncTestToPimon/' + test.id )
        .then( 
            function( response ){
                console.log( "Successfully synched test configuration." ) ;
                fetchTestConfigurations() ;
            }, 
            function( error ){
                console.log( "Error synching test on server." + error ) ;
                $scope.$parent.addErrorAlert( "Could not save test." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    function cloneTest( testId ) {
        
        console.log( "Cloning test id = " + testId ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.post( "/CloneTestConfiguration/" + testId )
        .then( 
            function( response ){
                console.log( response.data ) ;
                showTestCloneSuccessMsg( response.data ) ;
                fetchTestConfigurations() ;
            }, 
            function( error ){
                console.log( "Could not clone test." ) ;
                console.log( error ) ;
                $scope.$parent.addErrorAlert( "Could not clone test." ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
    function showTestCloneSuccessMsg( test ) {
        
        var msg = "Test cloned successfully.\nID = " + test.id  ;
        
        if( test.examType == 'ADV' ) {
            if( test.phySectionNames.includes( "LCT" ) || 
                test.chemSectionNames.includes( "LCT" ) || 
                test.mathSectionNames.includes( "LCT" ) ) {
                
                msg += "\n" ;
                msg += "Test contains LCT. Needs manual intervention." ;
            } 
        }
        
        alert( msg ) ;
    }
    
	// --- [END] Local functions
} ) ;