package com.sandy.sconsole.dao.entity.master;

import javax.persistence.* ;

@Entity
@Table( name = "subject_master" )
public class Subject {

    @Id
    @Column( name = "subject_name" )
    private String name ;

    public String getName() { return name ; }
    public void setName( String name ) { this.name = name ; } 
}
