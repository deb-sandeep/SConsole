package com.sandy.sconsole.web.jeetest;

import org.springframework.stereotype.* ;
import org.springframework.web.bind.annotation.* ;

@Controller
@RequestMapping( "/jeetest/analysis" )
public class JEEExamAnalysisController {
    
    @RequestMapping( "/availableAttempts" )
    public String availableExams() {
        return "jeetest/analysis/available_attempts" ;
    }

    @RequestMapping( "/attemptDetails" )
    public String attemptDetails() {
        return "jeetest/analysis/attempt_details" ;
    }

    @RequestMapping( "/testAttemptTimeSequence" )
    public String testAttemptTimeSequence() {
        return "jeetest/analysis/attempt_time_sequence" ;
    }

    @RequestMapping( "/testAttemptLapDetails" )
    public String testAttemptLapDetails() {
        return "jeetest/analysis/attempt_lap_details" ;
    }
}