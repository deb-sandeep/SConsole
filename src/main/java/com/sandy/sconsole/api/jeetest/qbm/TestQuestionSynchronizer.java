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
    
    static final Logger log = Logger.getLogger( TestQuestionSynchronizer.class ) ;
    
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
            
            tq = tqRepo.findByHash( tqEx.getHash() ) ;
            if( tq == null ) {
                tq = new TestQuestion() ;
            }
            tqEx.populate( tq ) ;
            tq.setSynched( true ) ;
            tqRepo.save( tq ) ;
            
            if( !tqEx.getEmbeddedImages().isEmpty() ) {
                saveEmbeddedImages( tqEx.getEmbeddedImages() ) ;
            }
        }
    }
    
    private void saveEmbeddedImages( List<ImageData> embeddedImages ) 
        throws Exception {
        
        for( ImageData eImg : embeddedImages ) {
            File destFile = new File( SConsole.JEETEST_IMG_DIR, eImg.getImageName() ) ;
            byte[] data = Hex.decodeHex( eImg.getHexEncodedImageData() ) ;
            FileUtils.writeByteArrayToFile( destFile, data ) ;
        }
    }
}
