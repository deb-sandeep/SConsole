package com.sandy.sconsole.dao.repository.master;

import org.springframework.data.repository.* ;

import com.sandy.sconsole.dao.entity.master.* ;

public interface BookRepository extends CrudRepository<Book, Integer> {

    Book findByBookShortName( String shortName ) ;
}
