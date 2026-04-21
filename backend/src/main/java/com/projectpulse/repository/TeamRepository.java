package com.projectpulse.repository;

import com.projectpulse.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT t FROM Team t WHERE " +
           "(:sectionId IS NULL OR t.section.id = :sectionId) AND " +
           "(:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Team> search(@Param("sectionId") Long sectionId, @Param("name") String name);

    @Query("SELECT t FROM Team t JOIN t.students s WHERE s.id = :studentId")
    List<Team> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT t FROM Team t JOIN t.instructors i WHERE i.id = :instructorId")
    List<Team> findByInstructorId(@Param("instructorId") Long instructorId);
}
