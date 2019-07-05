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
    
    @RequestMapping( "/test-config-main" )
    public String testConfig() {
        return "jeetest/config_main/test_config" ;
    }

    @RequestMapping( "/exam" )
    public String exam() {
        return "jeetest/exam/exam" ;
    }

    @RequestMapping( "/analysis" )
    public String analysis() {
        return "jeetest/analysis/analysis" ;
    }
    
    @RequestMapping( "/revision" )
    public String revision() {
        return "jeetest/revision/revision" ;
    }
}
