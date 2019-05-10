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
    
    @RequestMapping( "/editTestConfig" )
    public String newTestConfig() {
        return "jeetest/config/edit_test" ;
    }
}