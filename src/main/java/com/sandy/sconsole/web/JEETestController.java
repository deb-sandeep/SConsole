package com.sandy.sconsole.web;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
@RequestMapping( "/jeetest" )
public class JEETestController {
    
    @RequestMapping( {"", "/"} )
    public String jeetest() {
        return "jeetest/landing" ;
    }
    
    @RequestMapping( "/qb-management" )
    public String qbManagement() {
        return "jeetest/qb-management" ;
    }
    
    @RequestMapping( "/main" )
    public String jeeMain() {
        return "jeetest/main" ;
    }
    
    @RequestMapping( "/red" )
    public String jeeRed() {
        return "jeetest/red" ;
    }
    
    @RequestMapping( "/blue" )
    public String jeeBlue() {
        return "jeetest/blue" ;
    }
    
    @RequestMapping( "/green" )
    public String jeeGreen() {
        return "jeetest/green" ;
    }
}
