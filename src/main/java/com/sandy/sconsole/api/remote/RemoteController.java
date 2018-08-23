package com.sandy.sconsole.api.remote;

import org.apache.log4j.* ;
import org.springframework.beans.factory.annotation.* ;
import org.springframework.web.bind.annotation.* ;

import com.sandy.sconsole.core.api.* ;
import com.sandy.sconsole.core.remote.* ;

@RestController
public class RemoteController {
    
    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( RemoteController.class ) ;
    
    @Autowired
    private RemoteKeyEventRouter keyEventRouter = null ;
    
    @PostMapping( "/RemoteControl" )
    public APIResponse buttonPressed( @RequestBody KeyEvent event ) {
        keyEventRouter.routeKeyEvent( event ) ;
        return new APIResponse( "Success" ) ;
    }
}
