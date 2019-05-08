package com.sandy.sconsole.web.jeetest;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
@RequestMapping( "/jeetest/exam" )
public class JEEExamController {
    
    @RequestMapping( "/availableExams" )
    public String availableExams() {
        return "jeetest/exam/available_exams" ;
    }
}
