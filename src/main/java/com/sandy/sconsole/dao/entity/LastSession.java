package com.sandy.sconsole.dao.entity;

import javax.persistence.* ;

@Entity
@Table( name = "last_session" )
public class LastSession {

    @Id
    private String subjectName ;
    
    @ManyToOne
    @JoinColumn( name="session_id" )
    private Session session ;

    public String getSubject() { return subjectName ; }
    public void setSubjectName( String subjectName ) {
        this.subjectName = subjectName ;
    }

    public Session getSession() { return session ; } 
    public void setSession( Session session ) {
        this.session = session ;
    }
}
