package com.projectpulse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "peer_evaluations", uniqueConstraints = @UniqueConstraint(columnNames = {"evaluator_id", "evaluatee_id", "active_week_id"}))
public class PeerEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    private User evaluator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluatee_id", nullable = false)
    private User evaluatee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_week_id", nullable = false)
    private ActiveWeek activeWeek;

    @Column(columnDefinition = "TEXT")
    private String publicComment;

    @Column(columnDefinition = "TEXT")
    private String privateComment;

    private LocalDateTime submittedAt;

    @Column(nullable = false)
    private boolean completed = false;

    @OneToMany(mappedBy = "peerEvaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PeerEvaluationScore> scores = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getEvaluator() { return evaluator; }
    public void setEvaluator(User evaluator) { this.evaluator = evaluator; }
    public User getEvaluatee() { return evaluatee; }
    public void setEvaluatee(User evaluatee) { this.evaluatee = evaluatee; }
    public ActiveWeek getActiveWeek() { return activeWeek; }
    public void setActiveWeek(ActiveWeek activeWeek) { this.activeWeek = activeWeek; }
    public String getPublicComment() { return publicComment; }
    public void setPublicComment(String publicComment) { this.publicComment = publicComment; }
    public String getPrivateComment() { return privateComment; }
    public void setPrivateComment(String privateComment) { this.privateComment = privateComment; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public List<PeerEvaluationScore> getScores() { return scores; }
    public void setScores(List<PeerEvaluationScore> scores) { this.scores = scores; }
}
