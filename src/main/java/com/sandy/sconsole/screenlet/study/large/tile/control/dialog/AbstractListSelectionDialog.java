package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import static com.sandy.sconsole.core.remote.RemoteKeyCode.FN_CANCEL ;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER ;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER ;
import static javax.swing.SwingConstants.CENTER ;

import java.awt.* ;
import java.util.List ;

import javax.swing.* ;
import javax.swing.border.EmptyBorder ;
import javax.swing.border.LineBorder ;

import org.apache.log4j.Logger ;

import com.sandy.sconsole.core.frame.AbstractDialogPanel ;
import com.sandy.sconsole.core.frame.UIConstant ;
import com.sandy.sconsole.core.remote.Handler ;

@SuppressWarnings( "serial" )
public abstract class AbstractListSelectionDialog<T> extends AbstractDialogPanel {

    static final Logger log = Logger.getLogger( AbstractListSelectionDialog.class ) ;

    private Icon selectIcon = null ;
    private Icon cancelIcon = null ;
    
    private JList<T> entityList = null ;
    private DefaultListModel<T> listModel = null ;
    private ListCellRenderer<T> renderer = null ;
    
    private T selectedEntity = null ;

    protected AbstractListSelectionDialog( String title,
                                           ListCellRenderer<T> renderer ) {
        super( title ) ;
        this.renderer  = renderer ;
        
        loadIcons() ;
        setUpUI() ;
        
        keyProcessor.disableAllKeys() ;
        keyProcessor.enableNavKeys( true ) ;
        keyProcessor.setKeyEnabled( true, FN_CANCEL ) ;
        keyProcessor.setFnHandler( FN_CANCEL, new Handler() {
            public void handle() { cancelPressed() ; }
        } ) ;
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
        
        JScrollPane sp = new JScrollPane( entityList, VERTICAL_SCROLLBAR_NEVER, 
                                                      HORIZONTAL_SCROLLBAR_NEVER ) ;
        sp.setBackground( UIConstant.BG_COLOR ) ;
        sp.getVerticalScrollBar().setBackground( UIConstant.BG_COLOR ) ;
        
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
    
    @Override 
    public void handleUpNavKey() {
        int nextSelIndex = entityList.getSelectedIndex()-1 ;
        if( nextSelIndex >= 0 ) {
            entityList.setSelectedIndex( nextSelIndex ) ;
            entityList.ensureIndexIsVisible( nextSelIndex ) ;
        }
    }
    
    @Override public void handleDownNavKey() {
        int nextSelIndex = entityList.getSelectedIndex()+1 ;
        if( nextSelIndex <= listModel.getSize()-1 ) {
            entityList.setSelectedIndex( nextSelIndex ) ;
            entityList.ensureIndexIsVisible( nextSelIndex ) ;
        }
    }

    @Override
    public void handleSelectNavKey() {
        selectedEntity = entityList.getSelectedValue() ;
        super.hideDialog() ;
    }
    
    private void cancelPressed() {
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
