package com.sandy.sconsole.dao.repository;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.ClickStreamEvent ;

public interface ClickStreamEventRepository 
    extends CrudRepository<ClickStreamEvent, Integer> {
}
