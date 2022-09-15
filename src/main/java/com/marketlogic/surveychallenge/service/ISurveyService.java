package com.marketlogic.surveychallenge.service;

import com.marketlogic.surveychallenge.rest.dto.SurveyRequest;
import com.marketlogic.surveychallenge.rest.dto.SurveyResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISurveyService {
    List<SurveyResponse> getSurveys();
    Optional<SurveyResponse> getSurvey(UUID surveyId);
    Optional<SurveyResponse> newSurvey(SurveyRequest survey);
    Optional<SurveyResponse> deleteSurveyQuestion(UUID surveyId, UUID questionId);
}
