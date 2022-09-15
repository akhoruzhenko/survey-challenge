package com.marketlogic.surveychallenge.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "SurveyEntity")
@Table(name = "surveys")
public class SurveyEntity {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Version
    private long version;

    @Column(name = "name", columnDefinition = "text")
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="survey_id")
    private List<QuestionEntity> questions = new ArrayList<>();
}
