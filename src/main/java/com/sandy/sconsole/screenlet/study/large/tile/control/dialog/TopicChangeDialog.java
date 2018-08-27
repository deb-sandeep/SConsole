package com.sandy.sconsole.screenlet.study.large.tile.control.dialog;

import static com.sandy.sconsole.core.remote.RemoteKeyCode.* ;
import static javax.swing.ScrollPaneConstants.* ;
import static javax.swing.SwingConstants.* ;

import java.awt.* ;
import java.util.List ;

import javax.swing.* ;
import javax.swing.border.* ;

import org.apache.log4j.* ;

import com.sandy.sconsole.* ;
import com.sandy.sconsole.core.frame.* ;
import com.sandy.sconsole.core.remote.* ;
import com.sandy.sconsole.dao.entity.master.* ;
import com.sandy.sconsole.dao.repository.master.* ;
import com.sandy.sconsole.screenlet.study.large.tile.control.* ;

@SuppressWarnings( "serial" )
public class TopicChangeDialog extends AbstractDialogPanel {
    
    static final Logger log = Logger.getLogger( TopicChangeDialog.class ) ;

    private Icon selectIcon = null ;
    private Icon cancelIcon = null ;
    
    private String subjectName = null ;
    private JList<Topic> topicList = null ;
    private DefaultListModel<Topic> listModel = new DefaultListModel<>() ;
    
    private TopicRepository topicRepo = null ;
    
    private SessionControlTile controlTile = null ;
    private Topic selectedTopic = null ;
    
    public TopicChangeDialog( SessionControlTile controlTile ) {
        super( "Choose topic" ) ;

        this.controlTile = controlTile ;
        this.subjectName = controlTile.getScreenlet().getDisplayName() ;
        
        topicRepo = SConsole.getAppContext().getBean( TopicRepository.class ) ;
        
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
        add( getTopicList(), BorderLayout.CENTER ) ;
        add( getButtonPanel(), BorderLayout.SOUTH ) ;
    }
    
    private JPanel getTopicList() {
        
        topicList = new JList<>( listModel ) ;
        topicList.setBackground( UIConstant.BG_COLOR ) ;
        topicList.setCellRenderer( new TopicChangeListCellRenderer() ) ;
        topicList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION ) ;
        
        JScrollPane sp = new JScrollPane( topicList, VERTICAL_SCROLLBAR_ALWAYS, 
                                                     HORIZONTAL_SCROLLBAR_NEVER ) ;
        sp.setBackground( UIConstant.BG_COLOR ) ;
        
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
        int nextSelIndex = topicList.getSelectedIndex()-1 ;
        if( nextSelIndex >= 0 ) {
            topicList.setSelectedIndex( nextSelIndex ) ;
            topicList.ensureIndexIsVisible( nextSelIndex ) ;
        }
    }
    
    @Override public void handleDownNavKey() {
        int nextSelIndex = topicList.getSelectedIndex()+1 ;
        if( nextSelIndex <= listModel.getSize()-1 ) {
            topicList.setSelectedIndex( nextSelIndex ) ;
            topicList.ensureIndexIsVisible( nextSelIndex ) ;
        }
    }

    @Override
    public void handleSelectNavKey() {
        selectedTopic = topicList.getSelectedValue() ;
        super.hideDialog() ;
    }
    
    private void cancelPressed() {
        selectedTopic = null ;
        super.hideDialog() ;
    }

    @Override
    public void isBeingMadeVisible() {
        // Populate the list data and prioritize them for usability
        // highlight the current topic
        listModel.clear() ;
        List<Topic> topics = topicRepo.findAllBySubjectName( subjectName ) ;

        for( Topic topic : topics ) {
            listModel.addElement( topic ) ;
        }
        
        if( !listModel.isEmpty() ) {
            Topic defaultTopic = controlTile.getChangeSelectionTopic() ;
            if( defaultTopic != null ) {
                topicList.setSelectedIndex( listModel.indexOf( defaultTopic ) ) ;
            }
            else {
                topicList.setSelectedIndex( 0 ) ;
            }
            topicList.ensureIndexIsVisible( topicList.getSelectedIndex() ) ;
        }
    } ;
    
    @Override
    public Object getReturnValue() {
        return selectedTopic ;
    }
}
