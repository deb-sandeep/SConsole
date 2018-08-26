package com.sandy.sconsole.screenlet.dummy;

import static com.sandy.sconsole.core.frame.UIConstant.* ;
import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;
import static javax.swing.SwingConstants.* ;

import java.awt.* ;

import javax.swing.* ;

import org.apache.log4j.* ;

import com.sandy.sconsole.core.frame.* ;
import com.sandy.sconsole.core.remote.* ;

@SuppressWarnings( "serial" )
public class DummyDialog extends AbstractDialogPanel {
    
    static final Logger log = Logger.getLogger( DummyDialog.class ) ;

    public DummyDialog() {
        super( "Dummy" ) ;
        
        setUpUI() ;
        keyProcessor.disableAllKeys() ;
        keyProcessor.setKeyEnabled( true, FN_A, FN_B ) ;
        keyProcessor.setFnHandler( FN_A, new Handler() {
            public void handle() { System.exit( -1 ) ; }
        } ) ;
        keyProcessor.setFnHandler( FN_B, new Handler() {
            public void handle() { hideDialog() ; }
        } ) ;
    }
    
    private void setUpUI() {
        setPreferredSize( new Dimension( 400, 400 ) ) ;
        setLayout( new GridLayout( 2, 1 ) ) ;
        add( getFnButton( FN_A_COLOR, "Shutdown" ) ) ;
        add( getFnButton( FN_B_COLOR, "Hide" ) ) ;
    }

    @Override
    public Object getReturnValue() {
        return "Success" ;
    }

    protected JPanel getFnButton( Color color, String text ) {
        
        JLabel label = new JLabel( text ) ;
        label.setFont( FNBTN_FONT ) ;
        label.setForeground( Color.WHITE ) ;
        label.setHorizontalAlignment( CENTER ) ;
        label.setVerticalAlignment( CENTER ) ;
        
        JPanel panel = new JPanel( new BorderLayout() ) ;
        panel.setBackground( color ) ;
        panel.add( label ) ;
        panel.setBorder( BorderFactory.createLineBorder( BG_COLOR, 10 ) ) ;
        
        return panel ;
    }
}
