package com.projectpulse.repository;

import com.projectpulse.model.War;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WarRepository extends JpaRepository<War, Long> {
    Optional<War> findByStudentIdAndActiveWeekId(Long studentId, Long activeWeekId);
    List<War> findByStudentIdOrderByActiveWeekWeekStartDesc(Long studentId);
    List<War> findByActiveWeekId(Long activeWeekId);
}
