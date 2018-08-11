package com.sandy.sconsole.dao.entity.master;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.JoinColumn ;
import javax.persistence.ManyToOne ;
import javax.persistence.Table ;

@Entity
@Table( name = "book_master" )
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    @ManyToOne
    @JoinColumn( name="subject_name" )
    private Subject subject ;
    
    private String bookName ;
    private String authorNames ;
    
    public Integer getId() { return id ; }
    public void setId( Integer id ) { this.id = id ; }
    
    public Subject getSubject() { return subject ; }
    public void setSubject( Subject subject ) { this.subject = subject ; }
    
    public String getBookName() { return bookName ; }
    public void setBookName( String bookName ) { this.bookName = bookName ; }
    
    public String getAuthorNames() { return authorNames ; }
    public void setAuthorNames( String authorNames ) { 
        this.authorNames = authorNames ; 
    }
}
