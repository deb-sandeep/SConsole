package com.sandy.sconsole.dao.repository;

import org.springframework.data.repository.* ;

import com.sandy.sconsole.dao.entity.* ;

public interface SessionRepository extends CrudRepository<Session, Integer> {
}
