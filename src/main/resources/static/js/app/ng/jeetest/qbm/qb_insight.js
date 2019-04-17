sConsoleApp.controller( 'QBInsightController', function( $scope, $http, $location ) {
    
	$scope.$parent.navBarTitle = "Question Bank Insight." ;

	drawGraph() ;

	function drawGraph() {
	    new RGraph.HBar({
	        id: 'subject_bar',
	        data: [[8,6], [4,8],[4,9]],
	        options: {
	            grouping: 'stacked',
	            hmargin: 20,
	            yaxisLabels: ['IIT - Physics','IIT - Chemistry','IIT - Maths'],
	            key: ['Remaining','Attempted'],
	            keyPosition: 'margin',
	            keyColors: ['#7CB5EC','#434348'],
	            keyTextSize: 10,
	            colors: ['#7CB5EC','#434348'],
	            xaxis: false,
	            axesColor: '#999',
	            textSize: 12,
	            marginTop: 30,
	            marginBottom: 20,
	            marginLeft: 95,
	            marginRight: 25
	        }
	    }).draw();		
	}
} ) ;