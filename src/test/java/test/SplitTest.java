package test;

import java.net.InetAddress ;

import org.apache.log4j.Logger ;

public class SplitTest {
    
    private static final Logger log = Logger.getLogger( SplitTest.class ) ;

    public static void main( String[] args ) throws Exception {
        isOperatingOnPiMon() ;
    }

    public static boolean isOperatingOnPiMon() throws Exception {
        InetAddress inetAddress = InetAddress.getLocalHost();
        String ipAddress = inetAddress.getHostAddress() ;
        log.debug( "isOperatingOnPiMon = " + ipAddress ) ;
        log.debug( "\tresult = " + ipAddress.equals( "192.168.0.117" ) ) ;
        return ipAddress.equals( "192.168.0.117" ) ;
    }
}
