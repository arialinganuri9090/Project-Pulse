package com.projectpulse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wars", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "active_week_id"}))
public class War {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "active_week_id", nullable = false)
    private ActiveWeek activeWeek;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "war", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WarActivity> activities = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public ActiveWeek getActiveWeek() { return activeWeek; }
    public void setActiveWeek(ActiveWeek activeWeek) { this.activeWeek = activeWeek; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<WarActivity> getActivities() { return activities; }
    public void setActivities(List<WarActivity> activities) { this.activities = activities; }
}
