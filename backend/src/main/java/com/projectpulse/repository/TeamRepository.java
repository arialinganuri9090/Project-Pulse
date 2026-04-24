package com.projectpulse.repository;

import com.projectpulse.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);

    List<Team> findAllByOrderByNameAsc();

    @Query("SELECT t FROM Team t WHERE t.section.id = :sectionId ORDER BY t.name ASC")
    List<Team> findBySectionId(@Param("sectionId") Long sectionId);

    @Query("SELECT t FROM Team t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY t.name ASC")
    List<Team> findByNameContaining(@Param("name") String name);

    @Query("SELECT t FROM Team t WHERE t.section.id = :sectionId AND LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY t.name ASC")
    List<Team> findBySectionIdAndNameContaining(@Param("sectionId") Long sectionId, @Param("name") String name);

    @Query("SELECT t FROM Team t JOIN t.students s WHERE s.id = :studentId")
    List<Team> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT t FROM Team t JOIN t.instructors i WHERE i.id = :instructorId")
    List<Team> findByInstructorId(@Param("instructorId") Long instructorId);
}
