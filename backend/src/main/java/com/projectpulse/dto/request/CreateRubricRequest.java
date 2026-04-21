package com.projectpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateRubricRequest {
    @NotBlank
    private String name;
    @NotEmpty
    private List<CriterionDto> criteria;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<CriterionDto> getCriteria() { return criteria; }
    public void setCriteria(List<CriterionDto> criteria) { this.criteria = criteria; }

    public static class CriterionDto {
        @NotBlank
        private String name;
        private String description;
        private Double maxScore;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Double getMaxScore() { return maxScore; }
        public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }
    }
}
