package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;

public class PlayState extends BaseControlTileState {

    public static final String NAME = "Play" ;
    
    public PlayState( SessionControlTile tile, StudyScreenletLargePanel screenletPanel ) {
        super( NAME, tile, screenletPanel ) ;
    }
}
