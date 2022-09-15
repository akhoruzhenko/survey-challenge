package com.marketlogic.surveychallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The survey request failed validation.")
public class SurveyRequestValidationException extends RuntimeException {
    private static final long serialVersionUID = 6688896795089587115L;

    public SurveyRequestValidationException(String message) {
        super(message);
    }
}
