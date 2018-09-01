package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import java.util.Map ;

import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;

public class ChangeState extends State {

    protected ChangeState( SessionControlTile parentTile ) {
        super( "ChangeSession", parentTile ) ;
    }

    @Override
    protected void populateTransitionMap( Map<String, State> map ) {
    }
}
