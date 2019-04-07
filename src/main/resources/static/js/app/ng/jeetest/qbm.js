sConsoleApp.config( function( $routeProvider ) {
	$routeProvider
	.when("/", {
		templateUrl : "/jeetest/editQuestion",
		controller : "EditQuestionController"
	})
	.when("/searchQuestions", {
		templateUrl : "/jeetest/searchQuestions",
		controller : "SearchQuestionController"
	})
	.when("/editQuestion/:id", {
		templateUrl : "/jeetest/editQuestion",
		controller : "EditQuestionController"
	})
});

sConsoleApp.controller( 'QBMController', function( $scope, $http ) {
	
	$scope.alerts = [] ;
    
	$scope.addErrorAlert = function( msgString ) {
	    $scope.alerts.push( { type: 'danger', msg: msgString } ) ;
	} ;
	
	$scope.dismissAlert = function( index ) {
		$scope.alerts.splice( index, 1 ) ;
	}
} ) ;