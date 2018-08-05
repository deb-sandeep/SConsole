package com.sandy.sconsole;

import org.springframework.boot.context.properties.ConfigurationProperties ;
import org.springframework.context.annotation.Configuration ;
import org.springframework.context.annotation.PropertySource ;

@Configuration( "config" )
@PropertySource( "classpath:sconsole.properties" )
@ConfigurationProperties( "sconsole" )
public class SConsoleConfig {

    private String appName = null ;

    public String getAppName() {
        return appName;
    }

    public void setAppName( String appName ) {
        this.appName = appName;
    }
}
