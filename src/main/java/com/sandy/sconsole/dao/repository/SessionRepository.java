package com.sandy.sconsole.dao.repository;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.Session ;

public interface SessionRepository extends CrudRepository<Session, Integer> {
}
