package com.sandy.sconsole.web.jeetest;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
@RequestMapping( "/jeetest/config" )
public class JEETestConfiguratorController {
    
    @RequestMapping( "/summaryDashboard" )
    public String jeeMain() {
        return "jeetest/config/summary_dashboard" ;
    }
    
    @RequestMapping( "/newTestConfig" )
    public String newTestConfig() {
        return "jeetest/config/new_test" ;
    }
}
