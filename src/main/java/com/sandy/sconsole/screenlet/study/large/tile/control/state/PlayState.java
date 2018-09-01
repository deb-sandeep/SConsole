package com.sandy.sconsole.screenlet.study.large.tile.control.state;

import java.util.Map ;

import com.sandy.sconsole.core.statemc.State ;
import com.sandy.sconsole.screenlet.study.large.tile.control.SessionControlTile ;

public class PlayState extends State {

    protected PlayState( SessionControlTile parentTile ) {
        super( "PlayState", parentTile ) ;
    }

    @Override
    protected void populateTransitionMap( Map<String, State> map ) {
    }
}
