package com.sandy.sconsole.screenlet.dashboard.tile;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.common.bus.Event ;
import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.core.screenlet.AbstractScreenletTile ;
import com.sandy.sconsole.core.screenlet.ScreenletPanel ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;
import com.sandy.sconsole.screenlet.study.StudyScreenlet ;

import info.clearthought.layout.TableLayout ;

@SuppressWarnings( "serial" )
public class ActiveTopicBurnDashboard extends AbstractScreenletTile {
    
    static final Logger log = Logger.getLogger( ActiveTopicBurnDashboard.class ) ;
    
    private TopicRepository topicRepo = null ;
    
    private List<Topic> phyTopics   = new ArrayList<Topic>() ;
    private List<Topic> chemTopics  = new ArrayList<Topic>() ;
    private List<Topic> mathsTopics = new ArrayList<Topic>() ;
    
    public ActiveTopicBurnDashboard( ScreenletPanel mother ) {
        super( mother, false ) ;
        topicRepo = SConsole.getAppContext().getBean( TopicRepository.class ) ;
        
        SConsole.GLOBAL_EVENT_BUS
                .addSubscriberForEventTypes( this, true, 
                                             EventCatalog.TOPIC_MILESTONES_CHANGED ) ; ;
    }

    /*
     * We refresh the active topic burn summary only when the dashboard
     * screen is getting maximized.
     */
    @Override
    protected void screenletMaximized() {
        super.screenletMaximized() ;
        try {
            refreshTopicSummaries() ;
            revalidate() ;
        }
        catch( Exception e ) {
            log.error( "Error populating topic burn summaries", e ) ;
        }
    }
    
    
    
    @Override
    public void handleEvent( Event event ) {
        super.handleEvent( event ) ;
        
        if( event.getEventType() == EventCatalog.TOPIC_MILESTONES_CHANGED ) {
            try {
                refreshTopicSummaries() ;
                revalidate() ;
                repaint() ;
            }
            catch( Exception e ) {
                log.error( "Error populating topic burn summaries", e ) ;
            }
        }
    }

    private void refreshTopicSummaries() 
        throws Exception {
        
        loadAndSortActiveTopics() ;
        createLayout() ;
        addTopicSummaryPanel( 0, phyTopics ) ;
        addTopicSummaryPanel( 1, chemTopics ) ;
        addTopicSummaryPanel( 2, mathsTopics ) ;
    }
    
    private void loadAndSortActiveTopics() {
        
        phyTopics.clear() ;
        chemTopics.clear() ;
        mathsTopics.clear() ;
        
        List<Topic> activeTopics = topicRepo.findActiveTopics() ;
        for( Topic topic : activeTopics ) {
            String subName = topic.getSubject().getName() ;
            if( subName.equals( StudyScreenlet.IIT_PHYSICS ) ) {
                phyTopics.add( topic ) ;
            }
            else if( subName.equals( StudyScreenlet.IIT_CHEM ) ) {
                chemTopics.add( topic ) ;
            }
            else if( subName.equals( StudyScreenlet.IIT_MATHS ) ) {
                mathsTopics.add( topic ) ;
            }
        }
    }
    
    /**
     * Refreshes the layout of this tile. Note that the number of 
     * active topics might have changed since we last set up the layout
     * and hence, we discard the previous layout and create a fresh
     * one.
     */
    private void createLayout() {
        removeAll() ;
        TableLayout layoutMgr = new TableLayout() ;
        
        int numCols = 3 ;
        int numRows = Math.max( phyTopics.size(), 
                                Math.max( chemTopics.size(), 
                                          mathsTopics.size() ) ) ;
        
        for( int i=0; i<numRows; i++ ) {
            layoutMgr.insertRow( i, 1.0/numRows ) ;
        }
        for( int i=0; i<numCols; i++ ) {
            layoutMgr.insertColumn( i, 1.0/numCols ) ;
        }
        
        setLayout( layoutMgr ) ;
    }

    private void addTopicSummaryPanel( int colNum, List<Topic> topics ) 
        throws Exception {
        
        for( int rowNum=topics.size()-1; rowNum>=0; rowNum-- ) {
            
            Topic topic = topics.get( rowNum ) ;
            String pc = colNum + "," + rowNum + "," + 
                        colNum + "," + rowNum ;
            
            TopicBurnSummaryPanel panel = new TopicBurnSummaryPanel( topic ) ;
            add( panel, pc ) ;
        }
    }
}
