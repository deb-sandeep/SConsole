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
        return "jeetest/qb-management" ;
    }
    
    @RequestMapping( "/editQuestion" )
    public String jeeMain() {
        return "jeetest/edit_question" ;
    }
    
    @RequestMapping( "/searchQuestions" )
    public String jeeRed() {
        return "jeetest/search_questions" ;
    }
}