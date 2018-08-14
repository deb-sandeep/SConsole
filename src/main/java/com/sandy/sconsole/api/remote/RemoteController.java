package com.sandy.sconsole.api.remote;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.sconsole.core.api.APIRespose ;
import com.sandy.sconsole.core.remote.RemoteKeyEventRouter ;

@RestController
public class RemoteController {
    
    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( RemoteController.class ) ;
    
    @Autowired
    private RemoteKeyEventRouter keyEventRouter = null ;
    
    @PostMapping( "/RemoteControl" )
    public APIRespose buttonPressed( @RequestBody KeyPressEvent event ) {
        keyEventRouter.routeKeyEvent( event ) ;
        return new APIRespose( "Success" ) ;
    }
}
