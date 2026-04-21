package com.projectpulse.dto.request;

import java.util.List;
import java.util.Map;

public class PeerEvalSubmitRequest {
    private List<EvaluationEntry> evaluations;

    public List<EvaluationEntry> getEvaluations() { return evaluations; }
    public void setEvaluations(List<EvaluationEntry> evaluations) { this.evaluations = evaluations; }

    public static class EvaluationEntry {
        private Long evaluateeId;
        private Map<Long, Integer> scores;
        private String publicComment;
        private String privateComment;

        public Long getEvaluateeId() { return evaluateeId; }
        public void setEvaluateeId(Long evaluateeId) { this.evaluateeId = evaluateeId; }
        public Map<Long, Integer> getScores() { return scores; }
        public void setScores(Map<Long, Integer> scores) { this.scores = scores; }
        public String getPublicComment() { return publicComment; }
        public void setPublicComment(String publicComment) { this.publicComment = publicComment; }
        public String getPrivateComment() { return privateComment; }
        public void setPrivateComment(String privateComment) { this.privateComment = privateComment; }
    }
}
