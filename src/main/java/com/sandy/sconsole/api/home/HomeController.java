package com.sandy.sconsole.api.home;

import org.springframework.stereotype.Controller ;
import org.springframework.web.bind.annotation.RequestMapping ;

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
