package com.sandy.sconsole.dao.repository;

import java.util.List ;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.ClickStreamEvent ;

public interface ClickStreamEventRepository 
    extends CrudRepository<ClickStreamEvent, Integer> {

    List<ClickStreamEvent> findAllByTestAttemptIdOrderById( Integer testAttemptId ) ;
}
