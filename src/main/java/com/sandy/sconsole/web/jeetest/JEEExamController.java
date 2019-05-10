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
    
    @RequestMapping( "/instructionsMain" )
    public String mainInstructions() {
        return "jeetest/exam/instruction_main" ;
    }
    
    @RequestMapping( "/instructionsAdv" )
    public String advInstructions() {
        return "jeetest/exam/instruction_adv" ;
    }

    @RequestMapping( "/mainTest" )
    public String mainTest() {
        return "jeetest/exam/main_test" ;
    }
    
    @RequestMapping( "/ui" )
    public String ui() {
        return "jeetest/exam/ui" ;
    }
}
