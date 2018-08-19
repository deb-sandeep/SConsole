package com.sandy.sconsole.api.mdm.problem;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.commons.io.IOUtils ;
import org.apache.log4j.Logger ;

public class ProblemMasterRawDataExpander {

    private static final Logger log = Logger.getLogger( ProblemMasterRawDataExpander.class ) ;

    public List<ExerciseMeta> generateExerciseMeta( List<String> inputs ) {
        List<ExerciseMeta> exMetaList = new ArrayList<>() ;
        for( String input : inputs ) {
            ExerciseMeta exMeta = new ExerciseMeta( input ) ;
            if( exMeta.getTotalNumProblems() > 0 ) {
                exMetaList.add( exMeta ) ;
            }
        }
        return exMetaList ;
    }
    
    public static void main( String[] args ) throws Exception {
        List<String> lines = IOUtils.readLines( ProblemMasterRawDataExpander.class.getResourceAsStream( "/Phy-Problems.txt" ) ) ;
        
        ProblemMasterRawDataExpander expander = new ProblemMasterRawDataExpander() ;
        List<ExerciseMeta> exMetaList = expander.generateExerciseMeta( lines ) ;
        
        for( ExerciseMeta exMeta : exMetaList ) {
            List<ProblemMeta> problems = exMeta.generateMetaProblems() ;
            log.debug( "Total problems generated = " + problems.size() ) ;
            for( ProblemMeta problem : problems ) {
                log.debug( problem ) ;
            }
        }
    }
}
