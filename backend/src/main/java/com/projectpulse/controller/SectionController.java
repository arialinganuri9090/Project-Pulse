package com.projectpulse.controller;

import com.projectpulse.dto.request.CreateSectionRequest;
import com.projectpulse.dto.request.SetupActiveWeeksRequest;
import com.projectpulse.model.ActiveWeek;
import com.projectpulse.model.Section;
import com.projectpulse.service.SectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService sectionService;

    @Autowired
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Section> createSection(@Valid @RequestBody CreateSectionRequest request) {
        return ResponseEntity.ok(sectionService.createSection(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<Section>> searchSections(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(sectionService.searchSections(name));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Section> getSection(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getSection(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Section> updateSection(@PathVariable Long id, @Valid @RequestBody CreateSectionRequest request) {
        return ResponseEntity.ok(sectionService.updateSection(id, request));
    }

    @PostMapping("/{id}/active-weeks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setupActiveWeeks(@PathVariable Long id, @RequestBody SetupActiveWeeksRequest request) {
        sectionService.setupActiveWeeks(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/active-weeks")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<List<ActiveWeek>> getActiveWeeks(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getActiveWeeks(id));
    }
}
