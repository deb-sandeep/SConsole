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
    public String mainTestConfig() {
        return "jeetest/config_main/test_config" ;
    }

    @RequestMapping( "/test-config-adv" )
    public String advTestConfig() {
        return "jeetest/config_adv/test_config" ;
    }

    @RequestMapping( "/exam-landing" )
    public String examLanding() {
        return "jeetest/exam_landing/exam_landing" ;
    }

    @RequestMapping( "/exam-landing/availableExams" )
    public String availableExams() {
        return "jeetest/exam_landing/available_exams" ;
    }

    @RequestMapping( "/exam-landing/instructionsMain" )
    public String instructionsJEEMain() {
        return "jeetest/exam_landing/instructions_main" ;
    }

    @RequestMapping( "/exam-landing/instructionsAdv" )
    public String instructionsJEEAdv() {
        return "jeetest/exam_landing/instructions_adv" ;
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
