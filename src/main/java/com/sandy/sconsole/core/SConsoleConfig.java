package com.sandy.sconsole.core;

import org.springframework.boot.context.properties.* ;
import org.springframework.context.annotation.* ;

@Configuration( "config" )
@PropertySource( "classpath:sconsole.properties" )
@ConfigurationProperties( "sconsole" )
public class SConsoleConfig {

    private boolean recordTestAttempt = false ;

    public boolean isRecordTestAttempt() {
        return recordTestAttempt ;
    }

    public void setRecordTestAttempt( boolean recordTestAttempt ) {
        this.recordTestAttempt = recordTestAttempt ;
    }
}
