package com.projectpulse.controller;

import com.projectpulse.dto.request.LoginRequest;
import com.projectpulse.dto.request.RegisterRequest;
import com.projectpulse.dto.response.AuthResponse;
import com.projectpulse.model.Invitation;
import com.projectpulse.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.me(userDetails.getUsername()));
    }

    @GetMapping("/invitation/{token}")
    public ResponseEntity<Map<String, Object>> validateInvitation(@PathVariable String token) {
        Invitation inv = authService.validateInvitation(token);
        return ResponseEntity.ok(Map.of(
                "email", inv.getEmail(),
                "role", inv.getRole().name(),
                "valid", !inv.isUsed()
        ));
    }
}
