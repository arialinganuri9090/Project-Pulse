package com.projectpulse.controller;

import com.projectpulse.dto.request.InviteRequest;
import com.projectpulse.model.User;
import com.projectpulse.model.enums.Role;
import com.projectpulse.repository.UserRepository;
import com.projectpulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public StudentController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<User>> searchStudents(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email) {
        return ResponseEntity.ok(userService.searchStudents(firstName, lastName, email));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<User> getStudent(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> inviteStudents(@RequestBody InviteRequest request) {
        List<String> sent = userService.inviteUsers(request, Role.STUDENT);
        return ResponseEntity.ok(Map.of("sent", sent, "count", sent.size()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<User> updateMyAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody java.util.Map<String, String> body) {
        User me = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User updated = userService.updateUser(me.getId(), body.get("firstName"), body.get("lastName"), body.get("email"));
        return ResponseEntity.ok(updated);
    }
}
