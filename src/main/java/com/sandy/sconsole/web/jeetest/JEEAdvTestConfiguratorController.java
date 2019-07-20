package com.sandy.sconsole.web.jeetest;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
@RequestMapping( "/jeetest/config_adv" )
public class JEEAdvTestConfiguratorController {
    
    @RequestMapping( "/summaryDashboard" )
    public String jeeMain() {
        return "jeetest/config_adv/summary_dashboard" ;
    }
}
