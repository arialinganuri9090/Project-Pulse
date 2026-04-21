package com.projectpulse.repository;

import com.projectpulse.model.PeerEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PeerEvaluationRepository extends JpaRepository<PeerEvaluation, Long> {
    Optional<PeerEvaluation> findByEvaluatorIdAndEvaluateeIdAndActiveWeekId(Long evaluatorId, Long evaluateeId, Long activeWeekId);
    List<PeerEvaluation> findByEvaluateeIdAndActiveWeekId(Long evaluateeId, Long activeWeekId);
    List<PeerEvaluation> findByEvaluatorIdAndActiveWeekId(Long evaluatorId, Long activeWeekId);

    @Query("SELECT pe FROM PeerEvaluation pe WHERE pe.evaluatee.id = :evaluateeId AND pe.activeWeek.id IN :weekIds")
    List<PeerEvaluation> findByEvaluateeIdAndWeekIds(@Param("evaluateeId") Long evaluateeId, @Param("weekIds") List<Long> weekIds);

    @Query("SELECT DISTINCT pe.evaluator.id FROM PeerEvaluation pe WHERE pe.activeWeek.id = :weekId AND pe.evaluator.id IN :studentIds AND pe.completed = true")
    List<Long> findSubmittedEvaluatorIdsByWeekIdAndStudentIds(@Param("weekId") Long weekId, @Param("studentIds") List<Long> studentIds);
}
