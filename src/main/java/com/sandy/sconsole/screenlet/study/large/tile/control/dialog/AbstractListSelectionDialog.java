package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER ;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED ;
import static javax.swing.SwingConstants.CENTER ;

import java.awt.* ;
import java.util.List ;

import javax.swing.* ;
import javax.swing.border.EmptyBorder ;
import javax.swing.border.LineBorder ;
import javax.swing.plaf.basic.BasicScrollBarUI ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.frame.AbstractDialogPanel ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.remote.Key ;

@SuppressWarnings( "serial" )
public abstract class AbstractListSelectionDialog<T> extends AbstractDialogPanel {

    static final Logger log = Logger.getLogger( AbstractListSelectionDialog.class ) ;

    private Icon selectIcon = null ;
    private Icon cancelIcon = null ;
    
    protected JList<T> entityList = null ;
    protected DefaultListModel<T> listModel = null ;
    private ListCellRenderer<T> renderer = null ;
    
    private T selectedEntity = null ;

    protected AbstractListSelectionDialog( String title,
                                           ListCellRenderer<T> renderer ) {
        super( title ) ;
        this.renderer  = renderer ;
        
        loadIcons() ;
        setUpUI() ;
        
        keyProcessor.disableAllKeys() ;
        keyProcessor.enableNavKeys() ;
        keyProcessor.disableKey( Key.FF_B ) ;
        keyProcessor.disableKey( Key.FF_F ) ;
    }

    private void loadIcons() {
        selectIcon = new ImageIcon( getClass().getResource( "/icons/select.png" ) ) ;
        cancelIcon = new ImageIcon( getClass().getResource( "/icons/cancel.png"   ) ) ;
    }
    
    private void setUpUI() {
        setPreferredSize( new Dimension( 800, 600 ) ) ;
        setLayout( new BorderLayout() ) ;
        setBackground( Color.GRAY ) ;
        setBorder( new LineBorder( Color.GRAY, 20 ) ) ;
        add( getEntityList(), BorderLayout.CENTER ) ;
        add( getButtonPanel(), BorderLayout.SOUTH ) ;
    }
    
    private JPanel getEntityList() {
        
        listModel = new DefaultListModel<>() ;
        
        entityList = new JList<>( listModel ) ;
        entityList.setBackground( UIConstant.BG_COLOR ) ;
        entityList.setCellRenderer( renderer ) ;
        entityList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION ) ;
        
        JScrollPane sp = new JScrollPane( entityList, VERTICAL_SCROLLBAR_AS_NEEDED, 
                                                      HORIZONTAL_SCROLLBAR_NEVER ) ;
        sp.setBackground( UIConstant.BG_COLOR ) ;
        sp.getVerticalScrollBar().setBackground( UIConstant.BG_COLOR ) ;
        sp.getVerticalScrollBar().setUI( new BasicScrollBarUI(){
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.GRAY ;
                this.thumbDarkShadowColor = Color.DARK_GRAY ;
                this.thumbHighlightColor = Color.LIGHT_GRAY.brighter() ;
                this.thumbLightShadowColor = Color.LIGHT_GRAY ;
                this.trackColor = UIConstant.BG_COLOR ;
            }

            @Override
            protected JButton createDecreaseButton( int orientation ) {
                JButton btn = super.createDecreaseButton( orientation ) ;
                btn.setBackground( Color.DARK_GRAY ) ;
                return btn ;
            }

            @Override
            protected JButton createIncreaseButton( int orientation ) {
                JButton btn = super.createIncreaseButton( orientation ) ;
                btn.setBackground( Color.DARK_GRAY ) ;
                return btn ;
            }
        });
        
        JPanel panel = new JPanel() ;
        panel.setBackground( UIConstant.BG_COLOR ) ;
        panel.setLayout( new BorderLayout() ) ;
        panel.add( sp, BorderLayout.CENTER ) ;
        
        return panel ;
    }
    
    private JPanel getButtonPanel() {
        JPanel panel = new JPanel( new GridLayout( 1, 2 ) ) ;
        panel.add( getButton( selectIcon ) ) ;
        panel.add( getButton( cancelIcon ) ) ;
        return panel ;
    }
    
    private JPanel getButton( Icon icon ) {
        
        JLabel label = new JLabel() ;
        label.setHorizontalAlignment( CENTER ) ;
        label.setVerticalAlignment( CENTER ) ;
        label.setIcon( icon ) ;
        
        JPanel panel = new JPanel( new BorderLayout() ) ;
        panel.setBackground( Color.GRAY ) ;
        panel.setLayout( new BorderLayout() ) ;
        panel.add( label ) ;
        panel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) ) ;
        
        return panel ;
    }
    
    protected void selectIndex( int index ) {
        
        int finalIndex = index ;
        
        if( finalIndex < -1 ) {
            finalIndex = 0 ;
        }
        else if( finalIndex > listModel.size() ) {
            finalIndex = listModel.size()-1 ;
        }
        
        entityList.setSelectedIndex( finalIndex ) ;
        entityList.ensureIndexIsVisible( finalIndex ) ;
        entityList.repaint() ;
    }
    
    @Override 
    public void handleUpNavKey() {
        selectIndex( entityList.getSelectedIndex()-1 ) ;
    }
    
    @Override public void handleDownNavKey() {
        selectIndex( entityList.getSelectedIndex()+1 ) ;
    }
    
    @Override 
    public void handleLeftNavKey() {
        selectIndex( entityList.getSelectedIndex()-10 ) ;
    }
    
    @Override public void handleRightNavKey() {
        selectIndex( entityList.getSelectedIndex()+10 ) ;
    }

    @Override
    public void handleSelectNavKey() {
        selectedEntity = entityList.getSelectedValue() ;
        super.hideDialog() ;
    }
    
    @Override
    public void handleCancelNavKey() {
        selectedEntity = null ;
        super.hideDialog() ;
    }

    @Override
    public void isBeingMadeVisible() {
        // Populate the list data and prioritize them for usability
        // highlight the current topic
        listModel.clear() ;
        List<T> entities = getListItems() ;

        for( T t : entities ) {
            listModel.addElement( t ) ;
        }
        
        if( !listModel.isEmpty() ) {
            T defaultSelectedEntity = getDefaultSelectedEntity() ;
            if( defaultSelectedEntity != null ) {
                entityList.setSelectedIndex( listModel.indexOf( defaultSelectedEntity ) ) ;
            }
            else {
                entityList.setSelectedIndex( 0 ) ;
            }
            entityList.ensureIndexIsVisible( entityList.getSelectedIndex() ) ;
        }
    } ;
    
    @Override
    public Object getReturnValue() {
        return selectedEntity ;
    }
    
    protected abstract List<T> getListItems() ;
    
    protected abstract T getDefaultSelectedEntity() ;
}
