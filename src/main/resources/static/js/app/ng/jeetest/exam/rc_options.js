function RCOptions() {
	this.choices = [
		{
			isAvoidable : true,
			displayName : '[A] Recollection failure',
			id          : 'RECOLLECTION'
		},
		{
			isAvoidable : true,
			displayName : '[A] Calculation error',
			id          : 'CALCULATION'
		},
        {
            isAvoidable : true,
            displayName : '[A] Stupid error',
            id          : 'STUPID'
        },
		{
			isAvoidable : true,
			displayName : '[A] Question misinterpretation',
			id          : 'INTERPRETATION'
		},
		{
			isAvoidable : true,
			displayName : '[A] Conceptual error',
			id          : 'CONCEPT'
		},
		{
			isAvoidable : true,
			displayName : '[A] Simple lateral missed',
			id          : 'LATERAL'
		},
        {
            isAvoidable : true,
            displayName : '[A] Judgement error',
            id          : 'JUDGEMENT_ERROR'
        },
        {
            isAvoidable : true,
            displayName : '[A] Unwarranted risk',
            id          : 'UNWARRANTED_RISK'
        },
		{
			isAvoidable : false,
			displayName : '[U] Unknown Concept',
			id          : 'ALIEN_CONCEPT'
		},
		{
			isAvoidable : false,
			displayName : '[U] WTF',
			id          : 'WTF'
		},
        {
            isAvoidable : false,
            displayName : '[U] WTF lateral',
            id          : 'WTF_LATERAL'
        },
		{
			isAvoidable : false,
			displayName : '[U] Abandoned - Lengthy',
			id          : 'LENGTHY'
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