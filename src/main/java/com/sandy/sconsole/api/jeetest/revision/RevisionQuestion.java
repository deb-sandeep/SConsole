package com.sandy.sconsole.api.jeetest.revision;

import java.sql.Timestamp ;

public interface RevisionQuestion {

    public Integer getQuestionId() ;
    public String getSubjectName() ;
    public String getTopicName() ;
    public String getBookShortName() ;
    public String getExerciseName() ;
    public String getProblemType() ;
    public String getProblemTag() ;
    public String getOutcome() ;
    public Integer getDuration() ;
    public Timestamp getStartTime() ;
    public Integer getStarred() ;
    public Integer getRevisionCount() ;
    public String getChapterId() ;
}
