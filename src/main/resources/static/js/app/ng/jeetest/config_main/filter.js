sConsoleApp.filter( "qSelFmt", function() {
    return function( question ) {
    	var fmt = question.questionType.padEnd( 4, '_' ) ;
    	fmt += question.questionRef.padEnd( 30, '.' ) ;
    	fmt += " | " ;
    	fmt += ( question.lateralThinkingLevel + "" ).padStart( 3 ) ;
    	fmt += " | " ;
    	fmt += ( question.projectedSolveTime + "" ).padStart( 4 ) ;
        return fmt ;
    }
}) ;