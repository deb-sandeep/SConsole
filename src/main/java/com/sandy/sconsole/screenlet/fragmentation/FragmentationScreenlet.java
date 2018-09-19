package com.sandy.sconsole.screenlet.fragmentation;

import javax.swing.JLabel ;

import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.remote.RemoteController ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.remote.DemuxKeyProcessor ;
import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.remote.KeyListener ;
import com.sandy.sconsole.core.screenlet.AbstractScreenlet ;
import com.sandy.sconsole.core.screenlet.ScreenletLargePanel ;
import com.sandy.sconsole.core.screenlet.ScreenletSmallPanel ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;

public class FragmentationScreenlet extends AbstractScreenlet
    implements KeyListener {

    private FragmentationLargePanel largePanel = null ;
    private DemuxKeyProcessor keyProcessor = null ;
    
    private RemoteController controller = null ;
    
    public FragmentationScreenlet() {
        super( "Fragmentation" ) ;
        keyProcessor = new DemuxKeyProcessor( "Fragmentation", this ) ;
        keyProcessor.disableAllKeys() ;
        keyProcessor.enableKey( Key.FN_A, "Phy" ) ;
        keyProcessor.enableKey( Key.FN_B, "Chem" ) ;
        keyProcessor.enableKey( Key.FN_C, "Math" ) ;
        keyProcessor.enableKey( Key.FN_D, "All" ) ;
        
        controller = SConsole.getAppContext().getBean( RemoteController.class ) ;
    }
    
    @Override
    protected ScreenletSmallPanel createSmallPanel() {
        
        JLabel label = new JLabel( "Fragmentation" ) ;
        label.setFont( UIConstant.BASE_FONT.deriveFont( 20F ) ) ;
        
        ScreenletSmallPanel panel = new ScreenletSmallPanel( this ) ;
        panel.add( label ) ;
        return panel ;
    }

    @Override
    protected ScreenletLargePanel createLargePanel() {
        largePanel = new FragmentationLargePanel( this ) ;
        return largePanel ;
    }

    @Override
    public void handleFnAKey() {
        largePanel.highlightSubject( StudyScreenlet.IIT_PHYSICS ) ;
    }

    @Override
    public void handleFnBKey() {
        largePanel.highlightSubject( StudyScreenlet.IIT_CHEM ) ;
    }

    @Override
    public void handleFnCKey() {
        largePanel.highlightSubject( StudyScreenlet.IIT_MATHS ) ;
    }

    @Override
    public void handleFnDKey() {
        largePanel.highlightSubject( null ) ;
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
}
