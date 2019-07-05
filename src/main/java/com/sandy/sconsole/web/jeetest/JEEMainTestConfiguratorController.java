package com.sandy.sconsole.web.jeetest;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
@RequestMapping( "/jeetest/config_main" )
public class JEEMainTestConfiguratorController {
    
    @RequestMapping( "/summaryDashboard" )
    public String jeeMain() {
        return "jeetest/config_main/summary_dashboard" ;
    }
    
    @RequestMapping( "/editTestConfig" )
    public String newTestConfig() {
        return "jeetest/config_main/edit_test" ;
    }
}
