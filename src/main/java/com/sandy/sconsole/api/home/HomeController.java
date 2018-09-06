package com.sandy.sconsole.api.home;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
public class HomeController {
    
    @RequestMapping( "/" )
    public String home() {
        return "index" ;
    }
    
    @RequestMapping( "/mdm-problem-master" )
    public String mdmProblemMaster() {
        return "mdm-problem-master" ;
    }
    
    @RequestMapping( "/add-session" )
    public String addSession() {
        return "add-session" ;
    }
    
    @RequestMapping( "/burn-calibration" )
    public String burnCalibration() {
        return "burn" ;
    }
}
