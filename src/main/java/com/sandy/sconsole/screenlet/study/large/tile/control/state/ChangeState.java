package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import com.sandy.sconsole.core.remote.Key ;
import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.screenlet.study.large.StudyScreenletLargePanel ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;

public class ChangeState extends BaseControlTileState {

    public ChangeState( SessionControlTile tile, StudyScreenletLargePanel screenletPanel ) {
        super( "Change", tile, screenletPanel ) ;
    }
    
    @Override
    public void resetState() {
    }

    @Override
    public boolean preActivate( Object payload, State fromState, Key key ) {
        return false ;
    }
}
