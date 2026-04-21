package com.projectpulse.controller;

import com.projectpulse.dto.request.CreateRubricRequest;
import com.projectpulse.model.Rubric;
import com.projectpulse.service.RubricService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rubrics")
public class RubricController {

    private final RubricService rubricService;

    @Autowired
    public RubricController(RubricService rubricService) {
        this.rubricService = rubricService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Rubric> createRubric(@Valid @RequestBody CreateRubricRequest request) {
        return ResponseEntity.ok(rubricService.createRubric(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<Rubric>> getAllRubrics() {
        return ResponseEntity.ok(rubricService.getAllRubrics());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Rubric> getRubric(@PathVariable Long id) {
        return ResponseEntity.ok(rubricService.getRubric(id));
    }
}
