package com.projectpulse.controller;

import com.projectpulse.dto.request.AssignUsersRequest;
import com.projectpulse.dto.request.CreateTeamRequest;
import com.projectpulse.model.Team;
import com.projectpulse.repository.UserRepository;
import com.projectpulse.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final UserRepository userRepository;

    @Autowired
    public TeamController(TeamService teamService, UserRepository userRepository) {
        this.teamService = teamService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Team> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.ok(teamService.createTeam(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<Team>> searchTeams(
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(teamService.searchTeams(sectionId, name));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<Team> getTeam(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeam(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Team> updateTeam(@PathVariable Long id, @Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.ok(teamService.updateTeam(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-team")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Team> getMyTeam(@AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        return teamService.getTeamForStudent(studentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/assign-students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignStudents(@RequestBody AssignUsersRequest request) {
        teamService.assignStudents(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{teamId}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeStudent(@PathVariable Long teamId, @PathVariable Long studentId) {
        teamService.removeStudent(teamId, studentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign-instructors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignInstructors(@RequestBody AssignUsersRequest request) {
        teamService.assignInstructors(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{teamId}/instructors/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeInstructor(@PathVariable Long teamId, @PathVariable Long instructorId) {
        teamService.removeInstructor(teamId, instructorId);
        return ResponseEntity.ok().build();
    }
}
