package com.sandy.sconsole.web.jeetest;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
@RequestMapping( "/jeetest/qbm" )
public class JEETestQBMController {
    
    @RequestMapping( "/editQuestion" )
    public String jeeMain() {
        return "jeetest/qbm/edit_question" ;
    }
    
    @RequestMapping( "/qbInsight" )
    public String qbInsight() {
        return "jeetest/qbm/qb_insight" ;
    }
    
    @RequestMapping( "/searchQuestions" )
    public String jeeRed() {
        return "jeetest/qbm/search_questions" ;
    }
}
