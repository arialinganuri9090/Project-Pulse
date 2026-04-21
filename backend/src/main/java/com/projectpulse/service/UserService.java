package com.projectpulse.service;

import com.projectpulse.dto.request.InviteRequest;
import com.projectpulse.model.Invitation;
import com.projectpulse.model.Section;
import com.projectpulse.model.User;
import com.projectpulse.model.enums.Role;
import com.projectpulse.model.enums.UserStatus;
import com.projectpulse.repository.InvitationRepository;
import com.projectpulse.repository.SectionRepository;
import com.projectpulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final SectionRepository sectionRepository;
    private final EmailService emailService;

    @Value("${app.admin-email}")
    private String adminEmail;

    @Value("${app.admin-first-name}")
    private String adminFirstName;

    @Value("${app.admin-last-name}")
    private String adminLastName;

    @Autowired
    public UserService(UserRepository userRepository, InvitationRepository invitationRepository,
                       SectionRepository sectionRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
        this.sectionRepository = sectionRepository;
        this.emailService = emailService;
    }

    public List<User> searchStudents(String firstName, String lastName, String email) {
        return userRepository.searchByRole(Role.STUDENT, firstName, lastName, email);
    }

    public List<User> searchInstructors(String firstName, String lastName, UserStatus status) {
        return userRepository.searchInstructors(Role.INSTRUCTOR, firstName, lastName, status);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUser(id);
        userRepository.delete(user);
    }

    @Transactional
    public void deactivateInstructor(Long id) {
        User user = getUser(id);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    @Transactional
    public void reactivateInstructor(Long id) {
        User user = getUser(id);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        emailService.sendAccountReactivated(user.getEmail(), user.getFirstName());
    }

    @Transactional
    public List<String> inviteUsers(InviteRequest request, Role role) {
        String[] emails = request.getEmails().split(";");
        List<String> sent = new ArrayList<>();

        Section section = null;
        if (request.getSectionId() != null) {
            section = sectionRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new RuntimeException("Section not found"));
        }

        for (String rawEmail : emails) {
            String email = rawEmail.trim();
            if (email.isEmpty()) continue;

            String token = UUID.randomUUID().toString();
            Invitation invitation = new Invitation();
            invitation.setEmail(email);
            invitation.setToken(token);
            invitation.setRole(role);
            invitation.setSection(section);
            invitation.setExpiresAt(LocalDateTime.now().plusDays(30));
            invitationRepository.save(invitation);

            emailService.sendInvitation(email, token, adminFirstName + " " + adminLastName,
                    adminEmail, role.name(), request.getCustomMessage());
            sent.add(email);
        }
        return sent;
    }

    @Transactional
    public User updateUser(Long id, String firstName, String lastName, String email) {
        User user = getUser(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already in use");
        }
        user.setEmail(email);
        return userRepository.save(user);
    }
}
