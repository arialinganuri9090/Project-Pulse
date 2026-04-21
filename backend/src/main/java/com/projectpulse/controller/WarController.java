package com.projectpulse.controller;

import com.projectpulse.dto.request.WarActivityRequest;
import com.projectpulse.model.ActiveWeek;
import com.projectpulse.model.War;
import com.projectpulse.model.WarActivity;
import com.projectpulse.repository.UserRepository;
import com.projectpulse.service.WarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wars")
public class WarController {

    private final WarService warService;
    private final UserRepository userRepository;

    @Autowired
    public WarController(WarService warService, UserRepository userRepository) {
        this.warService = warService;
        this.userRepository = userRepository;
    }

    @GetMapping("/weeks")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ActiveWeek>> getWeeks(@AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getUserId(userDetails);
        return ResponseEntity.ok(warService.getWeeksForStudent(studentId));
    }

    @GetMapping("/{weekId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ResponseEntity<War> getWar(@PathVariable Long weekId, @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getUserId(userDetails);
        War war = warService.getWarForStudentAndWeek(studentId, weekId);
        return war != null ? ResponseEntity.ok(war) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{weekId}/activities")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<WarActivity> addActivity(
            @PathVariable Long weekId,
            @Valid @RequestBody WarActivityRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getUserId(userDetails);
        return ResponseEntity.ok(warService.addActivity(studentId, weekId, request));
    }

    @PutMapping("/{weekId}/activities/{activityId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<WarActivity> updateActivity(
            @PathVariable Long weekId,
            @PathVariable Long activityId,
            @Valid @RequestBody WarActivityRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getUserId(userDetails);
        return ResponseEntity.ok(warService.updateActivity(studentId, weekId, activityId, request));
    }

    @DeleteMapping("/{weekId}/activities/{activityId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteActivity(
            @PathVariable Long weekId,
            @PathVariable Long activityId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getUserId(userDetails);
        warService.deleteActivity(studentId, weekId, activityId);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
