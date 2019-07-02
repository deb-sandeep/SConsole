sConsoleApp.controller( 'EditQuestionController', 
		                function( $scope, $http, $sce, $routeParams, $window, $timeout ) {
	
	var snippets = new LatexSnippets() ;
	var editHelper = new EditHelper() ;
	
	$scope.$parent.navBarTitle = "Create / Edit Questions" ;
	$scope.question = null ;
	$scope.lastSavedQuestion = null ;
	$scope.isAutoPreviewOn = true ;
	$scope.formattedContent = null ;
	$scope.answerType = "" ;
	$scope.answerTypes = [ "", "iChem", "iMath", "Math", "ART", "IMG" ] ;
	$scope.validationErrors = [] ;
	
	// --- [START] Controller initialization
	
	// First load the dropdown master data from the server. The drop down
	// master data will consist of subjects, topics, books etc.
	$scope.$parent.loadQBMMasterData() ;
	
	loadQuestionForEdit( $routeParams.id ) ;
	
	setInterval( function(){
		if( $scope.isAutoPreviewOn ) {
			generateFormattedTextAndRenderPreview() ;
		}
	}, 3000 ) ;
	
	MathJax.Hub.Queue(["Typeset",MathJax.Hub, "latexSnippetDlg"]);
	
	// --- [END] Controller initialization
	
	// --- [START] Scope functions
	
	$scope.isExistingQuestion = function() {
		return ( $scope.question    != null ) && 
		       ( $scope.question.id > 0   ) ;
	}
	
	$scope.editNewQuestion = function() {
		loadQuestionForEdit( -1 ) ;
	}
	
	$scope.discardChange = function() {
		$scope.question = jQuery.extend(true, {}, $scope.lastSavedQuestion ) ;
	}
	
	$scope.closeValidationErrorsDialog = function() {
		$( '#validationErrorsDialog' ).modal( 'hide' ) ;
		$scope.validationErrors.length = 0 ;
	}

	$scope.save = function() {
		if( inputsValidated() ) {
			// First parameter  - boolean - shouldRenderPreview
			// Second parameter - boolean - createNewQuestion 
			saveQuestionOnServer( true, false ) ;
		}
		else {
			$( '#validationErrorsDialog' ).modal( 'show' ) ;
		}
	}

	$scope.saveAndCreateNew = function() {
		if( inputsValidated() ) {
			saveQuestionOnServer( false, true ) ;
		}
		else {
			$( '#validationErrorsDialog' ).modal( 'show' ) ;
		}
	}

	$scope.preview = function() {
		// Get the question text formatted by the server
		// Do the following on response receipt
		// 		Save the formatted text as the question formatted text
		// 		Render the preview of the formatted text.
		generateFormattedTextAndRenderPreview() ;
	}
	
	$scope.prePopulateAnswerText = function() {
		
		if( $scope.question.id > 0 ) return ;
		if( $scope.question.questionText != null && 
		    $scope.question.questionText != "" ) return ;
		
		var answerText = "" ;
		
		if( $scope.question.questionText == null || 
		    $scope.question.questionText == "" ) {
			
			if( $scope.answerType == "iChem" ) {
				answerText = "\n\n1. {{@ichem }}\n2. {{@ichem }}\n3. {{@ichem }}\n4. {{@ichem }}"
			}
			else if( $scope.answerType == "iMath" ) {
				answerText = "\n\n1. {{@imath }}\n2. {{@imath }}\n3. {{@imath }}\n4. {{@imath }}"
			}
			else if( $scope.answerType == "Math" ) {
				answerText = "\n\n{{@opt\n1. \n2. \n3. \n4. }}"
			}
			else if( $scope.answerType == "ART" ) {
				answerText = "**Statement-1 :** \n" +
				"\n" +
				"**Statement-2 :** \n" + 
				"\n" + 
				"1. Statement-1 is True, Statement-2 is True, Statement 2 is a correct explanation for Statement 1\n" + 
				"2. Statement-1 is True, Statement-2 is True, Statement 2 is NOT a correct explanation for Statement 1\n" + 
				"3. Statement-1 is True, Statement-2 is False\n" + 
				"4. Statement-1 is False, Statement-2 is True\n" ; 
			}
			else if( $scope.answerType == "IMG" ) {
				var qImagePath = editHelper.constructQImagePath( true ) ;
				if( qImagePath != null ) {
					answerText = "{{@img " + qImagePath + "}}" ;
				}
			}
		}
		
		$scope.question.questionText = answerText ;
    	$timeout( function(){
    		var element = $window.document.getElementById( "questionText" ) ; 
    		element.setSelectionRange( 0, 0 ) ;
    	}) ;
	}
	
	$scope.applyLatexSnippet = function( snippetId ) {
		var snippet = snippets.getSnippet( snippetId ) ;
		$( '#latexSnippetsDlg' ).modal( 'hide' ) ;
		$timeout( function() {
			insertAtCursor( snippet ) ;
		}) ;
	}
	
	$scope.questionTypeChanged = function() {
		$scope.question.lctContext = "" ;
	}
	
	// --- [START] Internal Questions
	
	function insertAtCursor( myValue ) {

		var myField = $window.document.getElementById( "questionText" ) ;
		
		//IE support
	    if (document.selection) {
	        myField.focus();
	        sel = document.selection.createRange();
	        sel.text = myValue;
	    }
	    
	    //MOZILLA and others
	    else if (myField.selectionStart || myField.selectionStart == '0') {
	        var startPos = myField.selectionStart;
	        var endPos = myField.selectionEnd;
	        myField.value = myField.value.substring(0, startPos)
	            + myValue
	            + myField.value.substring(endPos, myField.value.length);
	    } 
	    else {
	        myField.value += myValue;
	    }
	    
	    $scope.question.questionText = myField.value ;
	}	
	
	function inputsValidated() {
		var q = $scope.question ;
		var errorsFound = false ;
		
		if( q.subject == null || q.subject.name == null ) {
			errorsFound = true ;
			$scope.validationErrors.push( "Subject name should be specified." ) ;
		}
		
		if( q.topic == null ) {
			errorsFound = true ;
			$scope.validationErrors.push( "Topic should be specified." ) ;
		}
		
		if( q.book == null ) {
			errorsFound = true ;
			$scope.validationErrors.push( "Book should be specified." ) ;
		}
		
		if( q.questionRef == null || 
		    ( typeof q.questionType === 'undefined' ) || 
		    q.questionRef.trim().length == 0 ) {
			errorsFound = true ;
			$scope.validationErrors.push( "Question Reference should be specified." ) ;
		}
		
		if( q.questionText == null || 
		    ( typeof q.questionText === 'undefined' ) || 
		    q.questionText.trim().length == 0 ) {
			errorsFound = true ;
			$scope.validationErrors.push( "Question Text should be specified." ) ;
		}
		
		if( q.questionType == "LCT" ) {
			if( q.lctContext.length == 0 ) {
				errorsFound = true ;
				$scope.validationErrors.push( "LCT context is absent." ) ;
			}
		}
		else {
			if( q.lctContext != null && q.lctContext.length > 0 ) {
				errorsFound = true ;
				$scope.validationErrors.push( "LCT context present in a non LCT question." ) ;
			}
		}

		if( q.answerText == null || 
		    ( typeof q.answerText === 'undefined' ) || 
		    q.answerText.trim().length == 0 ) {
			errorsFound = true ;
			$scope.validationErrors.push( "Answer Text should be specified." ) ;
		}
		else {
			if( !editHelper.isAnswerSemanticallyValid( q.answerText, 
					                                   $scope.question.questionType,
					                                   $scope.validationErrors ) ) {
				errorsFound = true ;
			}
		}

		if( errorsFound ) {
			console.log( "Errors found in user inputs." ) ;
			return false ;
		}
		
		return true ;
	}
	
	function loadQuestionForEdit( questionId ) {
    	
    	if( typeof questionId === 'undefined' ) {
    		questionId = -1 ;
    	}
    	console.log( "Loading question for edit. ID = " + questionId ) ;
    	
        $scope.$parent.interactingWithServer = true ;
        $http.get( '/TestQuestion/' + questionId )
        .then( 
                function( response ){
                    console.log( "Test Question received." ) ;
                    console.log( response ) ;
                    $scope.question = response.data ;
                    
                    if( questionId == -1 && $scope.lastSavedQuestion != null ) {
                    	$scope.question.subject               = $scope.lastSavedQuestion.subject ;
                    	$scope.question.topic                 = $scope.lastSavedQuestion.topic ;
                    	$scope.question.book                  = $scope.lastSavedQuestion.book ;
                        $scope.question.targetExam            = $scope.lastSavedQuestion.targetExam ;       
                        $scope.question.questionType          = $scope.lastSavedQuestion.questionType ;         
                        $scope.question.lctContext            = $scope.lastSavedQuestion.lctContext ;         
                        $scope.question.lateralThinkingLevel  = $scope.lastSavedQuestion.lateralThinkingLevel ;                 
                        $scope.question.projectedSolveTime    = $scope.lastSavedQuestion.projectedSolveTime ;
                        $scope.question.questionRef           = editHelper.nextQRef( $scope.lastSavedQuestion.questionRef ) ;
                    } 
                    
                    if( $scope.question.questionFormattedText == null ) {
                    	$scope.question.questionFormattedText = "" ;
                    }
                    
                    if( questionId == -1 ) {
                    	$timeout( function(){
                    		var element = $window.document.getElementById( "answer" ) ; 
                    		element.focus() ;
                    	}) ;
                    }
                    
                    $scope.lastSavedQuestion = jQuery.extend(true, {}, response.data) ;
                    renderPreview( $scope.question.questionFormattedText ) ;
                }, 
                function( error ){
                    console.log( "Error getting Test Question data." ) ;
                    console.log( error ) ;
                    $scope.$parent.addErrorAlert( "Could not fetch question." ) ;
                }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
	
    function saveQuestionOnServer( previewRender, newQuestionEdit ) {
    	
    	console.log( "Saving question on server." ) ;
        $scope.$parent.interactingWithServer = true ;
        $http.post( '/TestQuestion', $scope.question )
        .then( 
            function( response ){
                console.log( "Successfully saved question." ) ;
                $scope.question = response.data ;
                $scope.lastSavedQuestion = jQuery.extend(true, {}, response.data); ;
                if( newQuestionEdit ) {
                	$scope.editNewQuestion() ;
                }
                else if( previewRender ) {
                	renderPreview( $scope.question.questionFormattedText ) ;
                }
            }, 
            function( error ){
                console.log( "Error saving question on server." + error ) ;
                var errMsg = "Server error." ;
                if( error.status == 412 ) {
                	errMsg = "Duplicate question found." ;
                }
                $scope.$parent.addErrorAlert( "Could not save question. " + errMsg ) ;
            }
        )
        .finally(function() {
            $scope.$parent.interactingWithServer = false ;
        }) ;
    }
    
	function renderPreview( markupText ) {
		console.log( "Rendering preview with formatted text." ) ;
		$scope.formattedContent = markupText ;
	}
	
	function generateFormattedTextAndRenderPreview() {
		
		var questionText = "" ;
		
		if( $scope.question == null ) {
			return ;
		}
		
		if( $scope.question.lctContext != null && 
			$scope.question.lctContext.length > 0 ) {
			questionText = "<div class='lct-context'>" + $scope.question.lctContext + "</div>" ;
		}
		
		if( $scope.question.questionText != null && 
		    $scope.question.questionText.length > 0 ) {
			questionText += $scope.question.questionText ;
		}
		
        if( questionText == "" ) {
        	return ;
        }
        
        $http.post( '/FormattedText', questionText )        
        .then( 
            function( response ){
                console.log( "Formatted text received." ) ;
                console.log( response ) ;
                $scope.question.questionFormattedText = response.data.fmtText ;
                renderPreview( response.data.fmtText ) ;
            }, 
            function( error ){
                console.log( "Error getting book details." + error ) ;
                $scope.$parent.addErrorAlert( "Error generating formatted text" ) ;
            }
        ) ;
	}
} ) ;