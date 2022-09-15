package com.marketlogic.surveychallenge.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyRequest {
    private String name;
    private String description;
    @Builder.Default
    private List<QuestionRequest> questions = new ArrayList<>();
}
