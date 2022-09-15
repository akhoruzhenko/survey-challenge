package com.marketlogic.surveychallenge.rest;

import com.marketlogic.surveychallenge.rest.dto.SurveyRequest;
import com.marketlogic.surveychallenge.rest.dto.SurveyResponse;
import com.marketlogic.surveychallenge.service.ISurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class SurveyController {
    private final ISurveyService surveyService;

    /***
     * Return list of all active surveys
     * @return List<SurveyResponse>
     */
    @GetMapping(value = "/surveys")
    public ResponseEntity<List<SurveyResponse>> getSurveys() {
        return ResponseEntity.ok(surveyService.getSurveys());
    }

    /***
     * Return a single survey even though it removed before
     * @param surveyId - survey Id
     * @return SurveyResponse
     */
    @GetMapping(value = "/surveys/{surveyId}")
    public ResponseEntity<SurveyResponse> getSurvey(@PathVariable UUID surveyId) {
        return ResponseEntity.of(surveyService.getSurvey(surveyId));
    }

    /***
     * Create new survey
     * @param survey - survey definition
     * @return SurveyResponse
     */
    @PostMapping(value = "/surveys")
    public ResponseEntity<SurveyResponse> newSurvey(@RequestBody SurveyRequest survey) {
        return ResponseEntity.of(surveyService.newSurvey(survey));
    }

    /***
     * Mark the question as deleted
     * @param surveyId - survey Id
     * @param questionId - question Id
     * @return SurveyResponse
     */
    @DeleteMapping(value = "/surveys/{surveyId}/questions/{questionId}")
    public ResponseEntity<SurveyResponse> deleteSurveyQuestion(@PathVariable UUID surveyId,
                                                               @PathVariable UUID questionId) {
        return ResponseEntity.of(surveyService.deleteSurveyQuestion(surveyId, questionId));
    }
}