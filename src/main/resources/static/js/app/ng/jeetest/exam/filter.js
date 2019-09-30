// -----------------------------------------------------------------------------
// Filter - 'duration'
// Converts the given input into hh:mm:ss formatted string
// -----------------------------------------------------------------------------
sConsoleApp.filter( 'duration', function(){

	return function( secondsCount ) {
		
	    var hours   = Math.floor( secondsCount / 3600 ) ;
	    var minutes = Math.floor( ( secondsCount - (hours * 3600) ) / 60 ) ;
	    var seconds = secondsCount - ( hours * 3600 ) - ( minutes * 60 ) ;

	    if( hours   < 10 ){ hours   = "0" + hours   ; }
	    if( minutes < 10 ){ minutes = "0" + minutes ; }
	    if( seconds < 10 ){ seconds = "0" + seconds ; }

	    return hours + ':' + minutes + ':' + seconds ;
	} ;
}) ;

sConsoleApp.filter( 'mmss', function(){

	return function( secondsCount ) {
		
	    var minutes = Math.floor( secondsCount / 60 ) ;
	    var seconds = secondsCount - ( minutes * 60 ) ;

	    if( minutes < 10 ){ minutes = "0" + minutes ; }
	    if( seconds < 10 ){ seconds = "0" + seconds ; }

	    return minutes + ':' + seconds ;
	} ;
}) ;