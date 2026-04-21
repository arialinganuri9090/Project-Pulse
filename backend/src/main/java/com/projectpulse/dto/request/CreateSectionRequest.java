package com.projectpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class CreateSectionRequest {
    @NotBlank
    private String name;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private Long rubricId;
    private List<CreateRubricRequest.CriterionDto> rubricCriteriaOverrides;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Long getRubricId() { return rubricId; }
    public void setRubricId(Long rubricId) { this.rubricId = rubricId; }
    public List<CreateRubricRequest.CriterionDto> getRubricCriteriaOverrides() { return rubricCriteriaOverrides; }
    public void setRubricCriteriaOverrides(List<CreateRubricRequest.CriterionDto> rubricCriteriaOverrides) { this.rubricCriteriaOverrides = rubricCriteriaOverrides; }
}
