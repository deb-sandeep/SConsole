function RCOptions() {
	this.choices = [
        {
            isAvoidable : true,
            displayName : '[A] Recollection failure',
            id          : 'RECOLLECTION',
            shortName   : 'Recollection'
        },
        {
            isAvoidable : true,
            displayName : '[A] Calculation error',
            id          : 'CALCULATION',
            shortName   : 'Calculation'
        },
        {
            isAvoidable : true,
            displayName : '[A] Stupid error',
            id          : 'STUPID',
            shortName   : 'Stupidity'
        },
        {
            isAvoidable : true,
            displayName : '[A] Question misinterpretation',
            id          : 'INTERPRETATION',
            shortName   : 'Interpretation'
        },
        {
            isAvoidable : true,
            displayName : '[A] Conceptual error',
            id          : 'CONCEPT',
            shortName   : 'Concept'
        },
        {
            isAvoidable : true,
            displayName : '[A] Simple lateral missed',
            id          : 'LATERAL',
            shortName   : 'Lateral'
        },
        {
            isAvoidable : true,
            displayName : '[A] Judgement error',
            id          : 'JUDGEMENT_ERROR',
            shortName   : 'Judgement'
        },
        {
            isAvoidable : true,
            displayName : '[A] Unwarranted risk',
            id          : 'UNWARRANTED_RISK',
            shortName   : 'Risk'
        },
        {
            isAvoidable : false,
            displayName : '[U] Unknown Concept',
            id          : 'ALIEN_CONCEPT',
            shortName   : 'Alien concept'
        },
        {
            isAvoidable : false,
            displayName : '[U] WTF',
            id          : 'WTF',
            shortName   : 'WTF'
        },
        {
            isAvoidable : false,
            displayName : '[U] WTF lateral',
            id          : 'WTF_LATERAL',
            shortName   : 'WTF lateral'
        },
        {
            isAvoidable : false,
            displayName : '[U] Abandoned - Lengthy',
            id          : 'LENGTHY',
            shortName   :'Lengthy'
        }
	] ;
	
	this.getOption = function( id ) {
		for( var i=0; i<this.choices.length; i++ ) {
			if( this.choices[i].id == id ) {
				return this.choices[i] ;
			}
		}
		return null ;
	}
}