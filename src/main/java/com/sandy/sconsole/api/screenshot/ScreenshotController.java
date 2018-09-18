package com.sandy.sconsole.api.screenshot;

import java.util.HashMap ;
import java.util.Map ;

import org.apache.log4j.Logger ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.sconsole.SConsole ;

@RestController
public class ScreenshotController {
    
    static final Logger log = Logger.getLogger( ScreenshotController.class ) ;
    
    @GetMapping( "/ScreenCapture" )
    public ResponseEntity<Map<String, String>> captureScreenshot() {
        try {
            Map<String, String> response = new HashMap<String, String>() ;
            response.put( "imgName", SConsole.getApp().getFrame().captureScreenshot( null ) ) ;
            return ResponseEntity.status( HttpStatus.OK ).body( response ) ;
        }
        catch( Exception e ) {
            return ResponseEntity.status( 500 ).body( null ) ;
        }
    }
}
