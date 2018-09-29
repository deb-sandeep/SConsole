package com.sandy.sconsole.api.mdm.topic;

import java.text.SimpleDateFormat ;

import org.apache.log4j.Logger ;
import org.springframework.beans.factory.annotation.Autowired ;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RestController ;

import com.sandy.sconsole.EventCatalog ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.api.mdm.topic.TopicChangeInfo.ChangeInfo ;
import com.sandy.sconsole.core.api.APIResponse ;
import com.sandy.sconsole.dao.entity.master.Topic ;
import com.sandy.sconsole.dao.repository.master.TopicRepository ;
import com.sandy.sconsole.screenlet.study.TopicBurnInfo ;

@RestController
public class TopicController {
    
    private static final Logger log = Logger.getLogger( TopicController.class ) ;
    private static final SimpleDateFormat SDF = new SimpleDateFormat( "MMM / dd / yyyy" ) ;
    
    @Autowired
    private TopicRepository topicRepo = null ;

    @PostMapping( "/ChangedTopics" )
    public ResponseEntity<APIResponse> updateTopic( @RequestBody TopicChangeInfo changeInfo ) {
        
        log.debug( "Received API request for Topic updates" ) ;

        
        for( ChangeInfo ci : changeInfo.getChangedTopics() ) {
            Topic topic = topicRepo.findById( ci.getTopicId() ).get() ;
            topic.setBurnStart( ci.getStartDay() ) ; 
            topic.setBurnCompletion( ci.getEndDay() ) ;
            topic.setActive( ci.getActive() ) ;
            
            log.debug( "Topic milestone information being updated for " + 
                       topic.getId() + " - " + topic.getTopicName() ) ;
            log.debug( "\tStart date = " + SDF.format( topic.getBurnStart() ) ) ;
            log.debug( "\tEnd date   = " + SDF.format( topic.getBurnCompletion() ) ) ;
            log.debug( "\tActive     = " + topic.getActive() ) ;

            topicRepo.save( topic ) ;
            
            try {
                TopicBurnInfo bi = new TopicBurnInfo( topic ) ;
                SConsole.GLOBAL_EVENT_BUS
                        .publishEvent( EventCatalog.TOPIC_BURN_CALIBRATED, bi ) ;
            }
            catch( Exception e ) {
                log.error( "Error creating burn info", e ) ;
                return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR )
                                     .body( new APIResponse( e.getMessage() ) ) ;
            }
        }
        
        if( !changeInfo.getChangedTopics().isEmpty() ) {
            SConsole.GLOBAL_EVENT_BUS
                    .publishEvent( EventCatalog.TOPIC_MILESTONES_CHANGED, null ) ;
        }
        
        return ResponseEntity.ok().body( new APIResponse( "OK" ) ) ;
    }
}
