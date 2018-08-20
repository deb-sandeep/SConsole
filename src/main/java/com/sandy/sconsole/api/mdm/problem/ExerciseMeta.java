package com.sandy.sconsole.api.mdm.problem;

import java.util.ArrayList ;
import java.util.List ;

import com.sandy.common.util.StringUtil ;

public class ExerciseMeta {
    
    String subjectName   = null ;
    String topic         = null ;
    String bookName      = null ;
    String chapterId     = null ;
    String exerciseName  = null ;
    int    numProblems[] = new int[0] ;
    
    String rawData = null ;
    
    public ExerciseMeta( String rawData ) 
        throws IllegalArgumentException {
        parse( rawData ) ;
        this.rawData = rawData ;
    }
    
    private void parse( String rawData ) {
        String temp = null ;
        String[] parts = rawData.split( "@", -1 ) ;
        
        if( parts.length != 6 ) {
            throw new IllegalArgumentException( "Input " + rawData + " is non conformant." ) ;
        }
        
        temp = parts[0].trim() ;
        if( StringUtil.isNotEmptyOrNull( temp ) ) {
            this.subjectName = temp ;
        }
        
        temp = parts[1].trim() ;
        if( StringUtil.isNotEmptyOrNull( temp ) ) {
            this.topic = temp ;
        }
        
        temp = parts[2].trim() ;
        if( StringUtil.isNotEmptyOrNull( temp ) ) {
            this.bookName = temp ;
        }
        
        temp = parts[3].trim() ;
        if( StringUtil.isNotEmptyOrNull( temp ) ) {
            this.chapterId = temp ;
        }
        
        temp = parts[4].trim() ;
        if( StringUtil.isNotEmptyOrNull( temp ) ) {
            this.exerciseName = temp ;
        }
        
        temp = parts[5].trim() ;
        if( StringUtil.isNotEmptyOrNull( temp ) ) {
            String numberStr[] = temp.split( "," ) ;
            ArrayList<String> nonBlankStrings = new ArrayList<>() ;
            
            for( String str : numberStr ) {
                if( StringUtil.isNotEmptyOrNull( str ) ) {
                    nonBlankStrings.add( str.trim() ) ;
                }
            }
            
            numProblems = new int[ nonBlankStrings.size() ] ;
            for( int i=0; i<numProblems.length; i++ ) {
                numProblems[i] = Integer.parseInt( nonBlankStrings.get( i ) ) ;
            }
        }
    }
    
    int getTotalNumProblems() {
        int retVal = 0 ;
        for( int i=0; i<getNumExercises(); i++ ) {
            retVal += getNumProblems( i ) ;
        }
        return retVal ;
    }
    
    int getNumExercises() {
        return numProblems.length ;
    }
    
    int getNumProblems( int exercise ) {
        return numProblems[ exercise ] ;
    }
    
    public List<ProblemMeta> generateMetaProblems() {
        
        List<ProblemMeta> problems = new ArrayList<>() ;
        
        for( int i=0; i<this.getNumExercises(); i++ ) {
            for( int p=0; p<this.getNumProblems( i ); p++ ) {
                ProblemMeta problem = this.createProblemMetaTemplate() ;
                if( this.getNumExercises() > 1 ) {
                    problem.setExerciseSubName( String.valueOf( i+1 ) ) ;
                }
                problem.setProblemTag( String.valueOf( p+1 ) );
                problems.add( problem ) ;
            }
        }
        
        return problems ;
    }
    
    private ProblemMeta createProblemMetaTemplate() {
        
        ProblemMeta pMeta = new ProblemMeta() ;
        pMeta.setSubjectName( subjectName ) ;
        pMeta.setTopicName( topic ) ;
        pMeta.setBookName( bookName ) ;
        pMeta.setChapterId( chapterId ) ;
        pMeta.setExerciseName( exerciseName ) ;
        
        return pMeta ;
    }
    
    public String toString() {
        return this.rawData ;
    }
}

