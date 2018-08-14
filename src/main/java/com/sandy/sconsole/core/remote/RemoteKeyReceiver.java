package com.sandy.sconsole.core.remote;

import com.sandy.sconsole.api.remote.KeyPressEvent;

public interface RemoteKeyReceiver {
	
    public void handleRemoteKeyEvent( KeyPressEvent event ) ;
}
