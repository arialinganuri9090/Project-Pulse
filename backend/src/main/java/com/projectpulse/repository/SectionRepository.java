package com.projectpulse.repository;

import com.projectpulse.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    boolean existsByName(String name);

    List<Section> findAllByOrderByNameDesc();

    @Query("SELECT s FROM Section s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY s.name DESC")
    List<Section> searchByName(@Param("name") String name);
}
