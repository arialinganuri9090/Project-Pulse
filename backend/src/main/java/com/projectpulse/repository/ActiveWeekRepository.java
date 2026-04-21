package com.projectpulse.repository;

import com.projectpulse.model.ActiveWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ActiveWeekRepository extends JpaRepository<ActiveWeek, Long> {
    List<ActiveWeek> findBySectionIdOrderByWeekStartAsc(Long sectionId);
    List<ActiveWeek> findBySectionIdAndActiveTrueOrderByWeekStartAsc(Long sectionId);

    @Query("SELECT aw FROM ActiveWeek aw WHERE aw.section.id = :sectionId AND aw.weekStart <= :date AND aw.weekEnd >= :date")
    Optional<ActiveWeek> findBySectionIdAndDate(@Param("sectionId") Long sectionId, @Param("date") LocalDate date);

    @Query("SELECT aw FROM ActiveWeek aw JOIN Team t ON t.section.id = aw.section.id JOIN t.students s WHERE s.id = :studentId AND aw.active = true ORDER BY aw.weekStart ASC")
    List<ActiveWeek> findActiveWeeksByStudentId(@Param("studentId") Long studentId);

    @Modifying
    @Query("DELETE FROM ActiveWeek aw WHERE aw.section.id = :sectionId")
    void deleteBySectionId(@Param("sectionId") Long sectionId);
}
