package com.projectpulse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "peer_evaluation_scores")
public class PeerEvaluationScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peer_evaluation_id", nullable = false)
    private PeerEvaluation peerEvaluation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "criterion_id", nullable = false)
    private RubricCriterion criterion;

    @Column(nullable = false)
    private Integer score;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PeerEvaluation getPeerEvaluation() { return peerEvaluation; }
    public void setPeerEvaluation(PeerEvaluation peerEvaluation) { this.peerEvaluation = peerEvaluation; }
    public RubricCriterion getCriterion() { return criterion; }
    public void setCriterion(RubricCriterion criterion) { this.criterion = criterion; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}
