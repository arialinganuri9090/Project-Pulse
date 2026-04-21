package com.projectpulse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectpulse.model.enums.ActivityCategory;
import com.projectpulse.model.enums.ActivityStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "war_activities")
public class WarActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "war_id", nullable = false)
    private War war;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityCategory category;

    @Column(nullable = false)
    private String activity;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double plannedHours;
    private Double actualHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public War getWar() { return war; }
    public void setWar(War war) { this.war = war; }
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
