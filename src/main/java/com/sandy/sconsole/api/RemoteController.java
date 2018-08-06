package com.sandy.sconsole.api;

import org.apache.log4j.Logger ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.sconsole.dto.APIRespose ;
import com.sandy.sconsole.dto.ButtonPressEvent ;

@RestController
public class RemoteController {
    
    private static final Logger log = Logger.getLogger( RemoteController.class ) ;
    
    @PostMapping( "/RemoteControl" )
    public APIRespose buttonPressed( @RequestBody ButtonPressEvent event ) {
        log.debug( "Btn press received : " + event.getBtnType() + " - " + 
                                             event.getBtnCode() );
        return new APIRespose( "Success" ) ;
    }
}
