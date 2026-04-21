package com.projectpulse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectpulse.model.enums.Role;
import com.projectpulse.model.enums.UserStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String middleInitial;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonIgnore
    @ManyToMany(mappedBy = "students")
    private Set<Team> studentTeams = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "instructors")
    private Set<Team> instructorTeams = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getMiddleInitial() { return middleInitial; }
    public void setMiddleInitial(String middleInitial) { this.middleInitial = middleInitial; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Set<Team> getStudentTeams() { return studentTeams; }
    public void setStudentTeams(Set<Team> studentTeams) { this.studentTeams = studentTeams; }
    public Set<Team> getInstructorTeams() { return instructorTeams; }
    public void setInstructorTeams(Set<Team> instructorTeams) { this.instructorTeams = instructorTeams; }
}
