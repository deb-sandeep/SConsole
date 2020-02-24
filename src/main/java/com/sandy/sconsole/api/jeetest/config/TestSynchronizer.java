package com.sandy.sconsole.api.jeetest.config;

import org.apache.log4j.Logger ;

import com.fasterxml.jackson.databind.ObjectMapper ;

import okhttp3.MediaType ;
import okhttp3.OkHttpClient ;
import okhttp3.Request ;
import okhttp3.RequestBody ;
import okhttp3.Response ;

public class TestSynchronizer {
    
    static final Logger log = Logger.getLogger( TestSynchronizer.class ) ;
    
    public static final MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8") ; 
    
    private String serverName = null ;
    
    public TestSynchronizer( String serverName ) {
        this.serverName = serverName ;
    }

    public void syncTest( TestConfiguration config ) throws Exception {
        
        TestConfiguration clone = ( TestConfiguration )config.clone() ;
        if( !config.getTestConfigIndex().isSynched() ) {
            clone.setId( -1 ) ;
            clone.setTestConfigIndex( null ) ;
        }
        
        ObjectMapper objMapper = new ObjectMapper() ;
        String json = objMapper.writeValueAsString( clone ) ;
        postJSONToServer( json ) ;
    }
    
    private void postJSONToServer( String json ) 
        throws Exception {
        
        String url = "http://" + this.serverName + "/TestConfiguration" ;
        
        OkHttpClient client = new OkHttpClient() ;
        RequestBody body = RequestBody.create( JSON, json ) ;
        Request request = new Request.Builder()
                                     .url( url )
                                     .post( body )
                                     .build() ;
        Response response = null ;
        
        try {
            response = client.newCall( request ).execute() ;
        }
        finally {
            if( response != null ) {
                response.body().close() ;
            }
        }
    }
}
