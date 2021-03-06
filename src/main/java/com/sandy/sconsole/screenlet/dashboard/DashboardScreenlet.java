package com.sandy.sconsole.screenlet.dashboard;

import javax.swing.* ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.remote.KeyEvent ;
import com.sandy.sconsole.api.remote.RemoteController ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.remote.* ;
import com.sandy.sconsole.core.screenlet.* ;

public class DashboardScreenlet extends AbstractScreenlet {

    private DashboardLargePanel largePanel = null ;
    private DemuxKeyProcessor keyProcessor = null ;
    
    private RemoteController controller = null ;
    
    public DashboardScreenlet() {
        super( "DayTime" ) ;
        controller = SConsole.getAppContext().getBean( RemoteController.class ) ;
        keyProcessor = new DemuxKeyProcessor( "Dashboard", new KeyListener() {
            @Override
            public void handleFnAKey() {
                switchToFragmentationScreenlet() ;
            }
        }) ;
        keyProcessor.disableAllKeys() ;
        keyProcessor.enableKey( Key.FN_A, "Frag" ) ;
    }
    
    @Override
    protected ScreenletSmallPanel createSmallPanel() {
        
        JLabel label = new JLabel( "Dashboard" ) ;
        label.setFont( UIConstant.BASE_FONT.deriveFont( 20F ) ) ;
        
        ScreenletSmallPanel panel = new ScreenletSmallPanel( this ) ;
        panel.add( label ) ;
        return panel ;
    }

    @Override
    protected ScreenletLargePanel createLargePanel() {
        
        largePanel = new DashboardLargePanel( this ) ;
        return largePanel ;
    }
    
    @Override
    public void isBeingMaximized() {
        super.isBeingMaximized() ;
        controller.pushKeyProcessor( keyProcessor ) ;
    }

    @Override
    public void isBeingMinimized() {
        super.isBeingMinimized() ;
        controller.popKeyProcessor() ;
    }
    
    private void switchToFragmentationScreenlet() {
        KeyEvent event = new KeyEvent() ;
        event.setBtnType( "ScreenletSelection" ) ; 
        event.setBtnCode( "5" ) ;
        controller.buttonPressed( event ) ;
    }
}
