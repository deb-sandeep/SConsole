package com.sandy.sconsole.web;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
@RequestMapping( "/jeetest" )
public class JEETestController {
    
    @RequestMapping( {"", "/"} )
    public String jeetest() {
        return "jeetest/landing" ;
    }
    
    @RequestMapping( "/qb-management" )
    public String qbManagement() {
        return "jeetest/qbm/qb-management" ;
    }
    
    @RequestMapping( "/qbm/editQuestion" )
    public String jeeMain() {
        return "jeetest/qbm/edit_question" ;
    }
    
    @RequestMapping( "/qbm/qbInsight" )
    public String qbInsight() {
        return "jeetest/qbm/qb_insight" ;
    }
    
    @RequestMapping( "/qbm/searchQuestions" )
    public String jeeRed() {
        return "jeetest/qbm/search_questions" ;
    }
}
