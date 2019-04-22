package com.sandy.sconsole.dao.repository;

import org.springframework.data.repository.CrudRepository ;

import com.sandy.sconsole.dao.entity.ProblemAttemptAnalysis ;

public interface ProblemAttemptAnalysisRepository 
    extends CrudRepository<ProblemAttemptAnalysis, Integer> {

}
