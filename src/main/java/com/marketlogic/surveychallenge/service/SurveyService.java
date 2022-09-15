package com.marketlogic.surveychallenge.service;

import com.marketlogic.surveychallenge.exceptions.SurveyRequestValidationException;
import com.marketlogic.surveychallenge.repository.AnswerEntity;
import com.marketlogic.surveychallenge.repository.ISurveyRepository;
import com.marketlogic.surveychallenge.repository.QuestionEntity;
import com.marketlogic.surveychallenge.repository.SurveyEntity;
import com.marketlogic.surveychallenge.rest.dto.AnswerRequest;
import com.marketlogic.surveychallenge.rest.dto.AnswerResponse;
import com.marketlogic.surveychallenge.rest.dto.QuestionRequest;
import com.marketlogic.surveychallenge.rest.dto.QuestionResponse;
import com.marketlogic.surveychallenge.rest.dto.SurveyRequest;
import com.marketlogic.surveychallenge.rest.dto.SurveyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService implements ISurveyService {
    private final ISurveyRepository repo;

    @Override
    public List<SurveyResponse> getSurveys() {
        return repo.findByDeletedIsFalse().stream()
                .map(this::buildSurveyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SurveyResponse> getSurvey(UUID surveyId) {
        return repo.findById(surveyId).map(this::buildSurveyResponse);
    }

    @Override
    public Optional<SurveyResponse> newSurvey(SurveyRequest survey) {
        SurveyEntity entity = createSurveyEntity(survey);
        return Optional.of(buildSurveyResponse(repo.save(entity)));
    }

    @Override
    public Optional<SurveyResponse> deleteSurveyQuestion(UUID surveyId, UUID questionId) {
        Optional<SurveyEntity> survey = repo.findById(surveyId);
        if (survey.isEmpty())
            return Optional.empty();

        Optional<QuestionEntity> question = survey.get().getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst();
        if (question.isEmpty())
            return Optional.empty();

        question.get().setDeleted(true);
        repo.save(survey.get());

        return getSurvey(surveyId);
    }

    protected SurveyResponse buildSurveyResponse(SurveyEntity entity) {
        return SurveyResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .questions(entity.getQuestions().stream()
                        .filter(e -> !e.getDeleted())
                        .map(this::buildQuestionResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private QuestionResponse buildQuestionResponse(QuestionEntity entity) {
        return QuestionResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .answers(entity.getAnswers().stream()
                        .filter(e -> !e.getDeleted())
                        .map(this::buildAnswerResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private AnswerResponse buildAnswerResponse(AnswerEntity entity) {
        return AnswerResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    private SurveyEntity createSurveyEntity(SurveyRequest survey) {
        if (survey == null)
            throw new SurveyRequestValidationException("Survey is not defined");

        if (survey.getName() == null || survey.getName().isEmpty())
            throw new SurveyRequestValidationException("Survey name is not defined");

        SurveyEntity entity = SurveyEntity.builder()
                .deleted(false)
                .build();
        entity.setName(survey.getName());
        entity.setDescription(survey.getDescription());
        entity.setQuestions(survey.getQuestions().stream()
                .map(this::createQuestionEntity)
                .collect(Collectors.toList()));
        return entity;
    }

    private QuestionEntity createQuestionEntity(QuestionRequest question) {
        if (question.getName() == null || question.getName().isEmpty())
            throw new SurveyRequestValidationException("Question name is not defined");

        QuestionEntity entity = QuestionEntity.builder()
                .deleted(false)
                .build();
        entity.setName(question.getName());
        entity.setDescription(question.getDescription());
        entity.setAnswers(question.getAnswers().stream()
                .map(this::createAnswerEntity)
                .collect(Collectors.toList()));
        return entity;
    }

    private AnswerEntity createAnswerEntity(AnswerRequest answer) {
        if (answer.getName() == null || answer.getName().isEmpty())
            throw new SurveyRequestValidationException("Answer name is not defined");

        return AnswerEntity.builder()
                .name(answer.getName())
                .description(answer.getDescription())
                .deleted(false)
                .build();
    }
}
