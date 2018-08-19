package com.sandy.sconsole.dao.repository.master;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.master.Book ;

public interface BookRepository extends CrudRepository<Book, Integer> {

    Book findByBookShortName( String shortName ) ;
}
