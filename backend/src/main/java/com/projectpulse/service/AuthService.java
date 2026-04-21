package com.projectpulse.service;

import com.projectpulse.dto.request.LoginRequest;
import com.projectpulse.dto.request.RegisterRequest;
import com.projectpulse.dto.response.AuthResponse;
import com.projectpulse.model.Invitation;
import com.projectpulse.model.User;
import com.projectpulse.model.enums.UserStatus;
import com.projectpulse.repository.InvitationRepository;
import com.projectpulse.repository.UserRepository;
import com.projectpulse.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(UserRepository userRepository, InvitationRepository invitationRepository,
                       PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new RuntimeException("Account is deactivated");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getRole().name(), user.getId(),
                user.getFirstName(), user.getLastName(), user.getEmail());
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Invitation invitation = invitationRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if (invitation.isUsed()) {
            throw new RuntimeException("Invitation already used");
        }
        if (invitation.getExpiresAt() != null && invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invitation has expired");
        }
        if (userRepository.existsByEmail(invitation.getEmail())) {
            throw new RuntimeException("Account already exists for this email");
        }

        User user = new User();
        user.setEmail(invitation.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleInitial(request.getMiddleInitial());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(invitation.getRole());
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        invitation.setUsed(true);
        invitationRepository.save(invitation);

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getRole().name(), user.getId(),
                user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public AuthResponse me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getRole().name(), user.getId(),
                user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public Invitation validateInvitation(String token) {
        return invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired invitation token"));
    }
}
