function MatrixMeta() {
    this.col1EntryKeys = ['A', 'B', 'C', 'D' ] ;
    this.col2EntryKeys = ['p', 'q', 'r', 's', 't' ] ;
}

function SolutionGrid( matrixMeta ) {
    this.meta = matrixMeta ;
    this.grid = null ;
    
    this.init = function() {
        this.grid = new Array( this.meta.col1EntryKeys.length ) ;
        for( var i=0; i<this.meta.col1EntryKeys.length; i++ ) {
            this.grid[i] = new Array( this.meta.col2EntryKeys.length ) ; 
            for( var j=0; j<this.meta.col2EntryKeys.length; j++ ) {
                this.grid[i][j] = false ;
            }
        }
    }
    
    this.getOptionString = function() {
        
        var optionString = "" ;
        for( var i=0; i<this.meta.col1EntryKeys.length; i++ ) {
            optionString += this.meta.col1EntryKeys[i] + " - " ;
            for( var j=0; j<this.meta.col2EntryKeys.length; j++ ) {
                if( this.grid[i][j] == true ) {
                    optionString += this.meta.col2EntryKeys[j] + "," ;
                }
            }
            optionString = optionString.substring( 0, optionString.length-1 ) ;
            if( i < this.meta.col1EntryKeys.length - 1 ) {
                optionString += " ; " ;
            }
        }
        return optionString ;
    }
    
    this.mutate = function() {

    	// For each row.. 
        for( var i=0; i<this.meta.col1EntryKeys.length; i++ ) {
        	var gridRow = this.grid[i] ;
        	var numMutations = 0 ;
        	
        	var numSelections = this.getNumCellsSelected( gridRow ) ;
        	
        	var iterIndex = 0 ;
        	while( numMutations <= 0 || 
        		   this.getNumCellsSelected( gridRow ) != numSelections ) {

        		if( iterIndex > gridRow.length-1 ) {
        			iterIndex = 0 ;
        		}
        		
        		// Mutate this index with a 10% probability
            	if( Math.random() >= 0.9 ) {
            		numMutations++ ;
            		gridRow[iterIndex] = gridRow[iterIndex] ? false : true ;
            	}

            	iterIndex++ ;
        	}
        }
    }
    
    this.equals = function( aGrid ) {
        for( var i=0; i<this.meta.col1EntryKeys.length; i++ ) {
            for( var j=0; j<this.meta.col2EntryKeys.length; j++ ) {
                if( this.grid[i][j] == true && 
                    aGrid.grid[i][j] != true ) {
                    return false ;
                }
                
                if( aGrid.grid[i][j] == true && 
                    this.grid[i][j]  != true ) {
                    return false ;
                }
            }
        }
        return true ;
    }
    
    this.getNumCellsSelected = function( gridRow ) {
    	var numSelected = 0 ;
        for( var j=0; j<this.meta.col2EntryKeys.length; j++ ) {
            if( gridRow[j] ) numSelected++ ;
        }
        return numSelected ;
    }
    
    this.init() ;
}

sConsoleApp.controller( 'MMTEditorController', function( $scope, $http ) {
	
    $scope.col1KeysRange = "A-D" ;
    $scope.col2KeysRange = "p-t" ;
    $scope.meta = new MatrixMeta() ;
    
    $scope.$on( 'mmtEditorIsBeingShown', function( event, entry ){
        if( $scope.$parent.entry == entry ) {
            $scope.refreshGridKeys() ;
        }
    }) ;
    
    $scope.grids = [ 
        new SolutionGrid( $scope.meta ),
        new SolutionGrid( $scope.meta ),
        new SolutionGrid( $scope.meta ),
        new SolutionGrid( $scope.meta )
    ] ;
    
    $scope.hideEditor = function() {
        $scope.$parent.entry.showMMTOptionsEditor = false ; 
    } ;
    
    $scope.refreshGridKeys = function() {
        keyRefresh( $scope.col1KeysRange, $scope.meta.col1EntryKeys ) ;
        keyRefresh( $scope.col2KeysRange, $scope.meta.col2EntryKeys ) ;
        for( var i=0; i<$scope.grids.length; i++ ) {
            $scope.grids[i].init() ;
        }
    }
    
    $scope.masterMatchChanged = function( rowIndex, colIndex ) {
        var cellVal = $scope.grids[0].grid[rowIndex][colIndex] ;
        for( var i=1; i<$scope.grids.length; i++ ) {
            $scope.grids[i].grid[rowIndex][colIndex] = cellVal ;
        }
    }
    
    $scope.applyMCAOptions = function() {
        var options = [] ;
        for( var i=0; i<$scope.grids.length; i++ ) {
            var grid = $scope.grids[i] ;
            var optionString = grid.getOptionString() ;
            options.push( optionString ) ;
        }
        
        var orderArray = [0,1,2,3] ;
        shuffle( orderArray ) ;
        
        var entryMMTOptions = $scope.$parent.entry.mmtOptions ;
        entryMMTOptions.length = 0 ;
        
        for( var i=0; i<orderArray.length; i++ ) {
            var index = orderArray[i] ;
            if( index == 0 ) {
                $scope.$parent.entry.aText = "" + (i+1) ;
            }
            entryMMTOptions.push( options[index] ) ;
        }
        
        for( var i=0; i<entryMMTOptions.length; i++ ) {
            console.log( entryMMTOptions[i] ) ;
        }
        console.log( $scope.$parent.entry.aText ) ;
        
        $scope.$parent.entry.showMMTOptionsEditor = false ;
    }
    
    $scope.mutateOptions = function() {
        for( var i=0; i<$scope.grids.length; i++ ) {
            var grid = $scope.grids[i] ;
            if( i > 0 ) {
            	grid.mutate() ;
            } 
        }
    }
    
    $scope.getOptionBackgroundStyle = function( optIndex ) {
    	
    	var srcGrid = $scope.grids[optIndex] ;
        for( var i=0; i<$scope.grids.length; i++ ) {
        	if( i == optIndex ) continue ;
            var tgtGrid = $scope.grids[i] ;
            if( srcGrid.equals( tgtGrid ) ) {
            	return {'background-color':'red'} ;
            } 
        }
        return null ;
    }
    
    function shuffle( array ) {
        var currentIndex = array.length, temporaryValue, randomIndex;
        
        // While there remain elements to shuffle...
        while( 0 !== currentIndex ) {
            // Pick a remaining element...
            randomIndex = Math.floor(Math.random() * currentIndex);
            currentIndex -= 1;
            
            // And swap it with the current element.
            temporaryValue = array[currentIndex];
            array[currentIndex] = array[randomIndex];
            array[randomIndex] = temporaryValue;
        }
        return array;
    }
    
    function keyRefresh( range, keyArray ) {
        var fromChar = range.charAt(0) ;
        var toChar = range.charAt(2) ;
        
        keyArray.length = 0 ;
        var key = fromChar ;
        do {
            keyArray.push( key ) ;
            key = String.fromCharCode( key.charCodeAt() + 1 ) ;
        }
        while ( key.charCodeAt() <= toChar.charCodeAt() ) ;
    }
} ) ;