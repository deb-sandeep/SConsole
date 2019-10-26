sConsoleApp.config( function( $routeProvider ) {
    $routeProvider
    .when("/", {
        templateUrl : "/jeetest/exam/advTestExam",
        controller : "JEEAdvTestController"
    })
    .when("/testResult", {
        templateUrl : "/jeetest/exam/advTestResult",
        controller : "JEEAdvTestResultController"
    })
});
