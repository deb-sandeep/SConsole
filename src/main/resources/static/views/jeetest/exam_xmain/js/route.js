sConsoleApp.config( function( $routeProvider ) {
    $routeProvider
    .when("/", {
        templateUrl : "/jeetest/exam/xMainTestExam",
        controller : "JEEXMainTestController"
    })
    .when("/testResult", {
        templateUrl : "/jeetest/exam/xMainTestResult",
        controller : "JEEXMainTestResultController"
    })
});
