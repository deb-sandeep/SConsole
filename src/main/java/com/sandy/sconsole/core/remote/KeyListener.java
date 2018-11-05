package com.sandy.sconsole.core.remote;

public interface KeyListener {

    default public void handleFastFwdBackKey() {} ;
    default public void handleFastFwdFrontKey() {} ;
    default public void handleLeftNavKey() {} ;
    default public void handleRightNavKey() {} ;
    default public void handleUpNavKey() {} ;
    default public void handleDownNavKey() {} ;
    default public void handleSelectNavKey() {} ;
    default public void handleCancelNavKey() {} ;
    
    default public void handlePlayPauseResumeKey() {} ;
    default public void handleStopKey() {} ;
    
    default public void handleFnAKey() {} ;
    default public void handleFnBKey() {} ;
    default public void handleFnCKey() {} ;
    default public void handleFnDKey() {} ;
    default public void handleFnEKey() {} ;
    default public void handleFnFKey() {} ;
    default public void handleFnGKey() {} ;
    default public void handleFnHKey() {} ;  
}
