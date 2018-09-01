package com.sandy.sconsole.dao.entity;

import javax.persistence.AttributeConverter ;

import com.sandy.sconsole.dao.entity.Session.SessionType ;

public class SessionTypeConverter implements AttributeConverter<SessionType, String> {

    @Override
    public String convertToDatabaseColumn( SessionType attribute ) {
        return attribute.toString() ;
    }

    @Override
    public SessionType convertToEntityAttribute( String dbData ) {
        if( dbData.equals( SessionType.EXERCISE.toString() ) ) {
            return SessionType.EXERCISE ;
        }
        else if( dbData.equals( SessionType.LECTURE.toString() ) ) {
            return SessionType.LECTURE ;
        }
        else if( dbData.equals( SessionType.THEORY.toString() ) ) {
            return SessionType.THEORY ;
        }
        
        throw new IllegalArgumentException( "Database value " + dbData +  
                                              " is not a valid SessionType" ) ;
    }

}
