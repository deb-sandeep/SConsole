package com.sandy.sconsole.api.jeetest.qbm;

import java.io.File ;
import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.List ;

import org.apache.commons.codec.binary.Hex ;
import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.jeetest.qbm.TestQuestionEx.ImageData ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.dao.repository.master.TestQuestionRepository ;

import okhttp3.MediaType ;
import okhttp3.OkHttpClient ;
import okhttp3.Request ;
import okhttp3.RequestBody ;

public class TestQuestionSynchronizer {
    
    private static final Logger log = Logger.getLogger( TestQuestionSynchronizer.class ) ;
    
    public static final MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8") ; 
    
    private TestQuestionRepository tqRepo = null ;
    private String serverName = null ;
    
    public TestQuestionSynchronizer() {
        this( null ) ;
    }
    
    public TestQuestionSynchronizer( String serverName ) {
        tqRepo = SConsole.getAppContext()
                         .getBean( TestQuestionRepository.class ) ;
        this.serverName = serverName ;
    }

    public void syncQuestions( Integer[] ids ) throws Exception {
        
        log.debug( "Synching test questions to server = " + this.serverName ) ;
        
        List<Integer> qIds = Arrays.asList( ids ) ;
        Iterable<TestQuestion> results = tqRepo.findAllById( qIds ) ;
        
        List<TestQuestionEx> questions = new ArrayList<>() ;
        for( TestQuestion tq : results ) {
            questions.add( new TestQuestionEx( tq ) ) ;
        }
        
        ObjectMapper objMapper = new ObjectMapper() ;
        String json = objMapper.writeValueAsString( questions ) ;
        postJSONToServer( json ) ;
        
        for( TestQuestion tq : results ) {
            tq.setSynched( true ) ;
        }
        tqRepo.saveAll( results ) ;
    }
    
    private void postJSONToServer( String json ) 
        throws Exception {
        
        String url = "http://" + this.serverName + "/ImportNewTestQuestions" ;
        
        log.debug( "Posting to server : " + url ) ;
        log.debug( "Data size = " + json.length() ) ;
        
        
        OkHttpClient client = new OkHttpClient() ;
        RequestBody body = RequestBody.create( JSON, json ) ;
        Request request = new Request.Builder()
                                     .url( url )
                                     .post( body )
                                     .build() ;
        client.newCall( request ).execute() ;
    }

    public void importQuestions( List<TestQuestionEx> questions ) 
        throws Exception {
        
        TestQuestion tq = null ;
        for( TestQuestionEx tqEx : questions ) {
            
            log.debug( "Importing question with hash = " + tqEx.getHash() ) ;
            tq = tqRepo.findByHash( tqEx.getHash() ) ;
            if( tq == null ) {
                tq = new TestQuestion() ;
                log.debug( "Creating new question" ) ;
            }
            else {
                log.debug( "Question already exists - updating it" ) ;
            }
            tqEx.populate( tq ) ;
            tq.setSynched( true ) ;
            tqRepo.save( tq ) ;
            log.debug( "Question imported" ) ;
            
            if( !tqEx.getEmbeddedImages().isEmpty() ) {
                log.debug( "Importing embedded images." ) ;
                saveEmbeddedImages( tqEx.getEmbeddedImages() ) ;
            }
        }
    }
    
    private void saveEmbeddedImages( List<ImageData> embeddedImages ) 
        throws Exception {
        
        for( ImageData eImg : embeddedImages ) {
            
            log.debug( "Importing embedded image = " + eImg.getImageName() ) ;
            File destFile = new File( SConsole.JEETEST_IMG_DIR, eImg.getImageName() ) ;
            byte[] data = Hex.decodeHex( eImg.getHexEncodedImageData() ) ;
            FileUtils.writeByteArrayToFile( destFile, data ) ;
        }
    }
}
