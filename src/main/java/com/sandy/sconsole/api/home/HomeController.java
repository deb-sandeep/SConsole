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
}
