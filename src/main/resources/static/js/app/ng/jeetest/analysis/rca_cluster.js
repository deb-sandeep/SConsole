function RCACluster() {
	
	this.numQuestions         = 0 ;
	this.totalMarks           = 0 ;
	
	this.numCorrect           = 0 ;
	this.correctMks           = 0 ;
	
	this.numWrong             = 0 ;
	this.wrongMks             = 0 ;
	
    this.numRecollection      = 0 ;
    this.recollectionMks      = 0 ;

    this.numCalculation       = 0 ;
    this.calculationMks       = 0 ;

    this.numInterpretation    = 0 ;
    this.interpretationMks    = 0 ;

    this.numConcept           = 0 ;
    this.conceptMks           = 0 ;

    this.numLateral           = 0 ;
    this.lateralMks           = 0 ;

    this.numUnknownConcept    = 0 ;
    this.unknownConceptMks    = 0 ;

    this.numWtf               = 0 ;
    this.wtfMks               = 0 ;
    
    this.numLengthy           = 0 ;
    this.lengthyMks           = 0 ;

    this.numAvoidableTotal    = 0 ;
    this.avoidableTotalMks    = 0 ;
    
    this.numUnavoidableTotal  = 0 ;
    this.unavoidableTotalMks  = 0 ;
    
    this.numStupid            = 0 ;
    this.stupidMks            = 0 ;
    
    this.numJudgementError    = 0 ;
    this.judgementErrorMks    = 0 ;
    
    this.numUnwarrantedRisk   = 0 ;
    this.unwarrantedRiskMks   = 0 ;
    
    this.numWtfLateral        = 0 ;
    this.wtfLateralMks        = 0 ;
    
    
    var rcOptions = new RCOptions() ;
    
    this.reset = function() {
        this.numQuestions         = 0 ;
        this.totalMarks           = 0 ;
        this.numCorrect           = 0 ;
        this.correctMks           = 0 ;
        this.numWrong             = 0 ;
        this.wrongMks             = 0 ;
        this.numRecollection      = 0 ;
        this.recollectionMks      = 0 ;
        this.numCalculation       = 0 ;
        this.calculationMks       = 0 ;
        this.numInterpretation    = 0 ;
        this.interpretationMks    = 0 ;
        this.numConcept           = 0 ;
        this.conceptMks           = 0 ;
        this.numLateral           = 0 ;
        this.lateralMks           = 0 ;
        this.numUnknownConcept    = 0 ;
        this.unknownConceptMks    = 0 ;
        this.numWtf               = 0 ;
        this.wtfMks               = 0 ;
        this.numLengthy           = 0 ;
        this.lengthyMks           = 0 ;
        this.numAvoidableTotal    = 0 ;
        this.avoidableTotalMks    = 0 ;
        this.numUnavoidableTotal  = 0 ;
        this.unavoidableTotalMks  = 0 ;
        this.numStupid            = 0 ;
        this.stupidMks            = 0 ;
        this.numJudgementError    = 0 ;
        this.judgementErrorMks    = 0 ;
        this.numUnwarrantedRisk   = 0 ;
        this.unwarrantedRiskMks   = 0 ;
        this.numWtfLateral        = 0 ;
        this.wtfLateralMks        = 0 ;
    }
    
    this.updateStats = function( question, attempt ) {
    	
    	var marks = getMarksForQuestion( question ) ;
    	var marksGained = ( attempt.score > 0 ) ? attempt.score : 0 ;
    	var marksLost   = ( marks > attempt.score ) ? ( marks - attempt.score ) : 0 ;
    	
    	this.numQuestions++ ;
    	this.totalMarks += marks ;
    	this.correctMks += marksGained ;
    	this.wrongMks   += marksLost ;
    	
    	if( attempt.isCorrect ) {
    		this.numCorrect++ ;
    	}
    	else {
    		this.numWrong++ ;
    		this.updateRCStat( attempt.rootCause, marksLost ) ;
    	}
    }
    
    this.updateRCStat = function( rootCause, marksLost ) {
    	
    	var option = rcOptions.getOption( rootCause ) ;
    	
    	if( option == null ) return ;
    	
    	if( option.isAvoidable ) {
        	this.numAvoidableTotal++ ;
        	this.avoidableTotalMks += marksLost ;
    	}
    	else {
    		this.numUnavoidableTotal++ ;
        	this.unavoidableTotalMks += marksLost ;
    	}
    	
        if( rootCause == 'RECOLLECTION' ) {
            this.numRecollection++ ;
            this.recollectionMks += marksLost ;
        }
        else if( rootCause == 'CALCULATION' ) {
            this.numCalculation++ ;
            this.calculationMks += marksLost ;
        }
        else if( rootCause == 'INTERPRETATION' ) {
            this.numInterpretation++ ;
            this.interpretationMks += marksLost ;
        }
        else if( rootCause == 'CONCEPT' ) {
            this.numConcept++ ;
            this.conceptMks += marksLost ;
        }
        else if( rootCause == 'LATERAL' ) {
            this.numLateral++ ;
            this.lateralMks += marksLost ;
        }
        else if( rootCause == 'ALIEN_CONCEPT' ) {
            this.numUnknownConcept++ ;
            this.unknownConceptMks += marksLost ;
        }
        else if( rootCause == 'WTF' ) {
            this.numWtf++ ;
            this.wtfMks += marksLost ;
        }
        else if( rootCause == 'LENGTHY' ) {
            this.numLengthy++ ;
            this.lengthyMks += marksLost ;
        }
        else if( rootCause == 'STUPID' ) {
            this.numStupid++ ;
            this.stupidMks += marksLost ;
        }
        else if( rootCause == 'JUDGEMENT_ERROR' ) {
            this.numJudgementError++ ;
            this.judgementErrorMks += marksLost ;
        }
        else if( rootCause == 'UNWARRANTED_RISK' ) {
            this.numUnwarrantedRisk++ ;
            this.unwarrantedRiskMks += marksLost ;
        }
        else if( rootCause == 'WTF_LATERAL' ) {
            this.numWtfLateral++ ;
            this.wtfLateralMks += marksLost ;
        }
    }
}