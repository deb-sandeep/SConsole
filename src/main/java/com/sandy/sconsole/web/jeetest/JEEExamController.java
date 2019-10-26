package com.sandy.sconsole.web.jeetest;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
@RequestMapping( "/jeetest/exam" )
public class JEEExamController {
    
    @RequestMapping( "/mainTest" )
    public String mainTest() {
        return "jeetest/exam_main/exam_main_base" ;
    }

    @RequestMapping( "/mainTestExam" )
    public String mainTestExam() {
        return "jeetest/exam_main/exam_main_test" ;
    }

    @RequestMapping( "/mainTestResult" )
    public String mainTestResult() {
        return "jeetest/exam_main/exam_main_result" ;
    }
}
