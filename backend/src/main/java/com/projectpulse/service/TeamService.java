package com.projectpulse.service;

import com.projectpulse.dto.request.AssignUsersRequest;
import com.projectpulse.dto.request.CreateTeamRequest;
import com.projectpulse.model.Section;
import com.projectpulse.model.Team;
import com.projectpulse.model.User;
import com.projectpulse.repository.SectionRepository;
import com.projectpulse.repository.TeamRepository;
import com.projectpulse.repository.UserRepository;
import com.projectpulse.repository.WarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final WarRepository warRepository;
    private final EmailService emailService;

    @Autowired
    public TeamService(TeamRepository teamRepository, SectionRepository sectionRepository,
                       UserRepository userRepository, WarRepository warRepository,
                       EmailService emailService) {
        this.teamRepository = teamRepository;
        this.sectionRepository = sectionRepository;
        this.userRepository = userRepository;
        this.warRepository = warRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Team createTeam(CreateTeamRequest request) {
        if (teamRepository.existsByName(request.getName())) {
            throw new RuntimeException("Team name already exists");
        }
        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found"));
        Team team = new Team();
        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setWebsiteUrl(request.getWebsiteUrl());
        team.setSection(section);
        return teamRepository.save(team);
    }

    public List<Team> searchTeams(Long sectionId, String name) {
        return teamRepository.search(sectionId, name);
    }

    public Team getTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
    }

    @Transactional
    public Team updateTeam(Long id, CreateTeamRequest request) {
        Team team = getTeam(id);
        if (!team.getName().equals(request.getName()) && teamRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new RuntimeException("Team name already exists");
        }
        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setWebsiteUrl(request.getWebsiteUrl());
        return teamRepository.save(team);
    }

    @Transactional
    public void deleteTeam(Long id) {
        Team team = getTeam(id);
        String teamName = team.getName();
        for (User student : team.getStudents()) {
            emailService.sendTeamDeletionNotification(student.getEmail(), teamName, student.getFirstName());
        }
        for (User instructor : team.getInstructors()) {
            emailService.sendTeamDeletionNotification(instructor.getEmail(), teamName, instructor.getFirstName());
        }
        teamRepository.delete(team);
    }

    @Transactional
    public void assignStudents(AssignUsersRequest request) {
        Team team = getTeam(request.getTeamId());
        for (Long userId : request.getUserIds()) {
            User student = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Student not found: " + userId));
            team.getStudents().add(student);
            emailService.sendTeamAssignmentNotification(student.getEmail(), team.getName(), student.getFirstName());
        }
        teamRepository.save(team);
    }

    @Transactional
    public void removeStudent(Long teamId, Long studentId) {
        Team team = getTeam(teamId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        team.getStudents().remove(student);
        teamRepository.save(team);
        emailService.sendTeamRemovalNotification(student.getEmail(), team.getName(), student.getFirstName());
    }

    @Transactional
    public void assignInstructors(AssignUsersRequest request) {
        Team team = getTeam(request.getTeamId());
        for (Long userId : request.getUserIds()) {
            User instructor = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Instructor not found: " + userId));
            team.getInstructors().add(instructor);
            emailService.sendTeamAssignmentNotification(instructor.getEmail(), team.getName(), instructor.getFirstName());
        }
        teamRepository.save(team);
    }

    @Transactional
    public void removeInstructor(Long teamId, Long instructorId) {
        Team team = getTeam(teamId);
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        team.getInstructors().remove(instructor);
        teamRepository.save(team);
        emailService.sendTeamRemovalNotification(instructor.getEmail(), team.getName(), instructor.getFirstName());
    }
}
