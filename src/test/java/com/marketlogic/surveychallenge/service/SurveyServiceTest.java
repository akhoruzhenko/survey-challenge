package com.marketlogic.surveychallenge.service;

import com.marketlogic.surveychallenge.exceptions.SurveyRequestValidationException;
import com.marketlogic.surveychallenge.rest.dto.AnswerRequest;
import com.marketlogic.surveychallenge.rest.dto.AnswerResponse;
import com.marketlogic.surveychallenge.rest.dto.QuestionRequest;
import com.marketlogic.surveychallenge.rest.dto.QuestionResponse;
import com.marketlogic.surveychallenge.rest.dto.SurveyRequest;
import com.marketlogic.surveychallenge.rest.dto.SurveyResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SurveyServiceTest {

    @Autowired
    private ISurveyService surveyService;

    private void assertSurvey(SurveyRequest in, SurveyResponse out) {
        assertEquals(in.getName(), out.getName());
        assertEquals(in.getDescription(), out.getDescription());
        assertEquals(in.getQuestions().size(), out.getQuestions().size());
        for (QuestionRequest questionIn : in.getQuestions()) {
            Optional<QuestionResponse> questionOut = out.getQuestions().stream()
                    .filter(q -> q.getName().equals(questionIn.getName()))
                    .findFirst();

            assertTrue(questionOut.isPresent());
            assertEquals(questionIn.getName(), questionOut.get().getName());
            assertEquals(questionIn.getDescription(), questionOut.get().getDescription());
            assertEquals(questionIn.getAnswers().size(), questionOut.get().getAnswers().size());

            for (AnswerRequest answerIn : questionIn.getAnswers()) {
                Optional<AnswerResponse> answerOut = questionOut.get().getAnswers().stream()
                        .filter(a -> a.getName().equals(answerIn.getName()))
                        .findFirst();

                assertTrue(answerOut.isPresent());
                assertEquals(answerIn.getName(), answerOut.get().getName());
                assertEquals(answerIn.getDescription(), answerOut.get().getDescription());
            }
        }
    }

    @Test
    void getSurveys() {
        SurveyRequest survey1 = SurveyRequest.builder()
                .name("Test survey survey " + UUID.randomUUID())
                .description("Test survey description " + UUID.randomUUID())
                .build();
        Optional<SurveyResponse> response1 = surveyService.newSurvey(survey1);

        SurveyRequest survey2 = SurveyRequest.builder()
                .name("Test survey " + UUID.randomUUID())
                .description("Test description " + UUID.randomUUID())
                .build();
        Optional<SurveyResponse> response2 = surveyService.newSurvey(survey2);

        List<SurveyResponse> surveys = surveyService.getSurveys();

        assertTrue(response1.isPresent());
        assertTrue(surveys.stream().anyMatch(s -> s.getId().equals(response1.get().getId())));

        assertTrue(response2.isPresent());
        assertTrue(surveys.stream().anyMatch(s -> s.getId().equals(response2.get().getId())));
    }

    @Test
    void getSurveyByIdNoQuestions() {
        SurveyRequest survey = SurveyRequest.builder()
                .name("Test survey name " + UUID.randomUUID())
                .description("Test survey description " + UUID.randomUUID())
                .build();
        Optional<SurveyResponse> response = surveyService.newSurvey(survey);
        assertTrue(response.isPresent());

        Optional<SurveyResponse> newSurvey = surveyService.getSurvey(response.get().getId());

        assertTrue(newSurvey.isPresent());
        assertSurvey(survey, newSurvey.get());
    }

    @Test
    void getSurveyByIdWithQuestions() {
        SurveyRequest survey = SurveyRequest.builder()
                .name("Test survey name " + UUID.randomUUID())
                .description("Test survey description " + UUID.randomUUID())
                .questions(Arrays.asList(
                        QuestionRequest.builder()
                                .name("Test question name " + UUID.randomUUID())
                                .description("Test question description " + UUID.randomUUID())
                                .answers(Arrays.asList(
                                        AnswerRequest.builder().name("Answer 1").build(),
                                        AnswerRequest.builder().name("Answer 2").build(),
                                        AnswerRequest.builder().name("Answer 3").build()
                                ))
                                .build(),
                        QuestionRequest.builder()
                                .name("Test question name " + UUID.randomUUID())
                                .description("Test question description " + UUID.randomUUID())
                                .answers(Arrays.asList(
                                        AnswerRequest.builder().name("Answer 4").build(),
                                        AnswerRequest.builder().name("Answer 5").build(),
                                        AnswerRequest.builder().name("Answer 6").build()
                                ))
                                .build()
                ))
                .build();

        Optional<SurveyResponse> response = surveyService.newSurvey(survey);
        assertTrue(response.isPresent());

        Optional<SurveyResponse> newSurvey = surveyService.getSurvey(response.get().getId());

        assertTrue(newSurvey.isPresent());
        assertSurvey(survey, newSurvey.get());
    }

    @Test
    void newSurveyValid() {
        SurveyRequest survey = SurveyRequest.builder()
                .name("Test survey name " + UUID.randomUUID())
                .description("Test survey description " + UUID.randomUUID())
                .build();
        Optional<SurveyResponse> response = surveyService.newSurvey(survey);

        assertTrue(response.isPresent());
        assertSurvey(survey, response.get());
    }

    @Test
    void newSurveyInvalid_SurveyNameIsNull() {
        SurveyRequest survey = SurveyRequest.builder()
                .description("Test survey description " + UUID.randomUUID())
                .build();

        SurveyRequestValidationException thrown = assertThrows(SurveyRequestValidationException.class, () -> {
            Optional<SurveyResponse> response = surveyService.newSurvey(survey);
            assertTrue(response.isPresent());
        }, "SurveyValidationException exception was expected");
        assertEquals("Survey name is not defined", thrown.getMessage());
    }

    @Test
    void newSurveyInvalid_SurveyNameIsEmpty() {
        SurveyRequest survey = SurveyRequest.builder()
                .name("")
                .description("Test survey description " + UUID.randomUUID())
                .build();

        SurveyRequestValidationException thrown = assertThrows(SurveyRequestValidationException.class, () -> {
            Optional<SurveyResponse> response = surveyService.newSurvey(survey);
            assertTrue(response.isPresent());
        }, "SurveyValidationException exception was expected");
        assertEquals("Survey name is not defined", thrown.getMessage());
    }

    @Test
    void newSurveyInvalid_QuestionNameIsNull() {
        SurveyRequest survey = SurveyRequest.builder()
                .name("Test survey name " + UUID.randomUUID())
                .description("Test survey description " + UUID.randomUUID())
                .questions(Collections.singletonList(
                        QuestionRequest.builder()
                                .description("Test question description " + UUID.randomUUID())
                                .build()))

                .build();

        SurveyRequestValidationException thrown = assertThrows(SurveyRequestValidationException.class, () -> {
            Optional<SurveyResponse> response = surveyService.newSurvey(survey);
            assertTrue(response.isPresent());
        }, "SurveyRequestValidationException exception was expected");
        assertEquals("Question name is not defined", thrown.getMessage());
    }

    @Test
    void newSurveyInvalid_QuestionNameIsEmpty() {
        SurveyRequest survey = SurveyRequest.builder()
                .name("Test survey name " + UUID.randomUUID())
                .description("Test survey description " + UUID.randomUUID())
                .questions(Collections.singletonList(
                        QuestionRequest.builder()
                                .name("")
                                .description("Test question description " + UUID.randomUUID())
                                .build()))

                .build();

        SurveyRequestValidationException thrown = assertThrows(SurveyRequestValidationException.class, () -> {
            Optional<SurveyResponse> response = surveyService.newSurvey(survey);
            assertTrue(response.isPresent());
        }, "SurveyRequestValidationException exception was expected");
        assertEquals("Question name is not defined", thrown.getMessage());
    }

    @Test
    void newSurveyInvalid_AnswerNameIsNull() {
        SurveyRequest survey = SurveyRequest.builder()
                .name("Test survey name " + UUID.randomUUID())
                .description("Test survey description " + UUID.randomUUID())
                .questions(Collections.singletonList(
                        QuestionRequest.builder()
                                .name("Test question name " + UUID.randomUUID())
                                .description("Test question description " + UUID.randomUUID())
                                .answers(Arrays.asList(
                                        AnswerRequest.builder().name("Answer 1").build(),
                                        AnswerRequest.builder().build(),
                                        AnswerRequest.builder().name("Answer 3").build()
                                ))
                                .build()))

                .build();

        SurveyRequestValidationException thrown = assertThrows(SurveyRequestValidationException.class, () -> {
            Optional<SurveyResponse> response = surveyService.newSurvey(survey);
            assertTrue(response.isPresent());
        }, "SurveyRequestValidationException exception was expected");
        assertEquals("Answer name is not defined", thrown.getMessage());
    }

    @Test
    void newSurveyInvalid_AnswerNameIsEmpty() {
        SurveyRequest survey = SurveyRequest.builder()
                .name("Test survey name " + UUID.randomUUID())
                .description("Test survey description " + UUID.randomUUID())
                .questions(Collections.singletonList(
                        QuestionRequest.builder()
                                .name("Test question name " + UUID.randomUUID())
                                .description("Test question description " + UUID.randomUUID())
                                .answers(Arrays.asList(
                                        AnswerRequest.builder().name("Answer 1").build(),
                                        AnswerRequest.builder().name("Answer 2").build(),
                                        AnswerRequest.builder().name("").build()
                                ))
                                .build()))

                .build();

        SurveyRequestValidationException thrown = assertThrows(SurveyRequestValidationException.class, () -> {
            Optional<SurveyResponse> response = surveyService.newSurvey(survey);
            assertTrue(response.isPresent());
        }, "SurveyRequestValidationException exception was expected");
        assertEquals("Answer name is not defined", thrown.getMessage());
    }

    @Test
    void deleteSurveyQuestion() {
        SurveyRequest survey = SurveyRequest.builder()
                .name("Test survey name " + UUID.randomUUID())
                .description("Test survey description " + UUID.randomUUID())
                .questions(Arrays.asList(
                        QuestionRequest.builder()
                                .name("Test question name " + UUID.randomUUID())
                                .description("Test question description " + UUID.randomUUID())
                                .answers(Arrays.asList(
                                        AnswerRequest.builder().name("Answer 1").build(),
                                        AnswerRequest.builder().name("Answer 2").build(),
                                        AnswerRequest.builder().name("Answer 3").build()
                                ))
                                .build(),
                        QuestionRequest.builder()
                                .name("Test question name " + UUID.randomUUID())
                                .description("Test question description " + UUID.randomUUID())
                                .answers(Arrays.asList(
                                        AnswerRequest.builder().name("Answer 4").build(),
                                        AnswerRequest.builder().name("Answer 5").build(),
                                        AnswerRequest.builder().name("Answer 6").build()
                                ))
                                .build()
                ))
                .build();

        Optional<SurveyResponse> response = surveyService.newSurvey(survey);
        assertTrue(response.isPresent());

        Optional<SurveyResponse> newSurvey = surveyService.getSurvey(response.get().getId());
        assertTrue(newSurvey.isPresent());

        assertEquals(2, newSurvey.get().getQuestions().size());

        UUID surveyId = newSurvey.get().getId();
        UUID questionId = newSurvey.get().getQuestions().get(0).getId();

        response = surveyService.deleteSurveyQuestion(surveyId, questionId);
        assertTrue(response.isPresent());

        assertEquals(1, response.get().getQuestions().size());

        newSurvey = surveyService.getSurvey(surveyId);
        assertTrue(newSurvey.isPresent());
        assertEquals(1, newSurvey.get().getQuestions().size());
        assertFalse(newSurvey.get().getQuestions().stream().anyMatch(q -> q.getId().equals(questionId)));
    }
}