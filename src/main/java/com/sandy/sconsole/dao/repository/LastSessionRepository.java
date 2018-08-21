package com.sandy.sconsole.dao.repository;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.LastSession ;

public interface LastSessionRepository extends CrudRepository<LastSession, String> {
}
