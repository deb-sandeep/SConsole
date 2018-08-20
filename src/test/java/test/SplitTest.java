package test;

public class SplitTest {

    public static void main( String[] args ) {
        String[] parts = "2,3,4,5,,,6".split( "," ) ;
        for( String part : parts ) {
            System.out.println( ">" + part ) ;
        }
    }
}
