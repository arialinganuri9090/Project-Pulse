package com.projectpulse.service;

import com.projectpulse.dto.request.PeerEvalSubmitRequest;
import com.projectpulse.model.*;
import com.projectpulse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PeerEvalService {

    private final PeerEvaluationRepository peerEvalRepository;
    private final ActiveWeekRepository activeWeekRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final RubricRepository rubricRepository;

    @Autowired
    public PeerEvalService(PeerEvaluationRepository peerEvalRepository, ActiveWeekRepository activeWeekRepository,
                           UserRepository userRepository, TeamRepository teamRepository,
                           RubricRepository rubricRepository) {
        this.peerEvalRepository = peerEvalRepository;
        this.activeWeekRepository = activeWeekRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.rubricRepository = rubricRepository;
    }

    @Transactional
    public void submitPeerEval(Long evaluatorId, Long weekId, PeerEvalSubmitRequest request) {
        ActiveWeek week = activeWeekRepository.findById(weekId)
                .orElseThrow(() -> new RuntimeException("Week not found"));

        // BR-4: can only submit for previous week
        LocalDate today = LocalDate.now();
        if (!today.isAfter(week.getWeekEnd())) {
            throw new RuntimeException("You can only submit peer evaluation for the previous week");
        }

        for (PeerEvalSubmitRequest.EvaluationEntry entry : request.getEvaluations()) {
            PeerEvaluation existing = peerEvalRepository
                    .findByEvaluatorIdAndEvaluateeIdAndActiveWeekId(evaluatorId, entry.getEvaluateeId(), weekId)
                    .orElse(null);

            // BR-3: cannot edit once completed
            if (existing != null && existing.isCompleted()) {
                throw new RuntimeException("Peer evaluation already completed and cannot be edited");
            }

            PeerEvaluation eval = existing != null ? existing : new PeerEvaluation();
            eval.setEvaluator(userRepository.findById(evaluatorId).orElseThrow());
            eval.setEvaluatee(userRepository.findById(entry.getEvaluateeId()).orElseThrow());
            eval.setActiveWeek(week);
            eval.setPublicComment(entry.getPublicComment());
            eval.setPrivateComment(entry.getPrivateComment());
            eval.setSubmittedAt(LocalDateTime.now());
            eval.setCompleted(true);
            eval.getScores().clear();

            if (entry.getScores() != null) {
                for (Map.Entry<Long, Integer> scoreEntry : entry.getScores().entrySet()) {
                    RubricCriterion criterion = new RubricCriterion();
                    criterion.setId(scoreEntry.getKey());
                    PeerEvaluationScore score = new PeerEvaluationScore();
                    score.setPeerEvaluation(eval);
                    score.setCriterion(criterion);
                    score.setScore(scoreEntry.getValue());
                    eval.getScores().add(score);
                }
            }
            peerEvalRepository.save(eval);
        }
    }

    public List<PeerEvaluation> getEvaluationsForEvaluator(Long evaluatorId, Long weekId) {
        return peerEvalRepository.findByEvaluatorIdAndActiveWeekId(evaluatorId, weekId);
    }

    public List<PeerEvaluation> getEvaluationsForEvaluatee(Long evaluateeId, Long weekId) {
        return peerEvalRepository.findByEvaluateeIdAndActiveWeekId(evaluateeId, weekId);
    }

    public List<ActiveWeek> getPeerEvalWeeksForStudent(Long studentId) {
        return activeWeekRepository.findActiveWeeksByStudentId(studentId);
    }
}
