package com.projectpulse.controller;

import com.projectpulse.dto.request.InviteRequest;
import com.projectpulse.model.User;
import com.projectpulse.model.enums.Role;
import com.projectpulse.model.enums.UserStatus;
import com.projectpulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    private final UserService userService;

    @Autowired
    public InstructorController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> searchInstructors(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) UserStatus status) {
        return ResponseEntity.ok(userService.searchInstructors(firstName, lastName, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getInstructor(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PostMapping("/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> inviteInstructors(@RequestBody InviteRequest request) {
        List<String> sent = userService.inviteUsers(request, Role.INSTRUCTOR);
        return ResponseEntity.ok(Map.of("sent", sent, "count", sent.size()));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        userService.deactivateInstructor(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reactivate(@PathVariable Long id) {
        userService.reactivateInstructor(id);
        return ResponseEntity.ok().build();
    }
}
