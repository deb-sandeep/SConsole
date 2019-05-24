package com.sandy.sconsole.web.jeetest;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
@RequestMapping( "/jeetest/revision" )
public class JEERevisionController {
    
    @RequestMapping( "/problemList" )
    public String availableExams() {
        return "jeetest/revision/problem_list" ;
    }
}