package com.sandy.sconsole.dao.repository.master;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.master.Subject ;

public interface SubjectRepository extends CrudRepository<Subject, String> {
}
