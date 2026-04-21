package com.projectpulse.dto.request;

import com.projectpulse.model.enums.ActivityCategory;
import com.projectpulse.model.enums.ActivityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WarActivityRequest {
    @NotNull
    private ActivityCategory category;
    @NotBlank
    private String activity;
    private String description;
    private Double plannedHours;
    private Double actualHours;
    @NotNull
    private ActivityStatus status;

    public ActivityCategory getCategory() { return category; }
    public void setCategory(ActivityCategory category) { this.category = category; }
    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPlannedHours() { return plannedHours; }
    public void setPlannedHours(Double plannedHours) { this.plannedHours = plannedHours; }
    public Double getActualHours() { return actualHours; }
    public void setActualHours(Double actualHours) { this.actualHours = actualHours; }
    public ActivityStatus getStatus() { return status; }
    public void setStatus(ActivityStatus status) { this.status = status; }
}
