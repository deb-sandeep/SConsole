package com.sandy.sconsole.web.jeetest;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
@RequestMapping( "/jeetest" )
public class JEETestLandingController {
    
    @RequestMapping( {"", "/"} )
    public String jeetest() {
        return "jeetest/landing" ;
    }
    
    @RequestMapping( "/qb-management" )
    public String qbManagement() {
        return "jeetest/qbm/qb_management" ;
    }
    
    @RequestMapping( "/test-config" )
    public String testConfig() {
        return "jeetest/config/test_config" ;
    }
}
