package com.sandy.sconsole.api;

import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RestController ;

@RestController
public class HelloController {

    @RequestMapping( "/" )
    public String index() {
        return "Hello from SConsole!" ;
    }
}
