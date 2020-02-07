package com.sandy.sconsole.web.jeetest;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
@RequestMapping( "/jeetest/exam" )
public class JEEExamController {
    
    @RequestMapping( "/mainTest" )
    public String mainTest() {
        return "jeetest/exam_xmain/exam_xmain_base" ;
    }

    @RequestMapping( "/mainTestExam" )
    public String mainTestExam() {
        return "jeetest/exam_main/exam_main_test" ;
    }

    @RequestMapping( "/mainTestResult" )
    public String mainTestResult() {
        return "jeetest/exam_main/exam_main_result" ;
    }

    @RequestMapping( "/advTest" )
    public String advTest() {
        return "jeetest/exam_adv/exam_adv_base" ;
    }

    @RequestMapping( "/advTestExam" )
    public String advTestExam() {
        return "jeetest/exam_adv/exam_adv_test" ;
    }

    @RequestMapping( "/advTestResult" )
    public String advTestResult() {
        return "jeetest/exam_adv/exam_adv_result" ;
    }
    
    @RequestMapping( "/xMainTest" )
    public String xMainTest() {
        return "jeetest/exam_adv/exam_xmain_base" ;
    }

    @RequestMapping( "/xMainTestExam" )
    public String xMainTestExam() {
        return "jeetest/exam_xmain/exam_xmain_test" ;
    }

    @RequestMapping( "/xMainTestResult" )
    public String xMainTestResult() {
        return "jeetest/exam_xmain/exam_xmain_result" ;
    }
}
